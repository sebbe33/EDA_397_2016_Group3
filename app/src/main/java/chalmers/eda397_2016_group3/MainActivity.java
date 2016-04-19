package chalmers.eda397_2016_group3;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends ActionBarActivity {
    private int startTime = 0;
    private boolean timerIsStarted = false;
    private long timeWhenStopped = 0;
    private boolean timerIsPaused = false;
    private Intent intent;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DevicePolicyManager policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        final ComponentName componentName = new ComponentName(this, MyAdminReceiver.class);
        final boolean isAdminActive = policyManager.isAdminActive(componentName);
        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        final Button btnStart = (Button) findViewById(R.id.btnStart);
        Button btnRest = (Button) findViewById(R.id.btnReset);
        final EditText edtSetTime = (EditText) findViewById(R.id.edt_settime);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerIsStarted && !timerIsPaused) {
                    System.out.println("--Start Timer---");
                    String ss = edtSetTime.getText().toString();
                    if (!(ss.equals("") && ss != null)) {
                        startTime = Integer.parseInt(edtSetTime.getText()
                                .toString());
                    }
                    // Set start time
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    // Start counting
                    chronometer.start();
                    btnStart.setText("Pause");
                    timerIsStarted = true;

                } else if (timerIsStarted && !timerIsPaused) {
                    //pause the timer
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    timerIsPaused = true;
                    timerIsStarted = false;
                    chronometer.stop();
                    btnStart.setText("Resume");

                } else {
                    //resume the timer
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    chronometer.start();
                    timerIsPaused = false;
                    timerIsStarted = true;
                    btnStart.setText("Pause");

                }
            }
        });

        // reset
        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset the timer
                chronometer.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;
                btnStart.setText("Start");
                timerIsPaused = false;
                timerIsStarted = false;

            }
        });
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                // If the current time over the time user set.
                if (SystemClock.elapsedRealtime()
                        - chronometer.getBase() > startTime * 1000) {
                    chronometer.stop();
                    // Notify the user
                    showDialog();

                    if(!isAdminActive){
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                        startActivity(intent);
                    }
                }
            }
        });





        intent = new Intent(this, MyService.class);
        startService(intent);




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

