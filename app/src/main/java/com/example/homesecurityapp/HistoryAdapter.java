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

    private static class ViewHolder {
        TextView lockStatusTextView;
        TextView safetyStatusTextView;
        TextView timestampTextView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.lockStatusTextView = convertView.findViewById(R.id.lock_status_text_view);
            viewHolder.safetyStatusTextView = convertView.findViewById(R.id.safety_status_text_view);
            viewHolder.timestampTextView = convertView.findViewById(R.id.timestamp_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HistoryItem historyItem = getItem(position);
        if (historyItem != null) {
            viewHolder.lockStatusTextView.setText("Lock Status: " + historyItem.getLockStatus());
            viewHolder.safetyStatusTextView.setText("Safety Status: " + historyItem.getSafetyStatus());
            viewHolder.timestampTextView.setText(formatTimestamp(historyItem.getTimestamp()));
        }

        return convertView;
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }
}
