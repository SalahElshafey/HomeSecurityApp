package com.example.homesecurityapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.homesecurityapp.R;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryItem> {

    public HistoryAdapter(Context context, List<HistoryItem> historyItems) {
        super(context, 0, historyItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_item, parent, false);
        }

        HistoryItem historyItem = getItem(position);

        TextView statusTextView = convertView.findViewById(R.id.status_text_view);
        TextView timestampTextView = convertView.findViewById(R.id.timestamp_text_view);

        if (historyItem != null) {
            statusTextView.setText(historyItem.getStatus());
            timestampTextView.setText(historyItem.getTimestamp());
        }

        return convertView;
    }
}
