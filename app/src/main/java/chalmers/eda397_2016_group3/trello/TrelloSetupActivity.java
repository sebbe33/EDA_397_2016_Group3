package chalmers.eda397_2016_group3.trello;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.trello4j.Trello;
import org.trello4j.model.Board;

import java.util.ArrayList;
import java.util.List;

import chalmers.eda397_2016_group3.R;

public class TrelloSetupActivity extends ActionBarActivity {
    private TrelloApp trelloApp = null;
    private Trello trelloAPI = null;
    private Bundle activeBundle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trello_setup);

        trelloApp = TrelloAppFactory.createTrelloApp(this, savedInstanceState);
        activeBundle = savedInstanceState;



        if(trelloApp.isAuthenticated()) {
            getBoardsHelper();
        } else {
            Button loginButton = (Button) findViewById(R.id.trello_login_button);
            loginButton.setText(getResources().getString(R.string.TRELLO_LOGIN_BUTTON_NOT_AUTHENTICATED));
        }
    }

    public void onTrelloLoginButtonClicked(View v) {
        if(trelloApp.isAuthenticated()) {
            // TODO : Logout
        } else {
            Intent trelloLoginIntent = new Intent(this, TrelloLoginActivity.class);
            activeBundle = new Bundle();
            trelloLoginIntent.putExtras(activeBundle);
            startActivityForResult(trelloLoginIntent, TrelloLoginActivity.LOGIN_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == TrelloLoginActivity.LOGIN_REQUEST_CODE) {
            if(resultCode == TrelloLoginActivity.LOGIN_SUCCEEDED){
                // Update app
                activeBundle = intent.getExtras();
                trelloApp = TrelloAppFactory.createTrelloApp(this, activeBundle);
                getBoardsHelper();
            } else if (resultCode == TrelloLoginActivity.LOGIN_FAILED) {

            }
        }
    }

    private void getBoardsHelper() {
        trelloAPI = TrelloAppFactory.getTrelloAPIInterface(trelloApp);
        // Get boards
        new FetchBoards().execute(trelloAPI);

        Button loginButton = (Button) findViewById(R.id.trello_login_button);
        loginButton.setText(getResources().getString(R.string.TRELLO_LOGIN_BUTTON_AUTHENTICATED));
    }

    private void setUpBoardSpinner(List<String> options) {
        Spinner spinner = (Spinner) findViewById(R.id.trello_board_spinner);
        ArrayAdapter<String> spinnerArrayAdapter =
                new ArrayAdapter<>(TrelloSetupActivity.this, android.R.layout.simple_spinner_item, options);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private class FetchBoards extends AsyncTask<Trello, Integer, List<Board>> {

        @Override
        protected List<Board> doInBackground(Trello... params) {
            Trello trello = params[0];
            return trello.getBoardsByMember("me");
        }

        @Override
        protected void onPostExecute(List<Board> result) {
            List<String> options = new ArrayList<>(result.size());

            for(Board b : result) {
                Log.d("debug", "Retreived Board " + b.getName());
                options.add(b.getName());
            }

            setUpBoardSpinner(options);
        }
    }
}
