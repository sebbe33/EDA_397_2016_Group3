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
public class CardChangePoller {

    private List<CardChangeListener> listeners;
    private String boardIDToTrack;
    private TrelloImproved trelloAPI;

    private Map<String, Card> oldState;
    private boolean isRunning = false;


    public CardChangePoller(TrelloImproved trelloAPI, String boardIDToTrack, List<CardChangeListener> listeners) {
        if(trelloAPI == null) {
            throw new NullPointerException("trelloAPI cannot not be null");
        }
        if(boardIDToTrack == null || boardIDToTrack.isEmpty()) {
            throw new NullPointerException("boardIDToTrack cannot not be null or empty");
        }
        if(listeners == null || listeners.isEmpty()) {
            throw new NullPointerException("listeners cannot not be null or empty");
        }
        this.listeners = listeners;
        this.boardIDToTrack = boardIDToTrack;
        this.trelloAPI = trelloAPI;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean poll() {
        if(isRunning) {
            return false;
        }
        isRunning = true;
        new Thread(cardFetcherTask).start();

        return true;
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
                notifyChangeHelper(oldCard, null);
            } else if(!cardEquals(oldCard, newCard)) {
                notifyChangeHelper(oldCard, newCard);
            }
        }

        // Check for new cards
        if(newState.size() > oldState.size()) {
            for(Card newCard : newState.values()) {
                Card oldCard = newState.get(newCard.getId());
                if(oldCard == null) {
                    // newCard has been added
                    notifyChangeHelper(null, newCard);
                }
            }
        }
    }

    private void notifyChangeHelper(Card oldCard, Card newCard) {
        for(CardChangeListener l : listeners) {
            l.onCardChange(oldCard, newCard);
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
            isRunning = false;
            oldState = newState;
        }
    };
}
