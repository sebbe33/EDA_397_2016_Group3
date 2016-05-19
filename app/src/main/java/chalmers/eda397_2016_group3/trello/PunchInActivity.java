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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private static final String DOD_LIST_NAME = "Definition Of Done";
    private TrelloImproved trelloAPI;
    private Card activeCard;
    private CardDescriptor cardDescriptor = null;

    private ChecklistImproved checkList = null;

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
        trelloAPI = TrelloAppService.getTrelloAPIInterface(trelloApp);

        // Start new task to retrieve card and checklist
        new FetchCard(trelloAPI).execute(cardID);
        new FetchChecklist(trelloAPI).execute(cardID);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        if(checkList != null) {
            LinearLayout checklistLayout = (LinearLayout) findViewById(R.id.checklist_layout);
            checklistLayout.removeAllViews();
            for(final ChecklistImproved.CheckItem item : checkList.getCheckItems()) {
                Log.d("debug", "Adding checkitem " + item.getName());
                final CheckBox currentItemCheckbox = new CheckBox(this);
                currentItemCheckbox.setText(item.getName());
                currentItemCheckbox.setChecked(item.getState().equals("complete"));
                Log.d("debug", item.getState());
                currentItemCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new UpdateCheckItem(trelloAPI, item)
                                .execute(currentItemCheckbox.isChecked()? "complete" : "incomplete");
                    }
                });
                checklistLayout.addView(currentItemCheckbox);
            }
        }
    }

    private class UpdateCheckItem extends AsyncTask<String, Integer, ChecklistImproved.CheckItem> {
        private final TrelloImproved trelloAPI;
        private ChecklistImproved.CheckItem checkItem;
        public UpdateCheckItem(TrelloImproved trelloAPI, ChecklistImproved.CheckItem checkItem) {
            this.trelloAPI = trelloAPI;
            this.checkItem = checkItem;
        }

        @Override
        protected ChecklistImproved.CheckItem doInBackground(String... params) {
            this.checkItem.setState(params[0] == "complete"? "complete" : "incomplete");
            trelloAPI.updateCheckItemByCard(activeCard, checkList, checkItem);
            return checkItem;
        }

        @Override
        protected void onPostExecute(ChecklistImproved.CheckItem result) {
            updateView();
        }
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

    private class FetchChecklist extends AsyncTask<String, Integer, ChecklistImproved> {
        private final TrelloImproved trelloAPI;
        private String cardId;
        public FetchChecklist(TrelloImproved trelloAPI) {
            this.trelloAPI = trelloAPI;
        }

        @Override
        protected ChecklistImproved doInBackground(String... params) {
            String cardId = params[0];
            List<ChecklistImproved> checklists = trelloAPI.getChecklistImprovedByCard(cardId);

            ChecklistImproved definitionOfDoneChecklist = null;

            for(ChecklistImproved c : checklists) {
                if(c.getName().equals(DOD_LIST_NAME)) {
                    definitionOfDoneChecklist = c;
                    break;
                }
            }

            if(definitionOfDoneChecklist == null) {
                // Create a new checklist
                Checklist dodChecklist = trelloAPI.createChecklistInCard(cardId, DOD_LIST_NAME);

                int i = 0;
                for(String name : DefinitionOfDoneService.getDodList(PunchInActivity.this)) {
                    trelloAPI.addCheckItemToCheckList(dodChecklist.getId(), name, i, false);
                }

                definitionOfDoneChecklist = trelloAPI.getChecklistImproved(dodChecklist.getId());
            }



            return definitionOfDoneChecklist;
        }

        @Override
        protected void onPostExecute(ChecklistImproved result) {
            Log.d("debug", "checklist : " + result.getName());
            checkList = result;
            updateView();
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

