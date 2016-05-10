package chalmers.eda397_2016_group3.trello;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.trello4j.Trello;
import org.trello4j.model.Card;

import java.util.ArrayList;
import java.util.List;

import chalmers.eda397_2016_group3.R;
import chalmers.eda397_2016_group3.adapter.GridAdapter;
import chalmers.eda397_2016_group3.utils.AdapterTuple;

/**
 * Created by N10 on 4/26/2016.
 */
public class TasksFragment extends Fragment implements TaskListItemAdapter.OnCardClickListener {
    private TrelloApp trelloApp = null;
    private Trello trelloAPI = null;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private List<AdapterTuple<String,String>> listItems = null;

    public static Fragment newInstance(Context context) {
        TasksFragment f = new TasksFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_feature, null);

        Context c=getActivity();
        mRecyclerView = (RecyclerView)root.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(c,1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //for debugging purpose only
        List<String> list=new ArrayList<String>();
        list.add("A user want to be able to synchronise with the trello project");
        list.add("As a navigator I want to able to set a timer");
        list.add("As a user I want to able to pause the timer");
        list.add("A user want to be able to synchronise with the trello project");
        list.add("As a navigator I want to able to set a timer");
        list.add("As a user I want to able to pause the timer");
        list.add("A user want to be able to synchronise with the trello project");
        list.add("As a navigator I want to able to set a timer");
        list.add("As a user I want to able to pause the timer");


        mAdapter = new GridAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onStart () {
        super.onStart();
        trelloApp = TrelloAppService.getTrelloApp(getActivity());
        trelloAPI = TrelloAppService.getTrelloAPIInterface(trelloApp);

        if(trelloApp.isAuthenticated()) {
            fetchTasks();
        }

    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void fetchTasks() {
        new CardFetcher(trelloAPI).execute(trelloApp.getSelectedBoardID());
    }

    @Override
    public void onCardClicked(Card c) {
        // A card got clicked, time to start a new intent with the card send along in a bundle.
        Bundle bundle = new Bundle();
        bundle.putString(TrelloAppService.TRELLO_CARD_ID, c.getId());
        Intent intent = new Intent(getActivity(), PunchInActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class CardFetcher extends AsyncTask<String, Integer, List<Card>> {
        private final Trello trelloAPI;

        public CardFetcher(Trello trelloAPI) {
            this.trelloAPI = trelloAPI;
        }

        @Override
        protected List<Card> doInBackground(String... params) {
            String board = params[0];
            return trelloAPI.getCardsByBoard(board);
        }

        @Override
        protected void onPostExecute(List<Card> result) {
            mRecyclerView.setAdapter(new TaskListItemAdapter(result, TasksFragment.this));
        }
    }

}