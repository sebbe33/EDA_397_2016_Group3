package chalmers.eda397_2016_group3.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import chalmers.eda397_2016_group3.trello.PunchInActivity;
import chalmers.eda397_2016_group3.R;


public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {



    List<String> list = new ArrayList<String>();


    public GridAdapter(List<String> feature) {
        super();

        list=feature;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_card_feature, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {


        Log.d("debug_app", list.get(i));
        viewHolder.cardblock.setText(list.get(i));

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Context context = v.getContext();
                String title = viewHolder.cardblock.getText().toString();

                intent = new Intent(v.getContext(), PunchInActivity.class);

                intent.putExtra("title", title);
                context.startActivity(intent);


                Toast.makeText(context, "Feature clicked", Toast.LENGTH_LONG).show();


            }
        });

    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgThumbnail;
        public TextView cardblock;
        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

            cardblock = (TextView)itemView.findViewById(R.id.feature_description);
        }
    }



}
