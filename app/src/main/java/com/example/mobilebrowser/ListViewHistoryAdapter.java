package com.example.mobilebrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListViewHistoryAdapter extends BaseAdapter {

    private List<HistoryItem> historyList;
    private static List<HistoryItem> selectedItems = new ArrayList<>();
    private LayoutInflater inflater;
    private Activity context;
    private OnItemClickListener onItemClickListener;

    public ListViewHistoryAdapter(Activity context, List<HistoryItem> historyList) {
        super();
        this.historyList = historyList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public interface OnItemClickListener {
        void onItemClick(String url);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        CheckBox historyCheckBox;
        ImageButton historyItemDelete;
        TextView historyItemTime;
        TextView historyItemUrl;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_historyitem, null);

            holder.historyCheckBox = (CheckBox) convertView.findViewById(R.id.historyCheckBox);
            holder.historyItemTime = (TextView) convertView.findViewById(R.id.historyItemTime);
            holder.historyItemUrl = (TextView) convertView.findViewById(R.id.historyItemUrl);
            holder.historyItemDelete = (ImageButton) convertView.findViewById(R.id.historyItemDelete);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        HistoryItem historyItem = (HistoryItem) historyList.get(position);
        holder.historyItemTime.setText(historyItem.getTime());
        holder.historyItemUrl.setText(historyItem.getUrl());

        final HistoryItem currentItem = (HistoryItem) getItem(position);

        //action of textview of Url
        holder.historyItemUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clickedUrl = currentItem.getUrl();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(clickedUrl);
                }
            }
        });

        // action of Checkbox
        holder.historyCheckBox.setChecked(selectedItems.contains(currentItem));
        holder.historyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItems.add(currentItem);
                } else {
                    selectedItems.remove(currentItem);
                }
            }
        });

        // action of ImageButton for deleting item
        holder.historyItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the delete button click for the specific item
                // use 'position' to identify the clicked item and remove it from the list
                historyList.remove(position);
                notifyDataSetChanged(); // Refresh the ListView
            }
        });

        return convertView;
    }

    public static List<HistoryItem> getSelectedItems() {
        return selectedItems;
    }
}
