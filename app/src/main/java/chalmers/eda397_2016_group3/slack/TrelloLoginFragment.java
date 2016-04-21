package chalmers.eda397_2016_group3.slack;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.trello4j.Trello;
import org.trello4j.model.Board;

import java.util.List;

import chalmers.eda397_2016_group3.R;

/**
 * Created by Sebastian Blomberg on 2016-04-17.
 */
public class TrelloLoginFragment extends Fragment
        implements TrelloLoginWebClient.TrelloTokenReceivedListener,
        TrelloLoginWebClient.SlackAuthorizationAbortedListener {

    private Bundle activeBundle = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slack_login, container, false);

        WebView myWebView = (WebView) view.findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new TrelloLoginWebClient(myWebView, this, this));

        TrelloApp trelloApp = TrelloAppFactory.createTrelloApp(this.getActivity(), savedInstanceState);
        myWebView.loadUrl(trelloApp.getAuthenticationURL());

        return view;
    }

    @Override
    public void onCreate(Bundle bundleInstance) {
        super.onCreate(bundleInstance);
        activeBundle = bundleInstance;
   }

    @Override
    public void onAuthorizationAborted() {

    }

    @Override
    public void onTokenReceived(String token) {
        activeBundle.putString(TrelloAppFactory.BUNDLE_TOKEN_KEY, token);
    }
}
