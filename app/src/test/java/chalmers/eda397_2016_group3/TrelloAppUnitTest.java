package chalmers.eda397_2016_group3;

import org.junit.Test;

import chalmers.eda397_2016_group3.trello.TrelloApp;
import chalmers.eda397_2016_group3.trello.TrelloAppImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by sebastianblomberg on 2016-05-02.
 */
public class TrelloAppUnitTest {

    @Test
    public void testGetters() throws Exception {
        String applicationID = "asdjlkjdoi12e3e",
                applicationName = "Some name",
                authenticationExpiration = "1days",
                authenticationToken = "sometoken";

        TrelloApp trelloApp = new TrelloAppImpl(applicationID, applicationName,
                authenticationExpiration);

        assertEquals(applicationID, trelloApp.getApplicationID());
        assertEquals(applicationName, trelloApp.getApplicationName());
        assertEquals(authenticationExpiration, trelloApp.getAuthenticationExpiration());
        assertNull("No auth token should be set", trelloApp.getAuthenticationToken());

        trelloApp = new TrelloAppImpl(applicationID, applicationName,
                authenticationExpiration, authenticationToken);

        assertEquals(applicationID, trelloApp.getApplicationID());
        assertEquals(applicationName, trelloApp.getApplicationName());
        assertEquals(authenticationExpiration, trelloApp.getAuthenticationExpiration());
        assertEquals(authenticationToken, trelloApp.getAuthenticationToken());
    }

    @Test
    public void testAuthURL() throws Exception {
        String applicationID = "asdjlkjdoi12e3e",
                applicationName = "Some name",
                authenticationExpiration = "1days",
                authenticationToken = "sometoken";
        String urlEncodedAuthURL = "https://trello.com/1/connect?key="+applicationID+"&name=Some+name" +
                "&response_type=token&scope=read,write" +
                "&expiration="+authenticationExpiration;

        TrelloApp trelloApp = new TrelloAppImpl(applicationID, applicationName,
                authenticationExpiration);
        assertEquals("The input in the constructor should be URL encoded",
                urlEncodedAuthURL, trelloApp.getAuthenticationURL());
    }

    @Test
    public void testSetters() throws Exception {
        TrelloAppImpl trelloApp = new TrelloAppImpl("asdjlkjdoi12e3e", "Some name",
                "1days", "sometoken");

        String selectedBoardID = "sdkfj34rfsd";
        trelloApp.setSelectedBoardID(selectedBoardID);
        assertEquals(selectedBoardID, trelloApp.getSelectedBoardID());

        String authenticationToken = "askdjlk2e1asdssdasdsdsad2";
        trelloApp.setAuthenticationToken(authenticationToken);
        assertEquals(authenticationToken, trelloApp.getAuthenticationToken());
    }
}
