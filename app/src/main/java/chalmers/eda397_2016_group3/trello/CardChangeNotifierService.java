package chalmers.eda397_2016_group3.trello;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.trello4j.model.Card;

import java.util.ArrayList;
import java.util.List;

import chalmers.eda397_2016_group3.MainActivity;
import chalmers.eda397_2016_group3.R;

public class CardChangeNotifierService extends IntentService implements CardChangePoller.CardChangeListener {
    private static final long DEFAULT_POLL_INTERVAL = 10000;

    private List<CardChangePoller.CardChangeListener> listeners = null;
    private CardChangePoller cardChangePoller;

    public CardChangeNotifierService() {
        super("CardChangeNotifierService");
        listeners = new ArrayList<>();
        this.listeners.add(this);

    }

    public CardChangeNotifierService(String name, List<CardChangePoller.CardChangeListener> changeListeners) {
        super(name);
        this.listeners = new ArrayList<>(changeListeners);
        this.listeners.add(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TrelloApp trelloApp = TrelloAppService.getTrelloApp(this);

        if (trelloApp.getSelectedBoardID() == null || trelloApp.getSelectedBoardID().isEmpty()) {
            // We don't know which board to listen to.
            return;
        }

        TrelloImproved trelloAPIInterface = TrelloAppService.getTrelloAPIInterface(trelloApp);

        cardChangePoller = new CardChangePoller(trelloAPIInterface, trelloApp.getSelectedBoardID(), listeners);

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

        List<CardChaneUtils.CardAttribute> changedAttributes = CardChaneUtils.getCardChange(oldCard, newCard);
        if(!changedAttributes.contains(CardChaneUtils.CardAttribute.LIST_ID)) {
            Log.d("debug", "Card change ignored. Does not contain a change of list id");
            return;
        }

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
}
