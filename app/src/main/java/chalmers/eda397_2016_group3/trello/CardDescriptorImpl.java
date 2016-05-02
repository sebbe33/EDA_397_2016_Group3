package chalmers.eda397_2016_group3.trello;

import android.util.Log;

import org.trello4j.model.Card;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by sebastianblomberg on 2016-04-26.
 */
public class CardDescriptorImpl implements CardDescriptor {
    private static final String OPENING_DELIMITER = "<<<<< Metadata - Do not touch >>>>>",
            CLOSING_DELIMITER = "<<<<< End of Metadata >>>>>";
    private boolean isActive = false;
    private Date startDate, endDate, timeSpent;
    private Card card;
    private Date workingTimeStart;

    public CardDescriptorImpl(Card c) {
        String desc = c.getDesc();
        if(desc == null) {
            desc = "";
        }
        parseDescription(desc);
        this.card = c;
    }

    private void parseDescription(String description) {
        int openingIndex = description.indexOf(OPENING_DELIMITER);
        int closingIndex = description.indexOf(CLOSING_DELIMITER);

        if(openingIndex != -1 && closingIndex != -1) {
            String[] arguments = description.substring(
                    openingIndex + OPENING_DELIMITER.length(), closingIndex).split("\\|");
            Log.d("debug", "Arg: " + Arrays.toString(arguments));
            if (arguments != null && arguments.length == 5) {
                isActive = "true".equals(arguments[0]);
                startDate = new Date(Long.parseLong(arguments[1]));
                timeSpent = new Date(Long.parseLong(arguments[2]));
                endDate = new Date(Long.parseLong(arguments[3]));
                workingTimeStart = new Date(Long.parseLong(arguments[4]));
                return;
            }
        }

        // Else instantiate default
        startDate = new Date(0);
        timeSpent = new Date(0);
        workingTimeStart = new Date(0);
        endDate = new Date(0);
    }

    @Override
    public Card getCard() {
        return card;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public Date getTimeSpent() {
        return timeSpent;
    }

    @Override
    public void setStartDate(Date date) {
        startDate = new Date(date.getTime());
        writeValuesToCard();

    }

    @Override
    public void setEndDate(Date date) {
        endDate = new Date(date.getTime());
        writeValuesToCard();
    }

    @Override
    public void startRecordingWorkingTime() {
        workingTimeStart = new Date(System.currentTimeMillis());
        isActive = true;
        if(startDate.getTime() == 0) {
            startDate = new Date(System.currentTimeMillis());
        }
        writeValuesToCard();
    }

    @Override
    public void stopRecordingWorkingTime() {
        timeSpent = new Date(timeSpent.getTime() +
                (System.currentTimeMillis() - workingTimeStart.getTime()));
        isActive = false;
        writeValuesToCard();
    }

    @Override
    public String getDescription() {
        String description = card.getDesc();
        int openingIndex = card.getDesc().indexOf(OPENING_DELIMITER);
        int closingIndex = card.getDesc().indexOf(CLOSING_DELIMITER);

        String beforeMetadata = description;
        if(closingIndex != -1) {
            beforeMetadata = description.substring(0, openingIndex - 1);
        }

        String afterMetadata = "";
        if(closingIndex != -1) {
            afterMetadata = description.substring(closingIndex + CLOSING_DELIMITER.length() + 1, description.length());
        }

        return beforeMetadata + afterMetadata;
    }

    private void writeValuesToCard() {
        String newMetadata = "\n" + OPENING_DELIMITER
                + (isActive? "true":"false")
                + "|" + startDate.getTime()
                + "|" + timeSpent.getTime()
                + "|" + endDate.getTime()
                + "|" + workingTimeStart.getTime()
                + CLOSING_DELIMITER + "\n";
        String description = card.getDesc();
        int openingIndex = description.indexOf(OPENING_DELIMITER);
        int closingIndex = description.indexOf(CLOSING_DELIMITER);
        Log.d("debug", "OpeningIndex : " + openingIndex + " | Closing : " + closingIndex);

        String beforeMetadata = description;
        if(closingIndex != -1) {
            beforeMetadata = description.substring(0, openingIndex - 1);
        }

        String afterMetadata = "";
        if(closingIndex != -1) {
            afterMetadata = description.substring(closingIndex + CLOSING_DELIMITER.length(), description.length());
        }

        card.setDesc(beforeMetadata + newMetadata + afterMetadata);
    }
}
