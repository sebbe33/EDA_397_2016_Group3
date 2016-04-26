package chalmers.eda397_2016_group3.trello;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.trello4j.Trello;
import org.trello4j.TrelloImpl;

import chalmers.eda397_2016_group3.R;

/**
 * Created by Sebastian on 2016-04-21.
 */
public class TrelloAppService {
    public static final String BUNDLE_TOKEN_KEY = "TRELLO_TOKEN_KEY",
            TRELLO_SELECTED_BOARD_ID ="TRELLO_SELECTED_BOARD_ID";
    private static final String TRELLO_SHARED_PREFERENCES = "TRELLO_SHARED_PREFERENCES";

    private static TrelloAppImpl instance = null;

    /**
     * Instansiates a Trello app based on data stored in string.xml and saved bundle.
     * @param context
     * @return a Trello app
     */
    public static TrelloApp getTrelloApp(Context context) {
        if(instance != null) {
            return instance;
        }

        String name = context.getResources().getString(R.string.app_name);
        String apiKey = context.getResources().getString(R.string.TRELLO_API_KEY);
        String authExp = context.getResources().getString(R.string.TRELLO_AUTHENTICATION_EXPIRATION);

        // Get auth token from shared preferences
        SharedPreferences sharedpreferences =
                context.getSharedPreferences(TRELLO_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String authToken = sharedpreferences.getString(BUNDLE_TOKEN_KEY, "");
        String selectedBoardID = sharedpreferences.getString(TRELLO_SELECTED_BOARD_ID, "");

        if(authToken == "") {
            instance = new TrelloAppImpl(apiKey, name, authExp);
        } else {
            instance = new TrelloAppImpl(apiKey, name, authExp, authToken);
            instance.setSelectedBoardID(selectedBoardID);
        }

        return instance;
    }

    /**
     * Returns a Trello API interface for the specified Trello Application
     * @return trello API interface
     */
    public static Trello getTrelloAPIInterface(TrelloApp trelloApp) {
        Log.d("debug", "Creating new trello API interface (APIKey: " + trelloApp.getApplicationID()
                + ", Token: " + trelloApp.getAuthenticationToken());
        return new TrelloImpl(trelloApp.getApplicationID(), trelloApp.getAuthenticationToken());
    }

    public static void setAuthenticationToken(String token, Context context) {
        SharedPreferences sharedpreferences =
                context.getSharedPreferences(TRELLO_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(BUNDLE_TOKEN_KEY, token);
        editor.commit();

        if(instance == null) {
            getTrelloApp(context);
        }
        instance.setAuthenticationToken(token);
    }

    public static void setSelectedBoardID(String boardID, Context context) {
        SharedPreferences sharedpreferences =
                context.getSharedPreferences(TRELLO_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(TRELLO_SELECTED_BOARD_ID, boardID);
        editor.commit();

        if(instance == null) {
            getTrelloApp(context);
        }
        instance.setSelectedBoardID(boardID);
    }
}
