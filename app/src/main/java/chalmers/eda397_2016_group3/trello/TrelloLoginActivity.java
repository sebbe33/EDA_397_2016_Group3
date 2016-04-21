package chalmers.eda397_2016_group3.trello;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import chalmers.eda397_2016_group3.R;

public class TrelloLoginActivity extends ActionBarActivity
        implements TrelloLoginWebClient.TrelloTokenReceivedListener,
        TrelloLoginWebClient.SlackAuthorizationAbortedListener {
    public static final int
            LOGIN_REQUEST_CODE = 0xFF01,
            LOGIN_SUCCEEDED = 0x01,
            LOGIN_FAILED = 0x02;

    private Bundle loginBundle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trello_login);

        loginBundle = getIntent().getExtras();

        WebView myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new TrelloLoginWebClient(myWebView, this, this));

        TrelloApp trelloApp = TrelloAppFactory.createTrelloApp(this, savedInstanceState);
        myWebView.loadUrl(trelloApp.getAuthenticationURL());
    }

    @Override
    public void onAuthorizationAborted() {
        Intent returnIntent = new Intent();
        returnIntent.putExtras(loginBundle);
        setResult(LOGIN_FAILED, returnIntent);
        finish();
    }

    @Override
    public void onTokenReceived(String token) {
        Intent returnIntent = new Intent();

        if(loginBundle != null) {
            loginBundle.putString(TrelloAppFactory.BUNDLE_TOKEN_KEY, token);
            returnIntent.putExtras(loginBundle);
            Log.d("debug", "Saved token: " + token + " to bundle");
            setResult(LOGIN_SUCCEEDED, returnIntent);
        } else {
            Log.e("trello", "Got token: " + token + ", but bundle was null");
            returnIntent.putExtras(loginBundle);
            setResult(LOGIN_FAILED, returnIntent);
        }
        finish();
    }
}
