package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.myapplication.MainActivity.DICTIONARY;
import static com.example.myapplication.MainActivity.ENTRY;

public class EntriesRecViewAdapter extends RecyclerView.Adapter<EntriesRecViewAdapter.ViewHolder> implements Filterable {

    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<Entry> entriesFull;
    private Context mContext;

    public EntriesRecViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtEntry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEntry = itemView.findViewById(R.id.txtEntry2);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtEntry.setText(entries.get(position).getEntry());

        holder.txtEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntryInformationModel entryInformationModel;
                //the ID one inputs here doesn't matter as it is never accessed later
                DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
                if (!databaseHelper.checkIfRecordExists(entries.get(position).getEntry())) {
                    entryInformationModel = new EntryInformationModel(-1, entries.get(position).getEntry(), 1, true);
                    boolean success = databaseHelper.addOne(entryInformationModel);
                    if (success) {
                        Toast.makeText(mContext, entries.get(position).getEntry() + " queried", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    boolean success = databaseHelper.updateRecord(entries.get(position).getEntry());
                    if (success) {
                        Toast.makeText(mContext, entries.get(position).getEntry() + " queried multiple times", Toast.LENGTH_SHORT).show();
                    }
                }
                //send entry and corresponding dictionary to HtmlsRecViewActivity
                Intent intent = new Intent(mContext, HtmlsRecViewActivity.class);
                intent.putExtra(ENTRY, entries.get(position).getEntry());
                intent.putExtra(DICTIONARY, entries.get(position).getDictionary());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
        this.entriesFull = new ArrayList<>(entries);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return entriesFilter;
    }

    private Filter entriesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Entry> filteredEntries = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredEntries.addAll(entriesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Entry entry : entriesFull) {
                    if (entry.getEntry().toLowerCase().contains(filterPattern)) {
                        filteredEntries.add(entry);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredEntries;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            entries.clear();
            entries.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
