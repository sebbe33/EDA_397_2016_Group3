package chalmers.eda397_2016_group3.trello;

import org.trello4j.model.Card;

import java.util.Date;

/**
 * Created by sebastianblomberg on 2016-04-26.
 */
public interface CardDescriptor {
    Card getCard();
    boolean isActive();
    Date getStartDate();
    Date getTimeSpent();
    Date getEndDate();
    String getDescription();

    void setStartDate(Date date);
    void setEndDate(Date date);

    void startRecordingWorkingTime();
    void stopRecordingWorkingTime();
}
