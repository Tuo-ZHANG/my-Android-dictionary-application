package com.tuo.mydictionary;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Entry> entries;
    private ArrayList<Entry> entriesBackup;
    public static final String ENTRY = "entry";
    public static final String DICTIONARIES = "dictionaries";
    private EntriesRecViewAdapter adapter;
    private TreeMap<String, String> types = new TreeMap<>();
    RecyclerView entriesRecView;
    ArrayList<File> dictionaryDirectories;
    SearchView searchView;
    private static String query;
    boolean redundantBehavior;
    AlertDialog alertDialog;

//    private int EXTERNAL_STORAGE_PERMISSION_CODE = 1;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("jni-layer");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i("LifecycleInfo", "onCreate called");

        setContentView(R.layout.activity_main);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        entriesRecView = findViewById(R.id.entriesRecView);

        dictionaryDirectories = getDictionaryDirectories();
//        Log.i("FileInfo", "the file list is " + dictionaryDirectories.toString());
//        Log.i("FileInfo", "the length of the dictionary directories is " + dictionaryDirectories.size());

//        Log.i("FieldInfo", String.valueOf(types.isEmpty()));

        fillEntriesByAlphabet(dictionaryDirectories);
//        Log.i("FileInfo", "the file list is " + entries.toString());
//        Log.i("FileInfo", "the length of the entries is " + entries.size());

        setUpRecyclerView();
//        Log.i("FieldInfo", "the types size at setUpRecyclerView " + types.size());

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        createDatabaseDirectory();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.i("LifecycleInfo", "onRestart called");

        if (query != null) {
//            Log.i("FieldInfo ", "entries size at onRestart " + entries.size());
//            Log.i("FieldInfo ", "entriesBackup size at onRestart " + entriesBackup.size());
            searchView.setQuery("", false);
//            Log.i("FieldInfo", "entries size at setQuery " + entries.size());
//            Log.i("FileInfo", "the size of the dictionary directories is " + dictionaryDirectories.size());
            entries = (ArrayList<Entry>) entriesBackup.clone();
//            Log.i("FieldInfo", "the size of the entries after copying from backup is " + entries.size());

//            Log.i("FieldInfo", "query is " + query);
            if (!redundantBehavior) {
                int position = types.headMap(query).size();
                entries.add(position, new Entry(query, types.get(query)));
                entriesBackup.add(position, new Entry(query, types.get(query)));
//            Log.i("FieldInfo", "the size of the backup entries is " + entriesBackup.size());
            }

            adapter = new EntriesRecViewAdapter(this);
            adapter.setEntries(entries);
            entriesRecView.setAdapter(adapter);
            query = null;
        }
        // the code below takes effect when you use entry in recycler view to jump into HTML pages
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public void createDatabaseDirectory() {
        File databaseDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "sqlite-databases");
        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdir();
        }
    }

    public void createBackup() {
        File myBackupDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "my-backup");
        if (!myBackupDirectory.exists()) {
            myBackupDirectory.mkdir();
        }
        File[] fileList = getExternalFilesDir(null).listFiles();
        if (fileList.length == 0) {
            Toast.makeText(MainActivity.this, "no need to backup", Toast.LENGTH_LONG).show();
        } else {
            for (File file : fileList) {
                if (file.isFile()) {
//                String destination = myBackupDirectory.getAbsolutePath() + File.separator + file.getName();
                    File destination = new File(myBackupDirectory.getAbsolutePath() + File.separator + file.getName());
                    if (!destination.exists()) {
                        try {
                            FileUtils.copyFile(file, destination);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, file.getName() + " already backed up", Toast.LENGTH_LONG).show();
                    }
                }
            }
            Toast.makeText(MainActivity.this, "backup created", Toast.LENGTH_LONG).show();
        }
    }

    public void deployBackup() {
        File externalFilesDir = getExternalFilesDir(null);
        File myBackupDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "my-backup");
        if (!myBackupDirectory.exists()) {
            Toast.makeText(MainActivity.this, "backup directory doesn't exist, please click create backup first", Toast.LENGTH_LONG).show();
        } else {
            for (File file : myBackupDirectory.listFiles()) {
                File destination = new File(externalFilesDir.getAbsolutePath() + File.separator + file.getName());
                if (!destination.exists()) {
                    try {
                        FileUtils.copyFile(file, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "no need to deploy" + file.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(MainActivity.this, "backup deployed", Toast.LENGTH_LONG).show();
        }
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
//        Log.d("debug", word);
        if (isExternalStorageWritable()) {
            File dictDirectory = new File(getExternalFilesDir(null).getAbsolutePath() + "/" + dict.substring(0, dict.length() - 4));
            if (!dictDirectory.exists()) {
                boolean wasSuccessful = dictDirectory.mkdir();
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
//                    fos.flush();
                    fos.close();

//                    Toast.makeText(this, "entry exists in " + dict, Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
//                Log.d("debug", htmlPage.getName() + " exists");
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
        String[] fileList = getExternalFilesDir(null).list();
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
        File[] fileList = getExternalFilesDir(null).listFiles();
        ArrayList<File> dictionaries = new ArrayList<File>();
        for (File file : fileList) {
            if (file.getName().endsWith(".mdx")) {
                dictionaries.add(file);
            }
        }
        return dictionaries;
    }

    private void fillEntriesByAlphabet(ArrayList<File> dictionaryDirectories) {
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
        for (Object[] object : array) {
            entries.add(new Entry(object[0].toString(), object[1].toString()));
        }
    }

    private void fillEntriesByQueryFrequency(ArrayList<File> dictionaryDirectories) {
        if (!types.isEmpty()) {
            TreeMap<String, String> mapByQueryFrequency = new TreeMap<>();
            for (Map.Entry<String, String> entry : types.entrySet()) {
                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                int quriedTimes = databaseHelper.returnQuriedTimes(entry.getKey());
                mapByQueryFrequency.put(quriedTimes + "," + entry.getKey(), entry.getValue());
            }
            // convert the treemap to 2 dimension array
            Object[][] array = new Object[mapByQueryFrequency.size()][2];
            Set setEntries = mapByQueryFrequency.entrySet();

            Iterator entriesIterator = setEntries.iterator();
            int i = 0;
            while (entriesIterator.hasNext()) {

                Map.Entry mapping = (Map.Entry) entriesIterator.next();

                array[i][0] = mapping.getKey();
                array[i][1] = mapping.getValue();

                i++;
            }
            // set entries
            for (Object[] object : array) {
                entries.add(new Entry(object[0].toString(), object[1].toString()));
            }
        }
    }

    private void setUpRecyclerView() {
//        Log.i("MethodInfo", "setUpRecyclerView called");
        adapter = new EntriesRecViewAdapter(this);
//        Log.i("MethodInfo", "first step");
        adapter.setEntries(entries);
//        Log.i("MethodInfo", "second step");
        entriesRecView.setAdapter(adapter);
//        Log.i("MethodInfo", "third step");
        entriesRecView.setLayoutManager(new LinearLayoutManager(this));
//        Log.i("MethodInfo", "fourth step");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dict_menu, menu);

        MenuItem dictInventory = menu.findItem(R.id.dict_inventory);
        SubMenu dictInventorySubMenu = dictInventory.getSubMenu();
//        dictInventorySubMenu.clearHeader();
        ArrayList<File> dictionaries = getDictionaries();
        for (File dictionary : dictionaries) {
            dictInventorySubMenu.add(dictionary.getName().substring(0, dictionary.getName().length() - 4));
        }

        MenuItem viewMode = menu.findItem(R.id.view_mode);
        MenuItem buttonViewByQueryFrequency = menu.findItem(R.id.by_query_frequency);

        MenuItem buttonDeleteHistory = menu.findItem(R.id.clear_query_history);
        MenuItem buttonDeleteLocalHtmls = menu.findItem(R.id.clear_local_htmls);
        MenuItem buttonDeleteLastQuery = menu.findItem(R.id.delete_last_query);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem buttonDownloadFromServer = menu.findItem(R.id.download_from_server);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                dictInventory.setVisible(true);
                viewMode.setVisible(true);
                buttonDeleteHistory.setVisible(true);
                buttonDeleteLocalHtmls.setVisible(true);
                buttonDeleteLastQuery.setVisible(true);
//                Log.i("FieldInfo ", "entries size at onMenuItemActionCollapse " + entries.size());
                searchView.setQuery("", false);
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //get focus
                item.getActionView().requestFocus();
                //get input method
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                dictInventory.setVisible(false);
                viewMode.setVisible(false);
                buttonDeleteHistory.setVisible(false);
                buttonDeleteLocalHtmls.setVisible(false);
                buttonDeleteLastQuery.setVisible(false);
                buttonDownloadFromServer.setVisible(false);

                entriesBackup = (ArrayList<Entry>) entries.clone();

                return true;  // Return true to expand action view
            }
        });

        int searchPlateId = searchView.
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
                query = query.toLowerCase(Locale.ROOT);
//                Log.i("MethodInfo", "onQueryTextSubmit called");

//                Toast.makeText(getApplicationContext(), "search " + query + " in the dictionaries", Toast.LENGTH_LONG).show();
                ArrayList<File> dictionaries = getDictionaries();
                boolean searchSuccess = false;
                redundantBehavior = false;
//                Log.i("FileInfo", "the length of the dictionary list is " + dictionaries.size());

                for (File dictionary : dictionaries) {
                    if (dictionary.exists()) {
//                        Log.i("FileInfo", "the absolute path of the parent directory is " + getExternalFilesDir(null).getAbsolutePath());
//                        Log.i("FileInfo", "the absolute path of the file is " + dictionary.getAbsolutePath());
                        // Example of a call to a native method
                        String queryReturnedValue = entryPoint(dictionary.getAbsolutePath(), query);
//                      webView.loadData(queryReturnedValue, "text/html", null);
//                        Log.i("length", String.valueOf(queryReturnedValue.length()));
                        if (queryReturnedValue.length() != 0) {
//                            Log.d("debug", "enter queryReturnedValue.length() != 0");
                            if (types.containsKey(query)) {
                                //create string which contains all the dictionaries
                                if (types.get(query).contains(dictionary.getName().substring(0, dictionary.getName().length() - 4))) {
                                    if (!redundantBehavior) {
                                        redundantBehavior = true;
                                    }
                                } else {
                                    types.put(query, types.get(query) + "," + dictionary.getName().substring(0, dictionary.getName().length() - 4));
                                }
                            } else {
                                types.put(query, dictionary.getName().substring(0, dictionary.getName().length() - 4));
                            }
//                            Log.d("debug", query);
                            writeFile(dictionary.getName(), query, queryReturnedValue);
                            if (!searchSuccess) {
                                searchSuccess = true;
                                MainActivity.query = query;
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

//                    Log.i("FieldInfo", String.valueOf(entries.size()));
//                    Log.i("FieldInfo", String.valueOf(types.size()));

                    EntryInformationModel entryInformationModel;
                    //the id one inputs here doesn't matter as it is never accessed later
                    DatabaseHelper databaseHelper = new DatabaseHelper(searchView.getContext());
                    if (!databaseHelper.checkIfRecordExists(query)) {
                        // the id here does not matter as it is never accessed later
                        entryInformationModel = new EntryInformationModel(-1, query, 1, true);
                        boolean success = databaseHelper.addOne(entryInformationModel);
                        if (success) {
                            Toast.makeText(searchView.getContext(), query + " queried", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (redundantBehavior) {
                            Toast.makeText(searchView.getContext(), "you have used search even though the query is in recycler view", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(searchView.getContext(), query + " already exists in the database but not in local cache", Toast.LENGTH_SHORT).show();
                        }
                        databaseHelper.updateRecord(query);
                    }

                    Intent intent = new Intent(searchView.getContext(), HtmlsRecViewActivity.class);
                    intent.putExtra(ENTRY, query);
                    intent.putExtra(DICTIONARIES, types.get(query));
//                    Log.i("MethodInfo ", String.valueOf(types.get(query)));
                    searchView.getContext().startActivity(intent);
                } else {
                    if (dictionaries.size() == 0) {
                        Toast.makeText(searchView.getContext(), "there is no mdx file in the APP folder", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(searchView.getContext(), "query cannot be found in all dictionaries", Toast.LENGTH_SHORT).show();
                    }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert Dialog");
        alertDialog.setMessage("are you sure to perform this functionality?");
        alertDialog.setButton(-2, "cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        if (id == R.id.clear_query_history) {
            alertDialog.setButton(-1, "ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                    if (!databaseHelper.isEmpty()) {
                        databaseHelper.deleteRecords();
                        Toast.makeText(MainActivity.this, "the query history is cleared", Toast.LENGTH_LONG).show();
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            alertDialog.show();
            return true;
        } else if (id == R.id.clear_local_htmls) {
            alertDialog.setButton(-1, "ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteLocalHtmls();
                    Toast.makeText(MainActivity.this, "local htmls are all deleted", Toast.LENGTH_LONG).show();

                    entries.clear();
                    adapter.notifyDataSetChanged();
                }
            });

            alertDialog.show();
            return true;
        } else if (id == R.id.delete_last_query) {
            DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
            if (!databaseHelper.isEmpty()) {
                String query = databaseHelper.getLastQuery();

                alertDialog.setMessage("are you sure to delete the query for " + query + "?");
                alertDialog.setButton(-1, "ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (types.containsKey(query)) {
                            for (File dictionaryDirectory : dictionaryDirectories) {
                                if (types.get(query).contains(dictionaryDirectory.getName())) {
                                    File file = new File(dictionaryDirectory, query + ".html");
                                    file.delete();
                                }
                            }

                            databaseHelper.deleteLastRow();

                            int position = types.headMap(query).size();
                            types.remove(query);
                            entries.remove(position);
//                            Log.i("FieldInfo", "entries size at headmap method " + entries.size());
                            adapter = new EntriesRecViewAdapter(MainActivity.this);
                            adapter.setEntries(entries);
                            entriesRecView.setAdapter(adapter);
                        }

                    }
                });
                alertDialog.show();
            } else {
                Toast.makeText(this, "the query table is empty", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.by_query_frequency) {
//            Toast.makeText(MainActivity.this, "item clicked", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.download_from_server) {
            Intent intent = new Intent(MainActivity.this, InventoryRecViewActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.create_backup) {
            alertDialog.setButton(-1, "ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createBackup();
                }
            });
            alertDialog.show();
            return true;
        } else if (id == R.id.deploy_backup) {
            alertDialog.setButton(-1, "ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deployBackup();
                }
            });
            alertDialog.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}