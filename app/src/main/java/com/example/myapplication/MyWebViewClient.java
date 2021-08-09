package com.example.myapplication;

import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideKeyEvent (WebView view, KeyEvent event) {
        // Do something with the event here
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading (WebView view, WebResourceRequest request) {
        // reject anything other
        return true;
    }
}
