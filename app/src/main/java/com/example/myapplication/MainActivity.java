package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView entriesRecView;
    private ArrayList<Entry> entries;
    public static final String ENTRY = "entry";
    public static final String DICTIONARY = "dictionary";
    private EntriesRecViewAdapter adapter;
    private HashMap<String, String> types = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillEntries();
        setUpRecyclerView();
    }

    private void fillEntries() {
        entries = new ArrayList<>();
        AssetManager assetManager = getAssets();
        try {
            String[] dictionaries = Arrays.copyOfRange(assetManager.list(""), 0, assetManager.list("").length - 2);
            System.out.println(dictionaries.length);
            for (String dictionary : dictionaries) {
                String[] files = assetManager.list(dictionary);
                for (String s : files) {
                    if (s.equals("s.css")) {
                        continue;
                    }
                    String token = s.substring(0, s.length() - 5);
                    if (types.containsKey(token)) {
                        types.put(token, types.get(token) + "," + dictionary);
                    } else {
                        types.put(token, dictionary);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // convert the hashmap to 2 dimension array
        Object[][] arr = new Object[types.size()][2];
        Set setEntries = types.entrySet();
        Iterator entriesIterator = setEntries.iterator();
        int i = 0;
        while (entriesIterator.hasNext()) {

            Map.Entry mapping = (Map.Entry) entriesIterator.next();

            arr[i][0] = mapping.getKey();
            arr[i][1] = mapping.getValue();

            i++;
        }
        // set entries
        for (int j = 0; j < arr.length; j++) {
            entries.add(new Entry(arr[j][0].toString(), arr[j][1].toString()));
        }
    }

    private void setUpRecyclerView() {
        entriesRecView = findViewById(R.id.entriesRecView);
        adapter = new EntriesRecViewAdapter(this);
        adapter.setEntries(entries);
        entriesRecView.setAdapter(adapter);
        entriesRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dict_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}