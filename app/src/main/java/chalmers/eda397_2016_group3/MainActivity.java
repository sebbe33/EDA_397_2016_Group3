package chalmers.eda397_2016_group3;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    private int totalTimeSeconds;
    EditText edtSetTime;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private NotificationManager mNotifyMgr;

    private MyTimer timer = new MyTimer(new Handler());

    private String formatTime(String format, long millisLeft) {
        long hours = millisLeft / 1000 / 60 / 60,
                minutes = (millisLeft / 1000 / 60) % 60,
                seconds = (millisLeft / 1000) % 60,
                millis = millisLeft % 1000;

        return String.format(Locale.US, format, hours, minutes, seconds, millis);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Notification
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Timer");

        // Timer stuff
        final String timerFormat = getString(R.string.timer_format);
        final TextView tv_timer = (TextView) findViewById(R.id.tv_timer);
        final MyTimer timer = new MyTimer(new Handler());
        timer.setCallback(new MyTimer.Callback() {
            @Override
            public void onTick(long totalElapsedMillis) {
                long millisLeft = Math.max(0, totalTimeSeconds * 1000 - totalElapsedMillis);
                String time = formatTime(timerFormat, millisLeft);
                tv_timer.setText(time);
                notificationBuilder.setContentText(time);
                mNotifyMgr.notify(R.integer.notification_timer, notificationBuilder.build());
                // If the current time over the time user set.
                if (millisLeft == 0) {
                    timer.stop();
                    // Notify the user
                    notificationBuilder.setContentText("Time is up!");
                    notificationBuilder.setSound(Uri.parse("android.resource://"
                            + getPackageName() + "/" + R.raw.cat));
                    mNotifyMgr.notify(R.integer.notification_timer, notificationBuilder.build());
                    notificationBuilder.setSound(null);
                    showDialog();

                }
            }
        });
        final Button btnStart = (Button) findViewById(R.id.btnStart);
        Button btnRest = (Button) findViewById(R.id.btnReset);
               edtSetTime = (EditText) findViewById(R.id.edt_settime);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timer.isRunning()) {
                    System.out.println("--Start MyTimer---");
                    String ss = edtSetTime.getText().toString();
                    if (!(ss.equals("") && ss != null)) {
                        totalTimeSeconds = Integer.parseInt(edtSetTime.getText()
                                .toString());
                    }
                    // Start counting
                    timer.start(1);
                    btnStart.setText("Pause");
                    String time = formatTime(timerFormat, totalTimeSeconds*1000);
                    tv_timer.setText(time);
                    // Notification
                    notificationBuilder.setContentText(time);
                    mNotifyMgr.notify(R.integer.notification_timer, notificationBuilder.build());
                } else if (!timer.isPaused()) {
                    //pause the timer
                    timer.pause();
                    btnStart.setText("Resume");
                    // Notification
                    notificationBuilder.setContentText("Paused");
                    mNotifyMgr.notify(R.integer.notification_timer, notificationBuilder.build());
                } else {
                    //resume the timer
                    timer.resume();
                    btnStart.setText("Pause");
                }
            }
        });

        // reset
        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset the timer
                if(timer.isRunning())
                    timer.stop();
                tv_timer.setText(null);
                btnStart.setText("Start");
                mNotifyMgr.cancel(R.integer.notification_timer);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }




    @Override
    protected void onResume() {
        super.onResume();


    }

    protected void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //   builder.setIcon(R.drawable.eb28d25);
        builder.setTitle("Alert").setMessage("Time is up!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}

