package com.example.mobilebrowser;

import android.app.Activity;
import android.content.Context;
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

public class ListViewBookmarksAdapter extends BaseAdapter {
    private List<BookmarksItem> bookmarksList;
    private static List<BookmarksItem> selectedItems = new ArrayList<>();
    private LayoutInflater inflater;
    private Activity context;
    private ListViewBookmarksAdapter.OnItemClickListener onItemClickListener;

    public ListViewBookmarksAdapter(Activity context, List<BookmarksItem>bookmarksList){
        super();
        this.bookmarksList =bookmarksList;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public interface OnItemClickListener {
        void onItemClick(String url);
    }

    public void setOnItemClickListener(ListViewBookmarksAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getCount() {
        return bookmarksList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookmarksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public static class ViewHolder{
        CheckBox bookmarksCheckBox;
        TextView bookmarksItemTitle;
        TextView bookmarksItemUrl;
        ImageButton bookmarksItemDelete;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_bookmarksitem, null);

            holder.bookmarksCheckBox = (CheckBox) convertView.findViewById(R.id.bookmarksCheckBox);
            holder.bookmarksItemTitle = (TextView) convertView.findViewById(R.id.bookmarksItemTitle);
            holder.bookmarksItemUrl = (TextView) convertView.findViewById(R.id.bookmarksItemUrl);
            holder.bookmarksItemDelete = (ImageButton)convertView.findViewById(R.id.bookmarksItemDelete);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();

        BookmarksItem bookmarksItem = (BookmarksItem) bookmarksList.get(position);
        holder.bookmarksItemTitle.setText(bookmarksItem.getTitle());
        holder.bookmarksItemUrl.setText(bookmarksItem.getUrl());

        final BookmarksItem currentItem = (BookmarksItem)getItem(position);

        //action of textview of Url
        holder.bookmarksItemUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clickedUrl = currentItem.getUrl();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(clickedUrl);
                }
            }
        });

        // action of Checkbox
        holder.bookmarksCheckBox.setChecked(selectedItems.contains(currentItem));
        holder.bookmarksCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        holder.bookmarksItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the delete button click for the specific item
                // You can use 'position' to identify the clicked item and remove it from the list
                bookmarksList.remove(position);
                notifyDataSetChanged(); // Refresh the ListView
            }
        });

        return convertView;
    }

    public static List<BookmarksItem> getSelectedItems() {
        return selectedItems;
    }
}
