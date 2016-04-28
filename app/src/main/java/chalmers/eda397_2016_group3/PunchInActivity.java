package chalmers.eda397_2016_group3;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.trello4j.Trello;
import org.trello4j.model.Board;
import org.trello4j.model.Card;

import java.util.ArrayList;
import java.util.List;

import chalmers.eda397_2016_group3.trello.CardDescriptor;
import chalmers.eda397_2016_group3.trello.CardDescriptorImpl;
import chalmers.eda397_2016_group3.trello.TrelloApp;
import chalmers.eda397_2016_group3.trello.TrelloAppImpl;
import chalmers.eda397_2016_group3.trello.TrelloAppService;
import chalmers.eda397_2016_group3.utils.AdapterTuple;

/**
 * Created by N10 on 4/26/2016.
 */
public class PunchInActivity  extends AppCompatActivity {


    public static String color="#fffff";

    int status;

    private Card activeCard;

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
        Trello trelloAPI = TrelloAppService.getTrelloAPIInterface(trelloApp);

        // Start new task to retrieve card
        new FetchCard(trelloAPI).execute(cardID);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        status=0;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadBackdrop(R.drawable.background);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(status==0) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_background_out)));


        }
        else{
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_background_in)));

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(status==0){
                    Snackbar.make(view,"Feature started", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_background_in)));
                    status=1;
                }
                else{
                    Snackbar.make(view, "Feature Stopped", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_background_out)));
                    status=0;
                }

            }
        });


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

    private void updateView(Card c) {
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(c.getName());

        CardDescriptor cardDescriptor = new CardDescriptorImpl(c);
        final TextView textView1= (TextView) findViewById(R.id.feature_status);
        final TextView textView2= (TextView) findViewById(R.id.feature_time);
        textView1.setText("Started work: " + cardDescriptor.getStartDate().toString());
        textView2.setText("Time spent: " + cardDescriptor.getTimeSpent().toString());
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
            updateView(PunchInActivity.this.activeCard);
        }
    }

}

