package chalmers.eda397_2016_group3.slack;

import android.content.Context;
import android.os.Bundle;

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
        String authToken = bundle.getString(BUNDLE_TOKEN_KEY);

        if(authToken == null) {
            return new TrelloAppImpl(name, apiKey, authExp);
        }

        return new TrelloAppImpl(name, apiKey, authExp, authToken);
    }

    /**
     * Returns a Trello API interface for the specified Trello Application
     * @param trelloApp
     * @return trello API interface
     */
    public static Trello getTrelloAPIInterface(TrelloApp trelloApp) {
        return new TrelloImpl(trelloApp.getApplicationID(), trelloApp.getAuthenticationToken());
    }
}
