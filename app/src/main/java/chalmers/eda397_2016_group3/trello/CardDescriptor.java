package chalmers.eda397_2016_group3.trello;

import java.util.Date;

/**
 * Created by sebastianblomberg on 2016-04-26.
 */
public interface CardDescriptor {
    boolean isActive();
    Date getStartDate();
    Date getTimeSpent();
}
