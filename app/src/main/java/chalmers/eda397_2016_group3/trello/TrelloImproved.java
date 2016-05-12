package chalmers.eda397_2016_group3.trello;

import org.trello4j.Trello;
import org.trello4j.model.Card;

/**
 * Created by sebastianblomberg on 2016-05-02.
 */
public interface TrelloImproved extends Trello {
    public void updateCardDescriptor(CardDescriptor card);
}
