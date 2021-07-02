package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.example.myapplication.MainActivity.DICTIONARY;
import static com.example.myapplication.MainActivity.ENTRY;

public class WebsiteActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setAllowFileAccess(true);

        Intent intent = getIntent();
        String entry = intent.getStringExtra(ENTRY);
        String dictionary = intent.getStringExtra(DICTIONARY);
        webView.loadUrl("file:///android_asset/" + dictionary + "/" + entry + ".html");
        webView.setWebViewClient(new WebViewClient());
    }
}