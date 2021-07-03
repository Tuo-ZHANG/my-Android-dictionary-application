package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import static com.example.myapplication.MainActivity.DICTIONARY;
import static com.example.myapplication.MainActivity.ENTRY;

public class HtmlsRecViewActivity extends AppCompatActivity {

    private RecyclerView htmlsRecView;
    private HtmlsRecViewAdapter adapter;
    private ArrayList<Entry> items;

    //    private Entry item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmls_rec_view);
        htmlsRecView = findViewById(R.id.htmlsRecView);
        adapter = new HtmlsRecViewAdapter(this);
//
        Intent intent = getIntent();
//        item.setDictionary(intent.getStringExtra(DICTIONARY));
//        item.setEntry(intent.getStringExtra(ENTRY));

        items = new ArrayList<>();

        items.add(new Entry(intent.getStringExtra(ENTRY), intent.getStringExtra(DICTIONARY)));
        adapter.setItems(items);
        htmlsRecView.setAdapter(adapter);
        htmlsRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }
}