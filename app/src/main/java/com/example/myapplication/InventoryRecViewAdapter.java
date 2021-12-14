package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        return new InventoryRecViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryRecViewAdapter.ViewHolder holder, int position) {
        holder.dictionary.setText(entries.get(position));
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
