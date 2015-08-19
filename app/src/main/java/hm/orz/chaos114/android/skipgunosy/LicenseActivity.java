package hm.orz.chaos114.android.skipgunosy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LicenseActivity extends AppCompatActivity {

    @Bind(R.id.webView)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        ButterKnife.bind(this);

        mWebView.loadUrl("file:///android_asset/license.html");
    }
}
