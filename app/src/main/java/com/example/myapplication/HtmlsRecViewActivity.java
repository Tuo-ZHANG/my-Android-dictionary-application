package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

import static com.example.myapplication.MainActivity.DICTIONARIES;
import static com.example.myapplication.MainActivity.ENTRY;

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
        String[] arr = intent.getStringExtra(DICTIONARIES).split(",");

        for (String s : arr) {
            items.add(new Entry(intent.getStringExtra(ENTRY), s));
        }


        adapter.setItems(items);
        htmlsRecView.setAdapter(adapter);
        htmlsRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}