package chalmers.eda397_2016_group3.trello;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.trello4j.Trello;
import org.trello4j.model.Board;
import org.trello4j.model.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import chalmers.eda397_2016_group3.MainActivity;
import chalmers.eda397_2016_group3.R;
import chalmers.eda397_2016_group3.utils.AdapterTuple;

public class TrelloSetupFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private TrelloApp trelloApp = null;
    private Trello trelloAPI = null;
    private List<AdapterTuple<String,String>> spinnerOptions = null;
    public String uname;
    PieChart mPieChart ;
    private TextView statisticsTxt;
    private MainActivity mainActivity;
    List<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trello_setup, container, false);



        return view;
    }

    @Override
    public void onStart () {
        super.onStart();
        trelloApp = TrelloAppService.getTrelloApp(getActivity());
        statisticsTxt = (TextView) getView().findViewById(R.id.statisticsTxt);

        Button loginButton = (Button) getView().findViewById(R.id.trello_login_button);
        mPieChart = (PieChart) getView().findViewById(R.id.piechart);
        mainActivity= (MainActivity) getActivity();

        if(trelloApp.isAuthenticated()) {
            getBoardsHelper();

        } else {

            loginButton.setText(getResources().getString(R.string.TRELLO_LOGIN_BUTTON_NOT_AUTHENTICATED));
            mainActivity.removeUser();
        }
        // Add self as listener to the login button
        loginButton.setOnClickListener(new LoginButtonListener());

        // Add self as a listener to the board spinner
        Spinner boardSpinner = (Spinner) getView().findViewById(R.id.trello_board_spinner);
        boardSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == TrelloLoginActivity.LOGIN_REQUEST_CODE) {
            if(resultCode == TrelloLoginActivity.LOGIN_SUCCEEDED){
                getBoardsHelper();



            } else if (resultCode == TrelloLoginActivity.LOGIN_FAILED) {

            }
        }
    }

    private void getBoardsHelper() {
        trelloAPI = TrelloAppService.getTrelloAPIInterface(trelloApp);
        // Get boards
        new FetchBoards().execute(trelloAPI);
        Button loginButton = (Button) getView().findViewById(R.id.trello_login_button);
        loginButton.setText(getResources().getString(R.string.TRELLO_LOGIN_BUTTON_AUTHENTICATED));
    }

    private void setUpBoardSpinner(List<AdapterTuple<String,String>> options, Integer selectedIntex) {
        Spinner spinner = (Spinner) getView().findViewById(R.id.trello_board_spinner);
        ArrayAdapter<AdapterTuple<String,String>> spinnerArrayAdapter =
                new ArrayAdapter<AdapterTuple<String,String>>(getActivity(), android.R.layout.simple_spinner_item, options);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        if(selectedIntex != null && selectedIntex < options.size()) {
            spinner.setSelection(selectedIntex);
            new CardFetcher(trelloAPI).execute(trelloApp.getSelectedBoardID());

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(spinnerOptions != null) {
            Log.d("debug", "Selected: " + spinnerOptions.get(position).getValue());
            TrelloAppService.setSelectedBoardID(spinnerOptions.get(position).getKey(), getActivity());
        }
    }

    private void setUpPieChart(List<Card> result){
        mPieChart.clearChart();
        CardDescriptorImpl cardDescriptorImpl ;
        Random random = new Random();
        int totalSpendHours = 0;
        for(Card card:result){
            cardDescriptorImpl  = new CardDescriptorImpl(card);
            Log.d("cardDescriptorImpl",""+cardDescriptorImpl.getTimeSpent());

            mPieChart.addPieSlice(new PieModel(ellipsize(card.getName(),60), cardDescriptorImpl.getTimeSpent().getHours(), Color.parseColor(
                    String.format("#%06x", random.nextInt(256*256*256))
            )));
            totalSpendHours+=cardDescriptorImpl.getTimeSpent().getHours();
        }

        statisticsTxt.setText("Total spend time: "+totalSpendHours+" Hours");
        mPieChart.startAnimation();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class LoginButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(trelloApp.isAuthenticated()) {
                TrelloAppService.setAuthenticationToken("", getActivity());
                // Toggle button and and disable selected board

                Button loginButton = (Button) getView().findViewById(R.id.trello_login_button);
                loginButton.setText(getResources().getString(R.string.TRELLO_LOGIN_BUTTON_NOT_AUTHENTICATED));
                mainActivity.removeUser();
                // TODO : Disable selected board
            } else {

                Intent trelloLoginIntent = new Intent(getActivity(), TrelloLoginActivity.class);
                startActivityForResult(trelloLoginIntent, TrelloLoginActivity.LOGIN_REQUEST_CODE);
            }
        }
    }

    private class FetchBoards extends AsyncTask<Trello, Integer, List<Board>> {



        @Override
        protected List<Board> doInBackground(Trello... params) {
            Trello trello = params[0];
            list=new ArrayList<>();
            list.add(trello.getMember("me").getFullName());
            list.add(trello.getMember("me").getInitials());

            if (trello.getMember("me").getAvatarHash().isEmpty())
             list.add("noimage");
            else
                list.add(trello.getMember("me").getAvatarHash());

            return trello.getBoardsByMember("me");
        }

        @Override
        protected void onPostExecute(List<Board> result) {
            spinnerOptions = new ArrayList<>(result.size());
            Integer selectedIndex = null;
            int i = 0;
            mainActivity.addUserDetails(list);
            for(Board b : result) {
                Log.d("debug", "Retreived Board " + b.getName());
                spinnerOptions.add(new AdapterTuple<String, String>(b.getId(), b.getName()));
                if(b.getId().equals(trelloApp.getSelectedBoardID())) {
                    Log.d("debug", "Selected board: " + b.getName());
                    selectedIndex = i;
                }
                i++;
            }
            setUpBoardSpinner(spinnerOptions, selectedIndex);
        }
    }

    private class CardFetcher extends AsyncTask<String, Integer, List<Card>> {
        private final Trello trelloAPI;

        public CardFetcher(Trello trelloAPI) {
            this.trelloAPI = trelloAPI;
        }

        @Override
        protected List<Card> doInBackground(String... params) {
            String board = params[0];
            List<Card> card=trelloAPI.getCardsByMember("me");
            List<Card> cardsByBoard=trelloAPI.getCardsByBoard(board);
            List<Card> common = new ArrayList<Card>();
            for(int i =0; i<card.size();i++) {
                for(int j = 0;j<cardsByBoard.size();j++){
                    if(card.get(i).getName().equals(cardsByBoard.get(j).getName()))
                        common.add(card.get(i));
                }
            }
            return common;
        }

        @Override
        protected void onPostExecute(List<Card> result) {
            setUpPieChart(result);
        }
    }


    String ellipsize(String input, int maxLength) {
        String ellip = "...";
        if (input == null || input.length() <= maxLength
                || input.length() < ellip.length()) {
            return input;
        }
        return input.substring(0, maxLength - ellip.length()).concat(ellip).concat(input.substring(input.length()-4,input.length()));
    }
}
