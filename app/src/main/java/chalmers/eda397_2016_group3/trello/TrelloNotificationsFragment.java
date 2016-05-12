package chalmers.eda397_2016_group3.trello;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.trello4j.Trello;
import org.trello4j.model.Board;
import org.trello4j.model.Card;

import java.util.ArrayList;
import java.util.List;

import chalmers.eda397_2016_group3.R;
import chalmers.eda397_2016_group3.utils.AdapterTuple;

public class TrelloNotificationsFragment extends Fragment implements CardChangePoller.CardChangeListener {
    private TrelloApp trelloApp = null;
    private Trello trelloAPI = null;
    private List<AdapterTuple<String,String>> spinnerOptions = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardChangeNotifierService.registerCardChangeListener(this);

        // READ!!!!!!! Use CardChangeNotifierService.getCardChanges(); to get a list of all card changes
        // Use CardChangeNotifierService.clear(); to clear all changes
        // Use  CardChangeNotifierService.removeCardChange(cardchange); to remove on change
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trello_notifications, container, false);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        CardChangeNotifierService.removeCardChangeListener(this);
    }

    @Override
    public void onCardChange(Card oldCard, Card newCard) {
        Log.d("debug", "Got card change while viewing the notifications fragment");
        // TODO : Update the list
    }
}
