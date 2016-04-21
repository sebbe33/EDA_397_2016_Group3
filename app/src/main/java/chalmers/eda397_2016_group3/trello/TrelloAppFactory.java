package chalmers.eda397_2016_group3.trello;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.trello4j.Trello;
import org.trello4j.TrelloImpl;

import chalmers.eda397_2016_group3.R;

/**
 * Created by Sebastian on 2016-04-21.
 */
public class TrelloAppFactory {
    public static final String BUNDLE_TOKEN_KEY = "TRELLO_TOKEN_KEY";

    /**
     * Instansiates a Trello app based on data stored in string.xml and saved bundle.
     * @param context
     * @param bundle
     * @return a Trello app
     */
    public static TrelloApp createTrelloApp(Context context, Bundle bundle) {
        String name = context.getResources().getString(R.string.app_name);
        String apiKey = context.getResources().getString(R.string.TRELLO_API_KEY);
        String authExp = context.getResources().getString(R.string.TRELLO_AUTHENTICATION_EXPIRATION);
        String authToken = null;
        if(bundle != null) {
            authToken = bundle.getString(BUNDLE_TOKEN_KEY);
        }

        if(authToken == null) {
            return new TrelloAppImpl(apiKey, name, authExp);
        }

        return new TrelloAppImpl(apiKey, name, authExp, authToken);
    }

    /**
     * Returns a Trello API interface for the specified Trello Application
     * @param trelloApp
     * @return trello API interface
     */
    public static Trello getTrelloAPIInterface(TrelloApp trelloApp) {
        Log.d("debug", "Creating new trello API interface (APIKey: " + trelloApp.getApplicationID()
                + ", Token: " + trelloApp.getAuthenticationToken());
        return new TrelloImpl(trelloApp.getApplicationID(), trelloApp.getAuthenticationToken());
    }
}
