package chalmers.eda397_2016_group3.trello;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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


import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;

import org.trello4j.Trello;
import org.trello4j.model.Board;
import org.trello4j.model.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chalmers.eda397_2016_group3.R;
import chalmers.eda397_2016_group3.utils.AdapterTuple;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class TrelloNotificationsFragment extends Fragment implements CardChangePoller.CardChangeListener {
    private TrelloApp trelloApp = null;
    private Trello trelloAPI = null;
    private List<AdapterTuple<String, String>> spinnerOptions = null;
    private static List<CardChangeNotifierService.CardChange> cardChanges = new ArrayList<>();

    Map mapList = new HashMap();

    private static List<CardChangeNotifierService.CardChange> dataSet = null;


    private static final int TIME_TO_AUTOMATICALLY_DISMISS_ITEM = 1200;
    RecyclerView recyclerView;
    List<String> description;
    String oldListID, newListId, cardName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardChangeNotifierService.registerCardChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trello_notifications, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        dataSet = CardChangeNotifierService.getCardChanges();



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        trelloApp = TrelloAppService.getTrelloApp(getActivity());
        trelloAPI = TrelloAppService.getTrelloAPIInterface(trelloApp);


        if(trelloApp.isAuthenticated()) {
            fetchList();
        }


    }

    private void fetchList() {

        new ListFetcher(trelloAPI).execute(trelloApp.getSelectedBoardID());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        CardChangeNotifierService.removeCardChangeListener(this);
    }

    @Override
    public void onCardChange(Card oldCard, Card newCard) {

        if (oldCard == null || newCard == null) {
            Log.d("debug", "Card change ignored: " + oldCard + " - " + newCard);
            return;
        }

        List<CardChangeUtils.CardAttribute> changedAttributes = CardChangeUtils.getCardChange(oldCard, newCard);
        if (!changedAttributes.contains(CardChangeUtils.CardAttribute.LIST_ID)) {
            Log.d("debug", "Card change ignored. Does not contain a change of list id");
            return;
        }

        // Stack the card change
        cardChanges.add(new CardChangeNotifierService.CardChange(oldCard, newCard));


        //refresh notification fragment
        getFragmentManager().beginTransaction()
                .detach(TrelloNotificationsFragment.this)
                .attach(TrelloNotificationsFragment.this)
                .commit();


    }



    private void init(RecyclerView recyclerView) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);


        org.trello4j.model.List list;

        description=new ArrayList<String>();
        for (CardChangeNotifierService.CardChange cardChange : dataSet) {


            cardName=cardChange.getNewCard().getName();
            oldListID = cardChange.getOldCard().getIdList();
            newListId = cardChange.getNewCard().getIdList();


            description.add(cardName + " moved to " +mapList.get(newListId) + " from " + mapList.get(oldListID));

        }


        final MyBaseAdapter adapter = new MyBaseAdapter(description);

        if (description != null)
            recyclerView.setAdapter(adapter);

        final SwipeToDismissTouchListener<RecyclerViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewAdapter(recyclerView),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onPendingDismiss(RecyclerViewAdapter recyclerView, int position) {

                            }

                            @Override
                            public void onDismiss(RecyclerViewAdapter view, int position) {
                                adapter.remove(position);
                            }
                        });
        touchListener.setDismissDelay(TIME_TO_AUTOMATICALLY_DISMISS_ITEM);
        recyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        recyclerView.setOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        recyclerView.addOnItemTouchListener(new SwipeableItemClickListener(getActivity().getBaseContext(),
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (view.getId() == R.id.txt_delete) {
                            touchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.txt_undo) {
                            touchListener.undoPendingDismiss();
                        } else { // R.id.txt_data
                            Toast.makeText(getActivity().getBaseContext(), "Position " + position, LENGTH_SHORT).show();
                        }
                    }
                }));
    }



    static class MyBaseAdapter extends RecyclerView.Adapter<MyBaseAdapter.MyViewHolder> {

        private static final int SIZE = 100;

        private List<CardChangeNotifierService.CardChange> mDataSet = null;

        private List<String> mDescription = null;

        MyBaseAdapter(List<String> dataSet) {
            mDescription = dataSet;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            if (mDescription != null) {
                holder.dataTextView.setText(mDescription.get(position));
            } else
                Log.d("debugn", "nothing to display" + mDescription);
        }

        @Override
        public int getItemCount() {
            return mDescription.size();
        }

        public void remove(int position) {
            mDescription.remove(position);
            notifyItemRemoved(position);
        }

        static class MyViewHolder extends RecyclerView.ViewHolder {

            TextView dataTextView;

            MyViewHolder(View view) {
                super(view);
                dataTextView = (TextView) view.findViewById(R.id.txt_data);
            }
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

            }

            init((RecyclerView) getView().findViewById(R.id.recycler_view));
        }
    }
}