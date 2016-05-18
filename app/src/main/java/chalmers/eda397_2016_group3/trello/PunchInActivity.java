package chalmers.eda397_2016_group3.trello;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.trello4j.Trello;
import org.trello4j.model.Card;
import org.trello4j.model.Checklist;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import chalmers.eda397_2016_group3.R;

/**
 * Created by N10 on 4/26/2016.
 */
public class PunchInActivity  extends AppCompatActivity {


    public static String color="#fffff";

    int status;

    private Card activeCard;
    private CardDescriptor cardDescriptor = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.punchin_feature);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            throw new IllegalStateException("A cardID must be sent with the bundle");
        }

        String cardID = extras.getString(TrelloAppService.TRELLO_CARD_ID);
        if(cardID == null) {
            throw new IllegalStateException("A cardID must be sent with the bundle");
        }

        // Set temporary title
        String menuName = extras.getString("title");
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(menuName);

        // Initialize trello service
        TrelloApp trelloApp = TrelloAppService.getTrelloApp(this);
        TrelloImproved trelloAPI = TrelloAppService.getTrelloAPIInterface(trelloApp);

        // Start new task to retrieve card and checklist
        new FetchCard(trelloAPI).execute(cardID);
        new FetchChecklist(trelloAPI).execute(cardID);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        status=0;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadBackdrop(R.drawable.background);
    }

    private void loadBackdrop(int imgId) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        imageView.setBackgroundColor(Color.parseColor("#ffffff"));
        Glide.with(this).load(imgId).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onBackPressed();
        return true;
    }

    private void initializeView() {

    }

    private void updateView() {
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(activeCard.getName());

        final TextView startDateView = (TextView) findViewById(R.id.feature_start_date);
        final TextView timeSpentView = (TextView) findViewById(R.id.feature_time);
        final TextView endDateView = (TextView) findViewById(R.id.feature_end_date);
        final Button finishButton = (Button) findViewById(R.id.feature_done_button);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        if(cardDescriptor.getStartDate().getTime() != 0) {
            startDateView.setText("Started work: " + new SimpleDateFormat("yyyy-MM-dd").format(cardDescriptor.getStartDate()));
            finishButton.setOnClickListener(onFinishActivityListener);
            finishButton.setVisibility(View.VISIBLE);
        } else {
            finishButton.setVisibility(View.GONE);
        }

        if(cardDescriptor.getEndDate().getTime() == 0) {
            endDateView.setVisibility(View.GONE);
        } else {
            finishButton.setVisibility(View.GONE);
            endDateView.setVisibility(View.VISIBLE);
            endDateView.setText("Finished work: " + new SimpleDateFormat("yyyy-MM-dd").format(cardDescriptor.getEndDate()));
        }

        timeSpentView.setText("Time spent: " +
                df.format((cardDescriptor.getTimeSpent().getTime() / (double)(3600 * 1000)) ) + "h");

        ((TextView) findViewById(R.id.other_details_text)).setText(cardDescriptor.getDescription());

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(!cardDescriptor.isActive()) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_background_out)));
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_background_in)));
        }

        fab.setOnClickListener(onTimeRecordingToggleListener);
    }

    private class FetchCard extends AsyncTask<String, Integer, Card> {
        private final Trello trelloAPI;

        public FetchCard(Trello trelloAPI) {
            this.trelloAPI = trelloAPI;
        }

        @Override
        protected Card doInBackground(String... params) {
            String card = params[0];
            return trelloAPI.getCard(card);
        }

        @Override
        protected void onPostExecute(Card result) {
            PunchInActivity.this.activeCard = result;
            PunchInActivity.this.cardDescriptor = new CardDescriptorImpl(result);
            updateView();
        }
    }

    private class FetchChecklist extends AsyncTask<String, Integer, Checklist> {
        private final TrelloImproved trelloAPI;
        private String cardId;
        public FetchChecklist(TrelloImproved trelloAPI) {
            this.trelloAPI = trelloAPI;
        }

        @Override
        protected Checklist doInBackground(String... params) {
            String cardId = params[0];
            List<Checklist> checklists = trelloAPI.getChecklistByCard(cardId);

            Checklist definitionOfDoneChecklist = null;

            for(Checklist c : checklists) {
                if(c.getName().equals("Definition of done")) {
                    definitionOfDoneChecklist = c;
                    break;
                }
            }

            if(definitionOfDoneChecklist == null) {
                // Create a new checklist
                Checklist dodChecklist = trelloAPI.createChecklistInCard(cardId, "Definition of done");
                List<String> dodItems = new ArrayList<>();
                dodItems.add("Item 1");
                dodItems.add("Item 2");

                int i = 0;
                for(String name : dodItems) {
                    trelloAPI.addCheckItemToCheckList(dodChecklist.getId(), name, i, false);
                }

                definitionOfDoneChecklist = trelloAPI.getChecklist(dodChecklist.getId());
            }



            return definitionOfDoneChecklist;
        }

        @Override
        protected void onPostExecute(Checklist result) {
            Log.d("debug", "checklist : " + result.getName());
        }
    }

    private class UpdateCardDescriptorTask extends AsyncTask<CardDescriptor, Integer, CardDescriptor> {
        private final TrelloImproved trelloAPI;

        public UpdateCardDescriptorTask(TrelloImproved trelloAPI) {
            this.trelloAPI = trelloAPI;
        }

        @Override
        protected CardDescriptor doInBackground(CardDescriptor... params) {
            trelloAPI.updateCardDescriptor(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(CardDescriptor result) {
            Log.d("debug", "update succeded");
        }
    }


    private View.OnClickListener onTimeRecordingToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(cardDescriptor.isActive()) {
                cardDescriptor.stopRecordingWorkingTime();
            } else {
                cardDescriptor.startRecordingWorkingTime();
            }
            updateView();

            new UpdateCardDescriptorTask(
                    TrelloAppService.getTrelloAPIInterface(TrelloAppService.getTrelloApp(PunchInActivity.this))
            ).execute(cardDescriptor);
        }
    };

    private View.OnClickListener onFinishActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cardDescriptor.setEndDate(new Date(System.currentTimeMillis()));
            new UpdateCardDescriptorTask(
                    TrelloAppService.getTrelloAPIInterface(TrelloAppService.getTrelloApp(PunchInActivity.this))
            ).execute(cardDescriptor);
            updateView();
        }
    };

}

