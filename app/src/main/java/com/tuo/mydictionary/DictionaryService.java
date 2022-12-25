package com.tuo.mydictionary;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class DictionaryService {
    public static final String QUERY = "http://35.158.109.10/lemmatize/";
    Context context;

    public DictionaryService(Context context) {
        this.context = context;
    }

    public interface QueryResponseListener {
        void onError(String message);
        void onResponse(String content);
    }

    public interface InventoryResponseListener {
        void onError(String message);
        void onResponse(ArrayList<String> inventory);
    }

    public interface LemmatizerResponseListener {
        void onError(String message);
        void onResponse(String lemma);
    }

    public void getLemma (String token, LemmatizerResponseListener lemmatizerResponseListener) {
        String url = QUERY + token;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lemmatizerResponseListener.onResponse(response);
                    }
                    },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        lemmatizerResponseListener.onError("That didn't work!");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getContent(String search, QueryResponseListener queryResponseListener) {
        // Instantiate the RequestQueue.
        String url = QUERY + search;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String content = null;
                try {
                    content = (String) response.get(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                queryResponseListener.onResponse(content);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                queryResponseListener.onError("That didn't work!");
            }
        });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getInventory(InventoryResponseListener inventoryResponseListener) {
        String url = "https://mdict-heroku.herokuapp.com/inventory";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<String> inventory = new ArrayList<String>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        inventory.add(response.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                inventoryResponseListener.onResponse(inventory);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                inventoryResponseListener.onError("That didn't work!");
            }
        });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}
