package chalmers.eda397_2016_group3.trello;

import android.util.Log;

import org.trello4j.model.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianblomberg on 2016-05-09.
 */
public class CardChangeUtils {
    public enum CardAttribute {
        BOARD_ID,
        LIST_ID,
        NAME,
        IS_CLOSED,
        POSITION,
        URL
    }
    private static void printCard(Card c) {
        Log.d("debug", "Card => name: " + c.getName() + ", board id: " + c.getIdBoard()
                + ", list id: " + c.getIdList() + ", is closed: " + c.isClosed()
                + ", position: " + c.getPos() + ", url: " + c.getUrl());
    }
    public static List<CardAttribute> getCardChange(Card oldCard, Card newCard) {
        List<CardAttribute> changed = new ArrayList<>();
        if(!oldCard.getIdBoard().equals(newCard.getIdBoard())) {
            changed.add(CardAttribute.BOARD_ID);
        }

        if(!oldCard.getIdList().equals(newCard.getIdList())) {

            changed.add(CardAttribute.LIST_ID);
        }

        if(!oldCard.getName().equals(newCard.getName())) {
            changed.add(CardAttribute.NAME);
        }

        if(oldCard.isClosed() != newCard.isClosed()) {
            changed.add(CardAttribute.IS_CLOSED);
        }

        if(oldCard.getPos() != newCard.getPos()) {
            changed.add(CardAttribute.POSITION);
        }

        if(!oldCard.getUrl().equals(newCard.getUrl())) {
            changed.add(CardAttribute.URL);
        }

        return changed;
    }
}
