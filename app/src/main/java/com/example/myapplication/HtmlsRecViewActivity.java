package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.example.myapplication.MainActivity.DICTIONARIES;
import static com.example.myapplication.MainActivity.ENTRY;

public class HtmlsRecViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MethodInfo", "onCreate of HtmlsRecViewActivity called");
        setContentView(R.layout.activity_htmls_rec_view);
        RecyclerView htmlsRecView = findViewById(R.id.htmls_rec_view);

        HtmlsRecViewAdapter adapterLocal = new HtmlsRecViewAdapter(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        ArrayList<Entry> items = new ArrayList<>();
        String[] listOfDictionaries = intent.getStringExtra(DICTIONARIES).split(",");

        for (String s : listOfDictionaries) {
            items.add(new Entry(intent.getStringExtra(ENTRY), s));
        }

        adapterLocal.setItems(items);
        htmlsRecView.setAdapter(adapterLocal);
        htmlsRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(htmlsRecView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}