package hm.orz.chaos114.android.skipgunosy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

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

    private InterstitialAd mInterstitial;
    private boolean urlLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initAds();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_license:
                Intent intent = new Intent(this, LicenseActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void initAds() {
        mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId(getString(R.string.banner_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitial.loadAd(adRequest);
    }

    private void displayInterstitial() {
        if (mInterstitial.isLoaded()) {
            mInterstitial.show();
        }
    }

    private void tryFetchUrl() {
        mWebView.loadUrl("javascript:a=document.getElementsByTagName('a');for(var i=0;i<a.length;i++){if(a[i].text.includes('元記事'))holder.setUrl(a[i].href)}");
    }

    private void startBrowser(final String url) {
        if (url.equals("undefined")) {
            return;
        }
        if (urlLoaded) {
            return;
        }
        urlLoaded = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayInterstitial();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

                finish();
            }
        });
    }

    private class HolderInterface {
        @JavascriptInterface
        public void setUrl(String url) {
            Log.d(TAG, "url = " + url);
            startBrowser(url);
        }
    }
}
