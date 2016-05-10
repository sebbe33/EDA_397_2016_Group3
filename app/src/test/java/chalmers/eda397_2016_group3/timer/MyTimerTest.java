package chalmers.eda397_2016_group3.timer;

import android.os.Handler;
import chalmers.eda397_2016_group3.timer.MyTimer;

import org.junit.Test;

import java.util.Locale;

import chalmers.eda397_2016_group3.R;

import static org.junit.Assert.*;

/**
 * Created by hui on 28/04/16.
 */
public class MyTimerTest {

    @Test
    public void testSetCallback() throws Exception {

    }

    @Test
    public void testIsRunning() throws Exception {

    }

    @Test
    public void testIsPaused() throws Exception {



    }

    @Test
    public void testStart() throws Exception {

    }

    @Test
    public void testStop() throws Exception {

    }

    @Test
    public void testPause() throws Exception {


        final int totalTimeSeconds = 10;
        final String timerFormat = "%02d:%02d:%02d";
        final String[] time = {""};
        final MyTimer timer = new MyTimer(new Handler());


        timer.setCallback(new MyTimer.Callback() {
            @Override
            public void onTick(long totalElapsedMillis) {
                long millisLeft = Math.max(0, totalTimeSeconds * 1000 - totalElapsedMillis);
                if (millisLeft == 100) {
                    timer.pause();
                    time[0] = formatTime(timerFormat, millisLeft);
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time[1] =  formatTime(timerFormat, millisLeft);
                    assertEquals(time[0],time[1]);
                }
            }
        });
        timer.start(1);
    }

    @Test
    public void testResume() throws Exception {

    }

    private String formatTime(String format, long millisLeft) {
        long hours = millisLeft / 1000 / 60 / 60,
                minutes = (millisLeft / 1000 / 60) % 60,
                seconds = (millisLeft / 1000) % 60,
                millis = millisLeft % 1000;

        return String.format(Locale.US, format, hours, minutes, seconds, millis);
    }

}