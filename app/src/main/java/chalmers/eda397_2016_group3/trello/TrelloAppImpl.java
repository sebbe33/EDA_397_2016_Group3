package chalmers.eda397_2016_group3.trello;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Sebastian Blomberg on 2016-04-19.
 */
public class TrelloAppImpl implements TrelloApp {
    private String applicationID, applicationName, authenticationExpiration;
    private String authenticationToken = null;
    private String selectedBoardID;

    public TrelloAppImpl(String applicationID, String applicationName,
                         String authenticationExpiration) {
        this.applicationID = applicationID;
        this.applicationName = applicationName;
        this.authenticationExpiration = authenticationExpiration;
    }

    public TrelloAppImpl(String applicationID, String applicationName,
                         String authenticationExpiration, String authenticationToken) {
        this(applicationID, applicationName, authenticationExpiration);
        this.authenticationToken = authenticationToken;
    }

    @Override
    public String getAuthenticationURL() {
        try {
            return "https://trello.com/1/connect?key="+ URLEncoder.encode(applicationID,"UTF-8")
                    + "&name=" + URLEncoder.encode(applicationName,"UTF-8")
                    + "&response_type=token&scope=read,write"
                    + "&expiration=" + URLEncoder.encode(authenticationExpiration,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getApplicationID() {
        return applicationID;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public String getAuthenticationExpiration() {
        return authenticationExpiration;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticationToken != null && !authenticationToken.isEmpty();
    }

    @Override
    public String getAuthenticationToken() {
        return authenticationToken;
    }

    @Override
    public String getSelectedBoardID() {
        return selectedBoardID;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public void setSelectedBoardID(String selectedBoardID) {
        this.selectedBoardID = selectedBoardID;
    }
}
