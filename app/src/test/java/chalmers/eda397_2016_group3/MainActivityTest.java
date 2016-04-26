package chalmers.eda397_2016_group3;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hui on 26/04/16.
 */
public class MainActivityTest {

    @Test
    public void testShowDialog() throws Exception {

    }

    @Test
    public void testToSeconds() throws Exception {
        MainActivity mainActivity = new MainActivity();
        assertEquals("Time can covert to seconds", "43932",mainActivity.toSeconds("12","12","12"));

    }
}