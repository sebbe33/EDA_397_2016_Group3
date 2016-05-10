package chalmers.eda397_2016_group3.timer;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.trello4j.Trello;
import org.trello4j.model.Member;

import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import chalmers.eda397_2016_group3.MainActivity;
import chalmers.eda397_2016_group3.R;
import chalmers.eda397_2016_group3.trello.TrelloApp;
import chalmers.eda397_2016_group3.trello.TrelloAppService;
import chalmers.eda397_2016_group3.utils.Pairs;


public class FragmentTimer extends Fragment {

    private NotificationManager mNotifyMgr;
    private TrelloApp trelloApp = null;
    private Trello trelloAPI = null;
    private int totalTimeSeconds;
    //private MyTimer timer = new MyTimer(new Handler());
     TextView txtNavi;
     TextView txtDriv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        final Button btnStart = (Button) getView().findViewById(R.id.btnStart);
        final Button btnRest = (Button) getView().findViewById(R.id.btnReset);

        txtNavi= (TextView) getView().findViewById(R.id.navigatorText);
         txtDriv = (TextView) getView().findViewById(R.id.driverText);

        final Spinner SpinnerHour = (Spinner) getView().findViewById(R.id.hourSpinner);
        final Spinner SpinnerMinute = (Spinner) getView().findViewById(R.id.MinuteSpinner);
        final Spinner SpinnerSecond = (Spinner) getView().findViewById(R.id.SecondSpinner);

        trelloApp = TrelloAppService.getTrelloApp(getActivity());

        if(trelloApp.isAuthenticated()) {
            getBoardsHelper();
        } else {
            Toast.makeText(getActivity(),"Please login first",Toast.LENGTH_SHORT).show();

        }


        // Timer stuff
        final String timerFormat = getString(R.string.timer_format);
        final TextView tv_timer = (TextView) getView().findViewById(R.id.tv_timer);
        final MyTimer timer = new MyTimer(new Handler());
        timer.setCallback(new MyTimer.Callback() {
            @Override
            public void onTick(long totalElapsedMillis) {
                long millisLeft = Math.max(0, totalTimeSeconds * 1000 - totalElapsedMillis);
                String time = formatTime(timerFormat, millisLeft);
                tv_timer.setText(time);
                if (millisLeft == 0) {
                    timer.stop();
                    btnStart.setText("Start");
                    showDialog("Time is up!");
                    SpinnerHour.setEnabled(true);
                    SpinnerMinute.setEnabled(true);
                    SpinnerSecond.setEnabled(true);
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timer.isRunning()) {

                    String ss = toSeconds(SpinnerHour.getSelectedItem().toString(),
                            SpinnerMinute.getSelectedItem().toString(),
                            SpinnerSecond.getSelectedItem().toString());

                    totalTimeSeconds = Integer.parseInt(ss);
                    if(totalTimeSeconds==0){
                        Toast.makeText(getActivity(),"Please set a time first",Toast.LENGTH_SHORT).show();
                    }else {

                        // Start counting
                        timer.start(1);
                        btnStart.setText("Pause");
                    //    String time = formatTime(timerFormat, totalTimeSeconds * 1000);
                     //   tv_timer.setText(time);

                        SpinnerHour.setEnabled(false);
                        SpinnerMinute.setEnabled(false);
                        SpinnerSecond.setEnabled(false);
                        btnRest.setEnabled(false);
                    }

                } else if (!timer.isPaused()) {
                    //pause the timer
                    timer.pause();
                    btnStart.setText("Resume");
                    btnRest.setEnabled(true);
                    // Notification
                    //notificationBuilder.setContentText("Paused");
                   // mNotifyMgr.notify(R.integer.notification_timer, notificationBuilder.build());
                } else {
                    //resume the timer
                    timer.resume();
                    btnStart.setText("Pause");
                    btnRest.setEnabled(false);
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
//                mNotifyMgr.cancel(R.integer.notification_timer);
                SpinnerHour.setEnabled(true);
                SpinnerHour.setSelection(0);
                SpinnerMinute.setEnabled(true);
                SpinnerMinute.setSelection(0);
                SpinnerSecond.setEnabled(true);
                SpinnerSecond.setSelection(0);
                btnRest.setEnabled(true);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String toSeconds(String hour,String minute,String second){
        int hours = Integer.valueOf(hour);
        int minutes = Integer.valueOf(minute);
        int seconds = Integer.valueOf(second);
        return String.valueOf(hours*3600+minutes*60+seconds);
    }
    private String formatTime(String format, long millisLeft) {
        long hours = millisLeft / 1000 / 60 / 60,
                minutes = (millisLeft / 1000 / 60) % 60,
                seconds = (millisLeft / 1000) % 60,
                millis = millisLeft % 1000;

        return String.format(Locale.US, format, hours, minutes, seconds, millis);
    }

    protected void showDialog(String text) {

        Intent startIntent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(startIntent);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Timer").setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setPair(String pair){
        StringTokenizer st = new StringTokenizer(pair);
        txtNavi.setText("Partner: "+st.nextToken(":"));
      //  txtDriv.setText("Driver: "+st.nextToken(":"));
        txtDriv.setText("");

    }
    private void getBoardsHelper() {
        trelloAPI = TrelloAppService.getTrelloAPIInterface(trelloApp);
        // Get boards
        new FetchBoards().execute(trelloAPI);
  }
    private class FetchBoards extends AsyncTask<Trello, Integer, List<Member>> {

        @Override
        protected List<Member> doInBackground(Trello... params) {
            TrelloApp trelloApp = TrelloAppService.getTrelloApp(getActivity());
            Trello trelloAPI = TrelloAppService.getTrelloAPIInterface(trelloApp);
            List<Member> members = trelloAPI.getMembersByBoard(trelloApp.getSelectedBoardID());

            return  members;
        }

        @Override
        protected void onPostExecute(List<Member> result) {
            Pairs pairs = new Pairs();
            for (int j = 0; j<result.size();j++){
                pairs.addPair(result.get(j).getFullName());

            }
            setPair(pairs.getRandomPair());
        }
    }
}


