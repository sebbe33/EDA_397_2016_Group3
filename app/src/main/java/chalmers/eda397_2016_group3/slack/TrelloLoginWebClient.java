package chalmers.eda397_2016_group3.slack;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Sebastian Blomberg on 2016-04-19.
 */
public class TrelloLoginWebClient extends WebViewClient {
    private TrelloTokenReceivedListener receivedListener;
    private SlackAuthorizationAbortedListener abortedListener;

    public TrelloLoginWebClient(WebView view, TrelloTokenReceivedListener receivedListener,
                                SlackAuthorizationAbortedListener abortedListener) {
        this.receivedListener = receivedListener;
        this.abortedListener = abortedListener;

        // Add a Javascript method that is callable in Java to be able to extract the HTML
        view.addJavascriptInterface(new MyJavascriptInterface(), "HtmlViewer");
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d("debug", url);
        if(!url.contains("https://trello.com/login") && !url.contains("https://trello.com/1/")) {
            if(abortedListener != null) {
                abortedListener.onAuthorizationAborted();
            }
        } else if(url.equalsIgnoreCase("https://trello.com/1/token/approve")) {
            // Add a Javascript method that is callable in Java to be able to extract the HTML
            view.addJavascriptInterface(new MyJavascriptInterface(), "HtmlViewer");
            view.loadUrl("javascript:window.HtmlViewer.showHTML" +
                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
        }
    }

    interface TrelloTokenReceivedListener {
        void onTokenReceived(String token);
    }

    interface SlackAuthorizationAbortedListener {
        void onAuthorizationAborted();
    }

    private class MyJavascriptInterface {
        @android.webkit.JavascriptInterface
        public void showHTML(String html) {
            if(html.contains("To complete the process,")) {
                String token = html.substring(html.indexOf("<pre>") + 5, html.indexOf("</pre>"));
                token = token.trim();
                // Notify listener that token has been received
                if(receivedListener != null) {
                    receivedListener.onTokenReceived(token);
                }
            } else {
                Log.e("slack", "Error: The authentication page has changed and the parsing code " +
                        "needs to be updated");
            }
        }
    }
}
