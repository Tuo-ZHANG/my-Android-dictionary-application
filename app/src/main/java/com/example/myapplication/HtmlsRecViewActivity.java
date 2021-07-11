package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.myapplication.MainActivity.DICTIONARY;
import static com.example.myapplication.MainActivity.ENTRY;

public class HtmlsRecViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmls_rec_view);
        RecyclerView htmlsRecView = findViewById(R.id.htmlsRecView);
        HtmlsRecViewAdapter adapter = new HtmlsRecViewAdapter(this);

        Intent intent = getIntent();

        ArrayList<Entry> items = new ArrayList<>();
        String[] arr = intent.getStringExtra(DICTIONARY).split(",");
        if (arr.length == 2) {
//            arr = Arrays.copyOfRange(arr, 0, 1);
            arr = ArrayUtils.removeElement(arr, "conjugation to lemma");
        }
        for (String s : arr) {
            items.add(new Entry(intent.getStringExtra(ENTRY), s));
        }


        adapter.setItems(items);
        htmlsRecView.setAdapter(adapter);
        htmlsRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }
}