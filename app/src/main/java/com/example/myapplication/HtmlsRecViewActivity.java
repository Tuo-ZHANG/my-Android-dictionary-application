package com.example.myapplication;


import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


import java.io.File;
import java.util.ArrayList;

public class HtmlsRecViewActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmls_rec_view);
        RecyclerView htmlsRecView = findViewById(R.id.htmlsRecView);


        HtmlsRecViewAdapter adapter = new HtmlsRecViewAdapter(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        ArrayList<Entry> items = new ArrayList<>();
        String[] listOfDictionaries = intent.getStringExtra(DICTIONARIES).split(",");

        for (String s : listOfDictionaries) {
            items.add(new Entry(intent.getStringExtra(ENTRY), s));
        }

        adapter.setItems(items);
        htmlsRecView.setAdapter(adapter);
        htmlsRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(htmlsRecView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}