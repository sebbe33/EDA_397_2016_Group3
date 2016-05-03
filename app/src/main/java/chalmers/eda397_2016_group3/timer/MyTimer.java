package chalmers.eda397_2016_group3.timer;

import android.os.Handler;

/**
 * Created by Alex on 2016-04-19.
 */
public class MyTimer {

    public interface Callback {
        void onTick(long totalElapsedMillis);
    }

    private class CallbackTask implements Runnable {
        @Override
        public void run() {
            long total = System.currentTimeMillis() - start;
            callback.onTick(total);
            synchronized(MyTimer.this) {
                if(!isRunning || isPaused)
                    return;
            }
            handler.postDelayed(this, Math.max(1, lastPeriod - (total % lastPeriod)));
        }
    }

    private final Handler handler;
    private MyTimer.CallbackTask task = new MyTimer.CallbackTask();
    private MyTimer.Callback callback;
    private boolean isRunning = false, isPaused = false;
    private int lastPeriod;
    private long start, pauseTime;

    public MyTimer(Handler handler) {
        this.handler = handler;
    }

    public void setCallback(MyTimer.Callback callback) {
        this.callback = callback;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public synchronized void start(int period) {
        if(period < 1)
            throw new IllegalArgumentException("period < 1");
        if(isRunning)
            throw new IllegalStateException("already running");
        isRunning = true;
        lastPeriod = period;
        start = System.currentTimeMillis();
        handler.postDelayed(task, period);
    }


    public synchronized void stop() {
        if(!isRunning)
            throw new IllegalStateException("not running");
        isRunning = false;
        isPaused = false;
        handler.removeCallbacks(task);
    }

    public synchronized void pause() {
        if(!isRunning)
            throw new IllegalStateException("not running");
        if(isPaused)
            return;
        isPaused = true;
        pauseTime = System.currentTimeMillis();
        handler.removeCallbacks(task);
    }

    public synchronized void resume() {
        if(!isRunning)
            throw new IllegalStateException("not running");
        if(!isPaused)
            return;
        isPaused = false;
        long delay = Math.max(1, lastPeriod - ((pauseTime - start) % lastPeriod));
        start += System.currentTimeMillis() - pauseTime;
        handler.postDelayed(task, delay);
    }

}
