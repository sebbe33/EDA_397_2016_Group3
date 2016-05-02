package chalmers.eda397_2016_group3;

import org.junit.Test;
import org.trello4j.model.Card;

import chalmers.eda397_2016_group3.trello.CardDescriptor;
import chalmers.eda397_2016_group3.trello.CardDescriptorImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by sebastianblomberg on 2016-05-02.
 */
public class CardDescriptorUnitTest {
    private class MockCard extends Card {
        private String desc;
        @Override
        public String getDesc() {
            return desc;
        }

        @Override
        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    @Test
    public void testGettersFull() throws Exception {
        Card c = new MockCard();
        long startTime = 2037921,
                timeSpent = 12312,
                endTime = 2371298,
                activeWorkingTimeStart = 12312;

        c.setDesc(CardDescriptorImpl.OPENING_DELIMITER + "true|"+startTime+"|"+timeSpent+"|"+endTime+"|"+activeWorkingTimeStart+CardDescriptorImpl.CLOSING_DELIMITER);

        CardDescriptor cd = new CardDescriptorImpl(c);
        assertEquals("Nothing else should be in the description", "", cd.getDescription());
        assertEquals(startTime, cd.getStartDate().getTime());
        assertEquals(timeSpent, cd.getTimeSpent().getTime());
        assertEquals(endTime, cd.getEndDate().getTime());
    }

    @Test
    public void testEdgeIncompleteEndDelimiter() throws Exception {
        Card c = new MockCard();
        long startTime = 2037921,
                timeSpent = 12312,
                endTime = 2371298,
                activeWorkingTimeStart = 12312;

        // Test missing end tag
        c.setDesc(CardDescriptorImpl.OPENING_DELIMITER + "true|"+startTime+"|"+timeSpent+"|"+endTime+"|"+"|"+activeWorkingTimeStart);

        CardDescriptor cd = new CardDescriptorImpl(c);
        incompleteDelimiterHelper(cd);
    }

    @Test
    public void testEdgeIncompleteStartDelimiter() throws Exception {
        Card c = new MockCard();
        long startTime = 2037921,
                timeSpent = 12312,
                endTime = 2371298,
                activeWorkingTimeStart = 12312;

        c.setDesc("true|"+startTime+"|"+timeSpent+"|"+endTime+"|"+"|"+activeWorkingTimeStart + CardDescriptorImpl.CLOSING_DELIMITER);
        CardDescriptor cd = new CardDescriptorImpl(c);
        incompleteDelimiterHelper(cd);
    }

    @Test
    public void testEdgeIncompleteArgument() throws Exception {
        Card c = new MockCard();
        long startTime = 2037921,
                timeSpent = 12312,
                endTime = 2371298,
                activeWorkingTimeStart = 12312;

        // Test missing a value
        c.setDesc(CardDescriptorImpl.OPENING_DELIMITER + "true|" + startTime + "|" + timeSpent + "|"+activeWorkingTimeStart + CardDescriptorImpl.CLOSING_DELIMITER);
        CardDescriptor cd = new CardDescriptorImpl(c);
        incompleteDelimiterHelper(cd);
    }

    @Test
    public void testEdgeInvalidNumberFormat() throws Exception {
        Card c = new MockCard();
        long
                timeSpent = 12312,
                endTime = 2371298,
                activeWorkingTimeStart = 12312;

        // Test missing a value
        c.setDesc(CardDescriptorImpl.OPENING_DELIMITER + "true|asd''|" + timeSpent + "|"+endTime+"|"+activeWorkingTimeStart  + CardDescriptorImpl.CLOSING_DELIMITER);
        CardDescriptor cd = new CardDescriptorImpl(c);
        incompleteDelimiterHelper(cd);
    }

    private void incompleteDelimiterHelper(CardDescriptor cd) {
        assertEquals("All fields should be set to default when parsing can't take place",
                cd.getStartDate().getTime(), 0);
        assertEquals("All fields should be set to default when parsing can't take place",
                cd.getTimeSpent().getTime(), 0);
        assertEquals("All fields should be set to default when parsing can't take place",
                cd.getEndDate().getTime(), 0);
    }

    @Test
    public void testGetDescription() throws Exception {
        Card c = new MockCard();
        String before = "This is the description before the metadata.";
        String after = "\n\n\nThis is the description after the metadata.";
        c.setDesc(before +
                CardDescriptorImpl.OPENING_DELIMITER + "true|0|0|0|0"+CardDescriptorImpl.CLOSING_DELIMITER
            +after);

        CardDescriptor cd = new CardDescriptorImpl(c);
        assertEquals("The getDescription() shall not include any metadata", before+after, cd.getDescription());
    }

    @Test
    public void testRecordingWorkingTime() throws Exception {
        Card c = new MockCard();
        CardDescriptor cd = new CardDescriptorImpl(c);

        assertEquals("Per default, timeSpent should be 0", cd.getTimeSpent().getTime(), 0);
        assertFalse("Per default, isActive should be false", cd.isActive());

        // Punch in
        long recordWorkingTimeBegining = System.currentTimeMillis();
        cd.startRecordingWorkingTime();

        assertTrue("When punched in, isActive should be true", cd.isActive());

        // Let some time pass by
        Thread.sleep(30);

        cd.stopRecordingWorkingTime();
        // Check time spent, with some margin
        int margin = 2; // 2 ms
        long timeSpent = System.currentTimeMillis() - recordWorkingTimeBegining;
        assertTrue(timeSpent - margin <= cd.getTimeSpent().getTime() &&
                cd.getTimeSpent().getTime() <= timeSpent + margin);

        assertFalse("When punched out, isActive should be false", cd.isActive());
    }
}
