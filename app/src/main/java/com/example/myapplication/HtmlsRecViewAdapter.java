package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HtmlsRecViewAdapter extends RecyclerView.Adapter<HtmlsRecViewAdapter.ViewHolder> {

    private ArrayList<Entry> items = new ArrayList<>();
    private Context mContext;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.html_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // path example:"file:///android_asset/conjugation/sein.html"
        holder.html.loadUrl("file:///android_asset/" + items.get(position).getDictionary() + "/" + items.get(position).getEntry() + ".html");
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