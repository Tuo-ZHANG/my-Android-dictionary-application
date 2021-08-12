package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    RecyclerView entriesRecView;
    ArrayList<File> dictionaryDirectories;
//    private int EXTERNAL_STORAGE_PERMISSION_CODE = 1;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("jni-layer");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        entriesRecView = findViewById(R.id.entriesRecView);

        dictionaryDirectories = getDictionaryDirectories();
//        Log.i("FileInfo", "the length of the dictionary directories is " + dictionaryDirectories.size());

        fillEntries(dictionaryDirectories);
//        Log.i("FileInfo", "the length of the entries is " + entries.size());

        setUpRecyclerView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.i("lifecycle", "onRestart: ");

//        Log.i("FileInfo", "the length of the dictionary directories is " + dictionaryDirectories.size());

//        Log.i("FileInfo", "the length of the entries is " + entries.size());

        adapter.setEntries(entries);
    }

    public native String entryPoint(String argument1, String argument2);

    private boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            Log.i("StateInfo", "Yes, it is writable!");
            return true;
        } else {
            return false;
        }
    }

    private boolean isExternalStorageReadable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
//            Log.i("StateInfo", "Yes, it is readable!");
            return true;
        } else {
            return false;
        }
    }

    private File createDictDirectory(String dict) {
        return new File(getExternalFilesDir(null).getAbsolutePath() + "/" + dict.substring(0, dict.length() - 4));
    }

    private File createHtmlPage(String word, File dictDirectory) {
        return new File(dictDirectory, word + ".html");
    }

    private void writeFile(String dict, String word, String definition) {
        if (isExternalStorageWritable()) {
            File dictDirectory = new File(getExternalFilesDir(null).getAbsolutePath() + "/" + dict.substring(0, dict.length() - 4));
            if (!dictDirectory.exists()) {
                boolean wasSuccessful = dictDirectory.mkdir();;
                if (!wasSuccessful) {
//                    Log.i("FileInfo", "directory creation is not successful");
                }
            }
            File htmlPage = new File(dictDirectory, word + ".html");
            if (!htmlPage.exists()) {
//                Log.i("FileInfo", "file exist");
                try {
                    FileOutputStream fos = new FileOutputStream(htmlPage);
                    fos.write(definition.getBytes());
                    fos.close();

//                    Toast.makeText(this, "entry exists in " + dict, Toast.LENGTH_LONG).show();
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

    private void deleteLocalHtmls() {
        for (File dictionaryDirectory : dictionaryDirectories) {
            String[] files = dictionaryDirectory.list();
            for (String s : files) {
                if (s.endsWith(".css")) {
                    continue;
                } else {
                    File file = new File(dictionaryDirectory, s);
                    file.delete();
                }
            }
        }
    }

    private ArrayList<File> getDictionaryDirectories() {
        String [] fileList = getExternalFilesDir(null).list();
        ArrayList<File> dictionaryDirectories = new ArrayList<File>();
//        Log.i("FileInfo", "the file list is " + Arrays.toString(fileList));
        for (String file : fileList) {
            File fileObject = new File(getExternalFilesDir(null), file);
            if (fileObject.isDirectory()) {
                dictionaryDirectories.add(fileObject);
            }
        }
        return dictionaryDirectories;
    }

    private ArrayList<File> getDictionaries() {
        String [] fileList = getExternalFilesDir(null).list();
        ArrayList<File> dictionaries = new ArrayList<File>();
//        Log.i("FileInfo", "the file list is " + Arrays.toString(fileList));
        for (String file : fileList) {
            File fileObject = new File(getExternalFilesDir(null), file);
            if (!fileObject.isDirectory()) {
                dictionaries.add(fileObject);
            }
        }
        return dictionaries;
    }

    private void fillEntries(ArrayList<File> dictionaryDirectories) {
        entries = new ArrayList<>();
        for (File dictionaryDirectory : dictionaryDirectories) {
            String[] files = dictionaryDirectory.list();
            for (String s : files) {
                if (s.endsWith(".css")) {
                    continue;
                }
                String token = s.substring(0, s.length() - 5);
                if (types.containsKey(token)) {
                    //create string which contains all the dictionaries
                    types.put(token, types.get(token) + "," + dictionaryDirectory.getName());
                } else {
                    types.put(token, dictionaryDirectory.getName());
                }
            }
        }
        // convert the treemap to 2 dimension array
        Object[][] array = new Object[types.size()][2];
        Set setEntries = types.entrySet();
        Iterator entriesIterator = setEntries.iterator();
        int i = 0;
        while (entriesIterator.hasNext()) {

            Map.Entry mapping = (Map.Entry) entriesIterator.next();

            array[i][0] = mapping.getKey();
            array[i][1] = mapping.getValue();

            i++;
        }
        // set entries
        for (Object[] objects : array) {
            entries.add(new Entry(objects[0].toString(), objects[1].toString()));
        }
    }

    private void setUpRecyclerView() {
        adapter = new EntriesRecViewAdapter(this);
        adapter.setEntries(entries);
        entriesRecView.setAdapter(adapter);
        entriesRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dict_menu, menu);

        MenuItem buttonDeleteHistory = menu.findItem(R.id.clear_query_history);
        buttonDeleteHistory.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                if (!databaseHelper.isEmpty()) {
                    databaseHelper.deleteRecords();
//                    Toast.makeText(getApplicationContext(), "the query history is cleared", Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        MenuItem buttonDeleteLocalHtmls = menu.findItem(R.id.clear_local_htmls);
        buttonDeleteLocalHtmls.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteLocalHtmls();
//                Toast.makeText(getApplicationContext(), "local htmls are all deleted", Toast.LENGTH_LONG).show();

                entries.clear();
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        MenuItem buttonDeleteLastQuery = menu.findItem(R.id.delete_last_query);
        buttonDeleteLastQuery.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                String query = databaseHelper.getLastQuery();

                for (File dictionaryDirectory : dictionaryDirectories) {
                    String[] files = dictionaryDirectory.list();
                    for (String s : files) {
                        if (s.startsWith(query)) {
                            File file = new File(dictionaryDirectory, s);
                            file.delete();
                        }
                    }
                }
                if (!databaseHelper.isEmpty()) {
                    databaseHelper.deleteLastRow();
                }

                types.clear();
                fillEntries(dictionaryDirectories);
                adapter.setEntries(entries);
                return true;
            }
        });


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

        int searchPlateId =
                searchView.
                getContext().
                getResources().
                getIdentifier("android:id/search_plate", null, null);
        View v = searchView.findViewById(searchPlateId);
        v.setBackgroundColor(Color.TRANSPARENT);

        searchView.setQueryHint("Type words in to search");
//        searchView.clearFocus();

//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getApplicationContext(), "search " + query + " in the dictionaries", Toast.LENGTH_LONG).show();
                ArrayList<File> dictionaries = getDictionaries();
                boolean searchSuccess = false;
//                Log.i("FileInfo", "the length of the dictionary list is " + dictionaries.size());
                for (File dictionary : dictionaries) {
                    if (dictionary.exists()) {
//                        Log.i("FileInfo", "the absolute path of the parent directory is " + getExternalFilesDir(null).getAbsolutePath());
//                        Log.i("FileInfo", "the absolute path of the file is " + dictionary.getAbsolutePath());
                        // Example of a call to a native method
                        String queryReturnedValue = entryPoint(dictionary.getAbsolutePath(), query);
//                      webView.loadData(queryReturnedValue, "text/html", null);
                        Log.i("length", String.valueOf(queryReturnedValue.length()));
                        if (queryReturnedValue.length() != 0) {
                            writeFile(dictionary.getName(), query, queryReturnedValue);
                            if (!searchSuccess) {
                                searchSuccess = true;
                            }
                        } else {
//                            Toast.makeText(getApplicationContext(), "entry does not exist in " + dictionary.getName(), Toast.LENGTH_LONG).show();
                        }
                    } else {
//                        Log.i("FileInfo", "the file cannot be found");
                        Toast.makeText(searchView.getContext(), "dictionary does not exist", Toast.LENGTH_LONG).show();
                    }
                }

                if (searchSuccess) {
//                    Log.i("FileInfo", "the length of the dictionary directories is " + dictionaryDirectories.size());

                    fillEntries(dictionaryDirectories);
//                    Log.i("FileInfo", "the length of the entries is " + entries.size());

                    EntryInformationModel entryInformationModel;
                    //the ID one inputs here doesn't matter as it is never accessed later
                    DatabaseHelper databaseHelper = new DatabaseHelper(searchView.getContext());
                    if (!databaseHelper.checkIfRecordExists(query)) {
                        entryInformationModel = new EntryInformationModel(-1, query, 1, true);
                        boolean success = databaseHelper.addOne(entryInformationModel);
                        if (success) {
                            Toast.makeText(searchView.getContext(), query + " queried", Toast.LENGTH_SHORT).show();
                        }
                    }

//                    adapter.setEntries(entries);

                    Intent intent = new Intent(searchView.getContext(), HtmlsRecViewActivity.class);
                    intent.putExtra(ENTRY, query);
                    intent.putExtra(DICTIONARIES, types.get(query));
                    searchView.getContext().startActivity(intent);
                } else {
                    Toast.makeText(searchView.getContext(), "query cannot be found in all dictionaries", Toast.LENGTH_SHORT).show();
                }
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