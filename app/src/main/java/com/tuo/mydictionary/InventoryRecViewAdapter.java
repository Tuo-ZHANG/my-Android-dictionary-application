package com.tuo.mydictionary;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class InventoryRecViewAdapter extends RecyclerView.Adapter<InventoryRecViewAdapter.ViewHolder>{

    private ArrayList<String> entries = new ArrayList<>();
    private final Context context;
    public InventoryRecViewAdapter(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dictionary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dictionary = itemView.findViewById(R.id.entry_dict);
        }
    }

    @NonNull
    @Override
    public InventoryRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_dictionary, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.dictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse("https://mdict-heroku.herokuapp.com/download/" + entries.get(holder.getBindingAdapterPosition()));
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(context, null, entries.get(holder.getBindingAdapterPosition()));
                File file = new File(context.getExternalFilesDir(null), entries.get(holder.getBindingAdapterPosition()));
                if (file.exists()) {
                    Toast.makeText(context, "the dictionary is already in local storage", Toast.LENGTH_LONG).show();
                } else {
                    Long reference = downloadManager.enqueue(request);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryRecViewAdapter.ViewHolder holder, int position) {
        holder.dictionary.setText(entries.get(position).substring(0, entries.get(position).length() - 4));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setEntries(ArrayList<String> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }
}
