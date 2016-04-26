package chalmers.eda397_2016_group3;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by N10 on 4/26/2016.
 */
public class PunchInActivity  extends AppCompatActivity {


    public static String color="#fffff";

    int status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.punchin_feature);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        status=0;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Feature Name");


        final TextView textView1= (TextView) findViewById(R.id.feature_status);
        final TextView textView2= (TextView) findViewById(R.id.feature_time);
        textView1.setText("Feature started");
        textView2.setText("Started since ");


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

        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onBackPressed();
        return true;
    }




}

