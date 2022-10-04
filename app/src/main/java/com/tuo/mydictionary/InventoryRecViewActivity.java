package com.tuo.mydictionary;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InventoryRecViewActivity extends AppCompatActivity {
    DictionaryService dictionaryService;
    private ArrayList<String> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_rec_view);
        RecyclerView inventoryRecView = findViewById(R.id.inventory_rec_view);

        dictionaryService = new DictionaryService(InventoryRecViewActivity.this);

        dictionaryService.getInventory(new DictionaryService.InventoryResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(InventoryRecViewActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(ArrayList<String> inventory) {
                entries = inventory;
                InventoryRecViewAdapter adapter = new InventoryRecViewAdapter(InventoryRecViewActivity.this);
                adapter.setEntries(entries);
                inventoryRecView.setAdapter(adapter);
                inventoryRecView.setLayoutManager(new LinearLayoutManager(InventoryRecViewActivity.this));
            }
        });
    }
}
