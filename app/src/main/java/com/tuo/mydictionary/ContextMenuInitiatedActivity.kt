package com.tuo.mydictionary

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ContextMenuInitiatedActivity : AppCompatActivity() {
    private val packageToCheck: String = "cn.mdict"
    private val types = TreeMap<String, String>()

    init {
        System.loadLibrary("jni-layer")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var input = (intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: "").trim()
        if (input.split(" ").size == 1) {
            val token = input
            if (searchSuccess(token)) {
                updateDatabase(token)
                actionAfterSearchSuccess(token)
            } else {
                searchByLemmatizer(token)
            }
        } else {
            val tokenList = input.split(" ")
            val token = tokenList[tokenList.lastIndex] + tokenList[0]
            searchByLemmatizer(token)
        }
    }

    private fun searchSuccess(token: String) : Boolean{
        val dictionaries: ArrayList<File> = getDictionaries()
        var searchSuccess = false
        for (dictionary in dictionaries) {
            if (dictionary.exists()) {
                // Example of a call to a native method
                val queryReturnedValue: String = entryPoint(dictionary.absolutePath, token)
                if (queryReturnedValue.isNotEmpty()) {
                    if (types.containsKey(token)) {
                        //create string which contains all the dictionaries
                        if (!types[token]
                            !!.contains(dictionary.name.substring(0, dictionary.name.length - 4))
                        ) {
                            types[token] =
                                types[token].toString() + "," + dictionary.name.substring(
                                    0,
                                    dictionary.name.length - 4
                                )
                        }
                    } else {
                        types[token] = dictionary.name.substring(0, dictionary.name.length - 4)
                    }
                    writeFile(dictionary.name, token, queryReturnedValue)
                    if (!searchSuccess) {
                        searchSuccess = true
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "dictionary does not exist",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return searchSuccess
    }

    private fun searchByLemmatizer(token: String) {
        val dictionaryService = DictionaryService(this)
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            dictionaryService.getLemma(
                token,
                object : DictionaryService.LemmatizerResponseListener {
                    override fun onResponse(lemma: String) {
    //                Log.d("lemma", lemma)
                        queryDictionaries(lemma.substring(1, lemma.length - 1))
                    }

                    override fun onError(message: String) {
                        Toast.makeText(
                            this@ContextMenuInitiatedActivity,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                        queryDictionaries(token)
                    }
                })
        } else {
            queryDictionaries(token)
        }
    }

    private fun queryDictionaries(token: String) {
        val dictionaries: ArrayList<File> = getDictionaries()

        var searchSuccess = false

        for (dictionary in dictionaries) {
            if (dictionary.exists()) {
                // Example of a call to a native method
                val queryReturnedValue: String = entryPoint(dictionary.absolutePath, token)
                if (queryReturnedValue.isNotEmpty()) {
                    if (types.containsKey(token)) {
                        //create string which contains all the dictionaries
                        if (!types[token]
                            !!.contains(dictionary.name.substring(0, dictionary.name.length - 4))
                        ) {
                            types[token] =
                                types[token].toString() + "," + dictionary.name.substring(
                                    0,
                                    dictionary.name.length - 4
                                )
                        }
                    } else {
                        types[token] = dictionary.name.substring(0, dictionary.name.length - 4)
                    }
                    writeFile(dictionary.name, token, queryReturnedValue)
                    if (!searchSuccess) {
                        searchSuccess = true
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "dictionary does not exist",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (searchSuccess) {
            updateDatabase(token)
            actionAfterSearchSuccess(token)
        } else {
            if (dictionaries.size == 0) {
                Toast.makeText(
                    this,
                    "there is no mdx file in the APP folder",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "query cannot be found in all dictionaries",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun actionAfterSearchSuccess(token: String) {
        if (isPackageInstalled(packageToCheck, packageManager)) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, token)
            sendIntent.type = "text/plain"
            sendIntent.setClassName(packageToCheck, "$packageToCheck.PopupForm")
            val shareIntent = Intent.createChooser(sendIntent, null)

            startActivity(shareIntent)
            finish()
        } else {
            setContentView(R.layout.activity_htmls_rec_view)
            val htmlsRecView = findViewById<RecyclerView>(R.id.htmls_rec_view)
            val adapterLocal = HtmlsRecViewAdapter(this)

            val items = ArrayList<Entry>()
            val listOfDictionaries = types[token]!!.split(",")

            for (s in listOfDictionaries) {
                items.add(Entry(token, s))
            }

            adapterLocal.setItems(items)
            htmlsRecView.adapter = adapterLocal
            htmlsRecView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(htmlsRecView)
        }
    }

    private fun updateDatabase(query: String) {
        val entryInformationModel: EntryInformationModel
        //the id one inputs here doesn't matter as it is never accessed later
        val databaseHelper = DatabaseHelper(this)
        if (!databaseHelper.checkIfRecordExists(query)) {
            // the id here does not matter as it is never accessed later
            entryInformationModel = EntryInformationModel(-1, query, 1, true)
            val success = databaseHelper.addOne(entryInformationModel)
            if (success) {
                Toast.makeText(this, "$query queried", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            databaseHelper.updateRecord(query)
            val quriedTimes = databaseHelper.returnQuriedTimes(query)
            Toast.makeText(this, "$query now queried $quriedTimes times", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (Resources.getSystem().displayMetrics.heightPixels * 0.5).toInt())
    }

    private fun getDictionaries(): ArrayList<File> {
        val fileList = getExternalFilesDir(null)!!.listFiles()
        val dictionaries = ArrayList<File>()
        for (file in fileList) {
            if (file.name.endsWith(".mdx")) {
                dictionaries.add(file)
            }
        }
        return dictionaries
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    private fun writeFile(dict: String, word: String, definition: String) {
        if (isExternalStorageWritable()) {
            val dictDirectory = File(
                getExternalFilesDir(null)!!.absolutePath + "/" + dict.substring(
                    0,
                    dict.length - 4
                )
            )
            if (!dictDirectory.exists()) {
                dictDirectory.mkdir()
            }
            val htmlPage = File(dictDirectory, "$word.html")
            if (!htmlPage.exists()) {
//                Log.i("FileInfo", "file exist");
                try {
                    val fos = FileOutputStream(htmlPage)
                    fos.write(definition.toByteArray())
                    //                    fos.flush();
                    fos.close()

//                    Toast.makeText(this, "entry exists in " + dict, Toast.LENGTH_LONG).show();
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(this, "cannot write to external storage", Toast.LENGTH_LONG).show()
        }
    }

    private external fun entryPoint(argument1: String?, argument2: String?): String

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}