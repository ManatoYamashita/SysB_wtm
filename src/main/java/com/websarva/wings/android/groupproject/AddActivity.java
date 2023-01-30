package com.websarva.wings.android.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity{

    // 東京都市大学の公式サイトに飛ばす広告（悪質極まりない）

    public String URL = "https://www.tcu.ac.jp/academics/informatics/information/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_show);

        //インテントオブジェクトを取得
        Intent intent = getIntent();

        WebView myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(URL);
    }
}