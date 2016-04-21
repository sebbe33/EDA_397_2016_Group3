package chalmers.eda397_2016_group3;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import chalmers.eda397_2016_group3.trello.TrelloSetupActivity;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, TrelloSetupActivity.class);
        startActivity(intent);
    }
}
