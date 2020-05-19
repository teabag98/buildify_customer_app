package com.qinsley.mbcustomer.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.R;

public class Terms extends AppCompatActivity {
    private WebView mWebView;
    private RelativeLayout rlclose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_terms);
        mWebView = (WebView) findViewById(R.id.mWebView);
        rlclose = (RelativeLayout) findViewById(R.id.rlclose);
        rlclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWebView.setWebViewClient(new MyBrowser());
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(Consts.TERMS_URL);

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


}