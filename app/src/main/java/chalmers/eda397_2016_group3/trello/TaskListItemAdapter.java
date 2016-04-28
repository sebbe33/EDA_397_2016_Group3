package chalmers.eda397_2016_group3.trello;

import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.trello4j.model.Card;
import org.w3c.dom.Text;

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
    private OnCardClickListener onClickListener = null;

    public TaskListItemAdapter(List<Card> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.onClickListener = listener;
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
        TextView textView = (TextView)holder.mTextView.findViewById(R.id.sub_card);
        textView.setText(cards.get(position).getName());
        holder.mTextView.setOnClickListener(new OnClickListenerWrapper(cards.get(position), onClickListener));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    private class OnClickListenerWrapper implements View.OnClickListener {
        private Card c;
        private OnCardClickListener listener;
        public OnClickListenerWrapper(Card c, OnCardClickListener listener) {
            this.c = c;
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            listener.onCardClicked(c);
        }
    }

    public interface OnCardClickListener {
        void onCardClicked(Card c);
    }
}
