package chalmers.eda397_2016_group3.trello;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.trello4j.model.Card;

import java.util.List;

import chalmers.eda397_2016_group3.R;

/**
 * Created by sebastianblomberg on 2016-04-27.
 */
public class TaskListItemAdapter extends RecyclerView.Adapter<TaskListItemAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = v;
        }
    }

    private List<Card> cards;

    public TaskListItemAdapter(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public TaskListItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_card_feature, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(TaskListItemAdapter.ViewHolder holder, int position) {
        ((TextView)holder.mTextView.findViewById(R.id.sub_card))
                .setText(cards.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
