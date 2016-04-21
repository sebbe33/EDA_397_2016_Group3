package chalmers.eda397_2016_group3.slack;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.trello4j.Trello;
import org.trello4j.model.Board;

import java.util.List;

import chalmers.eda397_2016_group3.R;

/**
 * Created by Sebastian Blomberg on 2016-04-21.
 */
public class TrelloSetupFragment extends Fragment {
    @Override
    public void onCreate(Bundle bundleInstance) {
        super.onCreate(bundleInstance);

        TrelloApp trelloApp = TrelloAppFactory.createTrelloApp(this.getActivity(), bundleInstance);


        if(trelloApp.isAuthenticated()) {
            Trello trelloAPI = TrelloAppFactory.getTrelloAPIInterface(trelloApp);
            //new FetchBoardsTask().execute(trelloAPI);
            /*for(Board b : borads) {
                Log.d("debug", b.toString());
            }*/
        }



    }

    private class FetchBoardsTask extends AsyncTask<Trello, Integer, List<Board>> {

        @Override
        protected List<Board> doInBackground(Trello... params) {
            Trello trello = params[0];
            return trello.getBoardsByMember("me");
        }

        @Override
        protected void onPostExecute(List<Board> result) {
            for(Board b : result) {
                Log.d("debug", b.getName());
            }
        }
    }
}
