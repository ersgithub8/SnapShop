package com.fyp.snapshop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fyp.snapshop.R;

public class WebViewPlaylist extends AppCompatActivity {

    WebView webView;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_playlist);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        url = getIntent().getExtras().getString("Key");
        webView.loadUrl(url

        );
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
        webView.goBack();}
        else {
        super.onBackPressed();}
    }
}
