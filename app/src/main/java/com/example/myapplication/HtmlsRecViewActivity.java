package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.myapplication.MainActivity.DICTIONARY;
import static com.example.myapplication.MainActivity.ENTRY;

public class HtmlsRecViewActivity extends AppCompatActivity {

    private RecyclerView htmlsRecView;
    private HtmlsRecViewAdapter adapter;
    private ArrayList<Entry> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmls_rec_view);
        htmlsRecView = findViewById(R.id.htmlsRecView);
        adapter = new HtmlsRecViewAdapter(this);

        Intent intent = getIntent();

        items = new ArrayList<>();
        String[] arr = intent.getStringExtra(DICTIONARY).split(",");
        if (arr.length == 2) {
            arr = Arrays.copyOfRange(arr, 0, 1);
        }
        for (String s : arr) {
            items.add(new Entry(intent.getStringExtra(ENTRY), s));
        }

        adapter.setItems(items);
        htmlsRecView.setAdapter(adapter);
        htmlsRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }
}