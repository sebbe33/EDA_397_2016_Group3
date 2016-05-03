package chalmers.eda397_2016_group3.trello;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trello_login);

        WebView myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new TrelloLoginWebClient(myWebView, this, this));

        TrelloApp trelloApp = TrelloAppService.getTrelloApp(this);
        myWebView.loadUrl(trelloApp.getAuthenticationURL());
    }

    @Override
    public void onAuthorizationAborted() {
        setResult(LOGIN_FAILED);
        finish();
    }

    @Override
    public void onTokenReceived(String token) {
        TrelloAppService.setAuthenticationToken(token, this);
        Log.d("debug", "Saved token: " + token + " to shared preferences");
        setResult(LOGIN_SUCCEEDED);
        finish();
    }
}
