package com.tuo.mydictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HtmlsRecViewAdapter extends RecyclerView.Adapter<HtmlsRecViewAdapter.ViewHolder> {

    private ArrayList<Entry> items = new ArrayList<>();
    private Context mContext;
    public float originalX;
    public float originalY;
    public float newX;
    public float newY;


    public HtmlsRecViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private WebView html;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            html = itemView.findViewById(R.id.myWebView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.html_page, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.html.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        originalX = event.getX();
                        originalY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        newX = event.getX();
                        newY = event.getY();
                        break;
                }
                float movementX = Math.abs(newX - originalX);
                float movementY = Math.abs(newY- originalY);
                if (movementX < movementY) {
                    holder.html.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.html.setWebViewClient(new MyWebViewClient());

        WebSettings settings = holder.html.getSettings();
        //necessary for reading css
        settings.setAllowFileAccess(true);

        String url = mContext.getExternalFilesDir(null).getAbsolutePath() + "/" + items.get(position).getDictionary() + "/" + items.get(position).getEntry() + ".html";
//        holder.html.loadUrl(url);

        File file = new File(url);
        try {
            holder.html.loadDataWithBaseURL("file://" + mContext.getExternalFilesDir(null).getAbsolutePath() + "/", Files.asCharSource(file, Charsets.UTF_8).read(), "text/html", "utf-8", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<Entry> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}