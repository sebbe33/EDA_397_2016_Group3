package chalmers.eda397_2016_group3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import chalmers.eda397_2016_group3.adapter.GridAdapter;

/**
 * Created by N10 on 4/26/2016.
 */
public class FragmentFeature extends Fragment {


    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;


    public static Fragment newInstance(Context context) {
        FragmentFeature f = new FragmentFeature();
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



}