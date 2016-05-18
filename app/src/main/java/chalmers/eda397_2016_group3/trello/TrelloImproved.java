package chalmers.eda397_2016_group3.trello;

import org.trello4j.Trello;
import org.trello4j.model.Card;
import org.trello4j.model.Checklist;

/**
 * Created by sebastianblomberg on 2016-05-02.
 */
public interface TrelloImproved extends Trello {
    void updateCardDescriptor(CardDescriptor card);
    Checklist createChecklistInCard(String cardId, String name);
    Checklist.CheckItem addCheckItemToCheckList(String checlistId, String name, int position, boolean isChecked);
}
