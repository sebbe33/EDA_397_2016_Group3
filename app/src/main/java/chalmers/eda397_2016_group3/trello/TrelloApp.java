package chalmers.eda397_2016_group3.trello;

/**
 * Created by Sebastian Blomberg on 2016-04-20.
 */
public interface TrelloApp {
    /**
     * Get then authentication URL for this Trello app, i.e. the http link to
     * the login prompt by Trello.
     * @return auth URL
     */
    String getAuthenticationURL();

    /**
     * Return the application identifier
     * @return id
     */
    String getApplicationID();

    /**
     * Return the name of this trello application
     * @return name
     */
    String getApplicationName();

    /**
     * Return the authentication expiration, i.e. how long
     * an authentication session lasts
     * @return
     */
    String getAuthenticationExpiration();

    /**
     * Returns if the user is authenticated
     * @return is authenticated?
     */
    boolean isAuthenticated();

    /**
     * Returns the authentication token, or null if the user is not authenticated
     * @return auth token
     */
    String getAuthenticationToken();

    /**
     * Get the selected board
     * @return board ID
     */
    String getSelectedBoardID();
}
