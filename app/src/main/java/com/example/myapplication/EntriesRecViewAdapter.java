package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.myapplication.MainActivity.DICTIONARIES;
import static com.example.myapplication.MainActivity.ENTRY;

public class EntriesRecViewAdapter extends RecyclerView.Adapter<EntriesRecViewAdapter.ViewHolder> implements Filterable {

    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<Entry> entriesFull;
    private final Context context;

    public EntriesRecViewAdapter(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtEntry;
        private TextView queryHistory;
        private TextView placeholder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEntry = itemView.findViewById(R.id.txt_entry_card);
            queryHistory = itemView.findViewById(R.id.query_history);
            placeholder = itemView.findViewById(R.id.placeholder);
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
        holder.placeholder.setText("");
        holder.queryHistory.setText("");

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int quriedTimes = databaseHelper.returnQuriedTimes(entries.get(position).getEntry());
        if (quriedTimes > 0) {
            holder.placeholder.setText(R.string.quried);
            if (quriedTimes == 1) {
                holder.queryHistory.setText(R.string.once);
            } else if (quriedTimes == 2) {
                holder.queryHistory.setText(R.string.twice);
            } else {
                String stringDisplayed = String.valueOf(quriedTimes) + " times";
                holder.queryHistory.setText(stringDisplayed);
            }
        }

        holder.txtEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntryInformationModel entryInformationModel;
                //the ID one inputs here doesn't matter as it is never accessed later
                if (!databaseHelper.checkIfRecordExists(entries.get(position).getEntry())) {
                    entryInformationModel = new EntryInformationModel(-1, entries.get(position).getEntry(), 1, true);
                    boolean success = databaseHelper.addOne(entryInformationModel);
                    if (success) {
//                        Toast.makeText(mContext, entries.get(position).getEntry() + " queried", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    boolean success = databaseHelper.updateRecord(entries.get(position).getEntry());
                    if (success) {
//                        Toast.makeText(mContext, entries.get(position).getEntry() + " queried multiple times", Toast.LENGTH_SHORT).show();
                    }
                }
                //send entry and corresponding dictionary to HtmlsRecViewActivity
                Intent intent = new Intent(context, HtmlsRecViewActivity.class);
                intent.putExtra(ENTRY, entries.get(position).getEntry());
                intent.putExtra(DICTIONARIES, entries.get(position).getDictionary());
                context.startActivity(intent);
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
//            Log.i("FieldInfo", "entries size at filter " + entries.size());
            entries.clear();
//            Log.i("FieldInfo", "entries size at filter " + entries.size());
            entries.addAll((ArrayList) results.values);
//            Log.i("FieldInfo", "entries size at filter " + entries.size());
            notifyDataSetChanged();
        }
    };
}
