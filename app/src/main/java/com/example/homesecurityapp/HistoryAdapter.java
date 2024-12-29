package com.example.homesecurityapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        TextView lockStatusTextView = convertView.findViewById(R.id.lock_status_text_view);
        TextView safetyStatusTextView = convertView.findViewById(R.id.safety_status_text_view);
        TextView timestampTextView = convertView.findViewById(R.id.timestamp_text_view);

        if (historyItem != null) {
            lockStatusTextView.setText("Lock Status: " + historyItem.getLockStatus());
            safetyStatusTextView.setText("Safety Status: " + historyItem.getSafetyStatus());
            timestampTextView.setText(formatTimestamp(historyItem.getTimestamp()));
        }

        return convertView;
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }
}
