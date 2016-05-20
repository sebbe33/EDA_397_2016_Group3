package chalmers.eda397_2016_group3.trello;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import org.trello4j.model.Card;

import java.util.ArrayList;
import java.util.List;

import chalmers.eda397_2016_group3.MainActivity;
import chalmers.eda397_2016_group3.R;
import chalmers.eda397_2016_group3.timer.FragmentTimer;

public class CardChangeNotifierService extends IntentService implements CardChangePoller.CardChangeListener {
    private static List<CardChange> cardChanges = new ArrayList<>();
    private static List<CardChangePoller.CardChangeListener> changeListeners = new ArrayList<>();
    private static final long DEFAULT_POLL_INTERVAL = 10000;

    private CardChangePoller cardChangePoller;

    public CardChangeNotifierService() {
        super("CardChangeNotifierService");
    }

    public static void registerCardChangeListener(CardChangePoller.CardChangeListener listener) {
        changeListeners.add(listener);
    }

    public static void removeCardChangeListener(CardChangePoller.CardChangeListener listener) {
        changeListeners.remove(listener);
    }

    public static List<CardChange> getCardChanges() {
        return cardChanges;
    }

    public static void clearCardChanges() {
        cardChanges.clear();
    }

    public static void removeCardChange(CardChange c) {
        cardChanges.remove(c);
    }

    private NotificationManager mNotifyMgr;

    @Override
    public void onCreate() {
        super.onCreate();
        registerCardChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeCardChangeListener(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TrelloApp trelloApp = TrelloAppService.getTrelloApp(this);

        if (trelloApp.getSelectedBoardID() == null || trelloApp.getSelectedBoardID().isEmpty()) {
            // We don't know which board to listen to.
            return;
        }

        TrelloImproved trelloAPIInterface = TrelloAppService.getTrelloAPIInterface(trelloApp);

        cardChangePoller = new CardChangePoller(trelloAPIInterface, trelloApp.getSelectedBoardID(), changeListeners);

        while (true) {
            Log.d("debug", "Polling...");
            cardChangePoller.poll();
            try {
                Thread.sleep(DEFAULT_POLL_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCardChange(Card oldCard, Card newCard) {
        Log.d("debug", "card change!");
        if(oldCard == null || newCard == null) {
            Log.d("debug", "Card change ignored: " + oldCard + " - " + newCard);
            return;
        }

        List<CardChangeUtils.CardAttribute> changedAttributes = CardChangeUtils.getCardChange(oldCard, newCard);
        if(!changedAttributes.contains(CardChangeUtils.CardAttribute.LIST_ID)) {
            Log.d("debug", "Card change ignored. Does not contain a change of list id");
            return;
        }

        final boolean showNotification = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("checkBoxTrello", true);

        if(!showNotification)
            return;

        // Stack the card change
        cardChanges.add(new CardChange(oldCard, newCard));

        String description = newCard.getName() + " moved to another list";





        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Card change")
                        .setContentText(description);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("StartFragment", R.id.navigation_trello_notifications_fragment);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );



        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        mBuilder.setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        mNotificationManager.notify(1, mBuilder.build());
    }



    public static class CardChange {
        private Card oldCard,newCard;
        public CardChange(Card oldCard, Card newCard) {
            this.oldCard = oldCard;
            this.newCard = newCard;
        }

        public Card getOldCard() {
            return oldCard;
        }

        public Card getNewCard() {
            return newCard;
        }

    }
}
