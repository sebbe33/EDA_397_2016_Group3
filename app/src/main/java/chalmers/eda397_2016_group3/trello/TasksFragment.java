package chalmers.eda397_2016_group3.trello;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.trello4j.Trello;
import org.trello4j.model.Card;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List<org.trello4j.model.List> listNames=null;
    private List<String> list=null;
    private List<String> listId=null;
    private Spinner spinnerWidget;
    private Bundle savedState = null;
    Map mapList = new HashMap();
    private ViewGroup viewGroup=null;
    private int selectedIndex=-1;

    public static Fragment newInstance(Context context) {
        TasksFragment f = new TasksFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_feature, null);

        viewGroup=root;
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


       /* mAdapter = new GridAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
       */
        return root;
    }

    @Override
    public void onStart () {
        super.onStart();
        trelloApp = TrelloAppService.getTrelloApp(getActivity());
        trelloAPI = TrelloAppService.getTrelloAPIInterface(trelloApp);


        if(trelloApp.isAuthenticated()) {
            fetchList();
            if(mapList!=null){


            }

        }
        else
        {
            Toast.makeText(getActivity(),"Please wait",Toast.LENGTH_SHORT).show();
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

    private void fetchList() {

        new ListFetcher(trelloAPI).execute(trelloApp.getSelectedBoardID());



        if(listNames==null){

            Toast.makeText(getActivity(),"Please wait",Toast.LENGTH_SHORT).show();
        }


    }


    private void fetchTasks(String listId) {

        new CardFetcher(trelloAPI).execute(listId);
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

            return trelloAPI.getCardsByList(board);
        }

        @Override
        protected void onPostExecute(List<Card> result) {



            mRecyclerView.setAdapter(new TaskListItemAdapter(result, TasksFragment.this));
        }
    }

    private class ListFetcher extends AsyncTask<String, Integer, List<org.trello4j.model.List>> {
        private final Trello trelloAPI;

        public ListFetcher(Trello trelloAPI) {
            this.trelloAPI = trelloAPI;
        }

        @Override
        protected List<org.trello4j.model.List> doInBackground(String... params) {
            String board = params[0];

            return trelloAPI.getListByBoard(board);
        }

        @Override
        protected void onPostExecute(List<org.trello4j.model.List> result) {


            for (org.trello4j.model.List listObject : result) {

                mapList.put(listObject.getId(), listObject.getName());
                Log.d("debugn10list",""+listObject.getId());
            }
            list=new ArrayList<>(mapList.values());
            listId=new ArrayList<>(mapList.keySet());

            spinnerWidget= (Spinner) getView().findViewById(R.id.list_spinner);
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerWidget.setAdapter(arrayAdapter);
            if(selectedIndex>-1){
                spinnerWidget.setSelection(selectedIndex);
            }

            spinnerWidget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here

                    selectedIndex=position;
                    fetchTasks(listId.get(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });

            listNames = result;
        }
    }



}