package chalmers.eda397_2016_group3.trello;

import android.os.AsyncTask;
import android.os.Handler;

import org.trello4j.Trello;
import org.trello4j.model.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sebastianblomberg on 2016-05-09.
 */
public class CardChangeNotifier {
    private final static long DEFAULT_POLL_INTERVAL = 2000;

    private CardChangeListener listener;
    private long pollInterval;
    private String boardIDToTrack;
    private TrelloImproved trelloAPI;

    private Map<String, Card> oldState;
    private Handler handler = new Handler();
    private boolean isRunning = false;

    public CardChangeNotifier(TrelloImproved trelloAPI, String boardIDToTrack, CardChangeListener listener) {
        this(trelloAPI, boardIDToTrack, listener, DEFAULT_POLL_INTERVAL);
    }

    public CardChangeNotifier(TrelloImproved trelloAPI, String boardIDToTrack, CardChangeListener listener, long pollInterval) {
        if(trelloAPI == null) {
            throw new NullPointerException("trelloAPI cannot not be null");
        }
        if(boardIDToTrack == null || boardIDToTrack.isEmpty()) {
            throw new NullPointerException("boardIDToTrack cannot not be null or empty");
        }
        if(listener == null) {
            throw new NullPointerException("listener cannot not be null");
        }
        if(pollInterval <= 0) {
            throw new IllegalArgumentException("pollInterval cannot be negative or 0");
        }
        this.listener = listener;
        this.boardIDToTrack = boardIDToTrack;
        this.pollInterval = pollInterval;
        this.trelloAPI = trelloAPI;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void run() {
        // Stop old fetcher if it is running
        if(isRunning) {
            stop();
            // Give some time for the current task to finish
            handler.postAtTime(cardFetcherTask, pollInterval);
        } else {
            handler.post(cardFetcherTask);
        }
        isRunning = true;

    }

    public void stop() {
        isRunning = false;
        handler.removeCallbacks(cardFetcherTask);
    }

    private void compareAndNotifyOfChange(Map<String, Card> newState) {
        if(oldState == null) {
            // First time, no change can be noticed since there is nothing to compare to
            return;
        }
        // Compare existing cards
        for(Card oldCard : oldState.values()) {
            Card newCard = newState.get(oldCard.getId());
            if(newCard == null) {
                // Card has been removed
                listener.onCardChange(oldCard, null);
            } else if(!cardEquals(oldCard, newCard)) {
                listener.onCardChange(oldCard, newCard);
            }
        }

        // Check for new cards
        if(newState.size() > oldState.size()) {
            for(Card newCard : newState.values()) {
                Card oldCard = newState.get(newCard.getId());
                if(oldCard == null) {
                    // newCard has been added
                    listener.onCardChange(null, newCard);
                }
            }
        }
    }

    private boolean cardEquals(Card c1, Card c2) {
        return c1.getDesc().equals(c2.getDesc()) && c1.getIdBoard().equals(c2.getIdBoard())
                && c1.getIdList().equals(c2.getIdList()) && c1.getName().equals(c2.getName())
                && c1.isClosed() == c2.isClosed() && c1.getIdMembers().equals(c2.getIdMembers())
                && c1.getPos() == c2.getPos() && c1.getLabels().equals(c2.getLabels())
                && c1.getUrl().equals(c1.getUrl());

    }

    public interface CardChangeListener {
        void onCardChange(Card oldCard, Card newCard);
    }

    private Runnable cardFetcherTask = new Runnable() {
        @Override
        public void run() {
            List<Card> newCards = trelloAPI.getCardsByBoard(boardIDToTrack);
            Map<String, Card> newState = new HashMap<>(newCards.size());
            for(Card c : newCards) {
                newState.put(c.getId(), c);
            }
            compareAndNotifyOfChange(newState);
            if(isRunning) {
                handler.postDelayed(cardFetcherTask, pollInterval);
            }
        }
    };
}
