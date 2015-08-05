package hm.orz.chaos114.android.skipgunosy;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.webView)
    WebView mWebView;
    @Bind(R.id.loading)
    View mLoading;
    @Bind(R.id.attention_text)
    View mAttentionText;

    private boolean urlLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String url;
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            url = uri.toString();
            mLoading.setVisibility(View.VISIBLE);
            mAttentionText.setVisibility(View.GONE);
        } else {
            mLoading.setVisibility(View.GONE);
            mAttentionText.setVisibility(View.VISIBLE);
            return;
        }

        setWebViewSetting();
        mWebView.loadUrl(url);
    }

    private void setWebViewSetting() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new HolderInterface(), "holder");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (!uri.getHost().equalsIgnoreCase("gunosy.com")) {
                    startBrowser(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                tryFetchUrl();
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // for fast check url
                Log.d(TAG, "newProgress = " + newProgress);
                tryFetchUrl();
            }
        });
    }

    private void tryFetchUrl() {
        mWebView.loadUrl("javascript:holder.setUrl($('.article__media a').attr('href'))");
    }

    private void startBrowser(String url) {
        if (url.equals("undefined")) {
            return;
        }
        if (urlLoaded) {
            return;
        }
        urlLoaded = true;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);

        finish();
    }

    private class HolderInterface {
        @JavascriptInterface
        public void setUrl(String url) {
            Log.d(TAG, "url = " + url);
            startBrowser(url);
        }
    }
}
