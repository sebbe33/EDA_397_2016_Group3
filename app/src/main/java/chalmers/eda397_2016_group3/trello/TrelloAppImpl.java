package chalmers.eda397_2016_group3.trello;

/**
 * Created by Sebastian Blomberg on 2016-04-19.
 */
public class TrelloAppImpl implements TrelloApp {
    private String applicationID, applicationName, authenticationExpiration;
    private String authenticationToken = null;


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
        return "https://trello.com/1/connect?key="+ applicationID
                + "&name=" + applicationName
                + "&response_type=token&scope=read,write"
                + "&expiration=" + authenticationExpiration;
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
        return authenticationToken != null;
    }

    @Override
    public String getAuthenticationToken() {
        return authenticationToken;
    }

}
