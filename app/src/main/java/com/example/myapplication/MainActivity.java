package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Entry> entries;
    public static final String ENTRY = "entry";
    public static final String DICTIONARIES = "dictionaries";
    private EntriesRecViewAdapter adapter;
    private TreeMap<String, String> types = new TreeMap<>();
    WebView webView;
    String path = "testdict.mdx";
    String word = "society";
//    private int EXTERNAL_STORAGE_PERMISSION_CODE = 1;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("jni-layer");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.html_card_view);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        fillEntries();
//        setUpRecyclerView();

        webView = findViewById(R.id.myWebView);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        File dict = new File(getExternalFilesDir(null), path);
        if (dict.exists()) {

            Log.i("FileInfo", "the absolute path of the parent directory is " + getExternalFilesDir(null).getAbsolutePath());
            Log.i("FileInfo", "the absolute path of the file is " + dict.getAbsolutePath());
        } else {
            Log.i("FileInfo", "the file cannot be found");
        }

        // Example of a call to a native method
        String queryReturnedValue = entryPoint(dict.getAbsolutePath(), word);
//        System.out.println(queryReturnedValue);
        webView.loadData(queryReturnedValue, "text/html; charset=utf-8", "UTF-8");

        isExternalStorageReadable();
        System.out.println(queryReturnedValue.length());
        writeFile(queryReturnedValue);

    }
    public native String entryPoint(String argument1, String argument2);

    private boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.i("StateInfo", "Yes, it is writable!");
            return true;
        } else {
            return false;
        }
    }

    private boolean isExternalStorageReadable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            Log.i("StateInfo", "Yes, it is readable!");
            return true;
        } else {
            return false;
        }
    }

    public void writeFile (String definition) {
        if (isExternalStorageWritable()) {
            File dictDirectory = new File(getExternalFilesDir(null).getAbsolutePath() + "/" + path.substring(0, path.length() - 4));
            System.out.println(dictDirectory.getAbsolutePath());
            if (!dictDirectory.exists()) {
                boolean wasSuccessful = dictDirectory.mkdir();;
                if (!wasSuccessful) {
                    Log.i("FileInfo", "directory creation is not successful");
                }
            }
            File textFile = new File(dictDirectory, word + ".html");
            if (!textFile.exists()) {
                Log.i("FileInfo", "file exist");
                try {
                    FileOutputStream fos = new FileOutputStream(textFile);
                    fos.write(definition.getBytes());
                    fos.close();

                    Toast.makeText(this, "saved to" + textFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "cannot write to external storage", Toast.LENGTH_LONG).show();
        }
    }

    private void fillEntries() {
        entries = new ArrayList<>();
        AssetManager assetManager = getAssets();
        try {
            String[] dictionaries = Arrays.copyOfRange(assetManager.list(""), 0, assetManager.list("").length - 2);
            ArrayUtils.reverse(dictionaries);
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
        // convert the treemap to 2 dimension array
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
        for (Object[] objects : arr) {
            entries.add(new Entry(objects[0].toString(), objects[1].toString()));
        }
    }

    private void setUpRecyclerView() {
        RecyclerView entriesRecView = findViewById(R.id.entriesRecView);
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
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //get focus
                item.getActionView().requestFocus();
                //get input method
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                return true;  // Return true to expand action view
            }
        });

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        int searchPlateId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View v = searchView.findViewById(searchPlateId);
        v.setBackgroundColor(Color.TRANSPARENT);

        searchView.setQueryHint("Type words in to search");
//        searchView.clearFocus();

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