package com.example.interviewcoach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Custom Adapter for ListView showing videos with tips in interviews.
 *
 * @author Emmy
 */
public class CustomBaseAdapterListview extends BaseAdapter {
    Context context;
    ArrayList<String> listItems;
    ArrayList<String> listIcons;
    LayoutInflater inflater;

    public CustomBaseAdapterListview(Context ctx, ArrayList<String> items, ArrayList<String> icons) {
        this.context = ctx;
        this.listItems = items;
        this.listIcons = icons;
        inflater = LayoutInflater.from(ctx);

    }

    //Return total count for item arrayList.
    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //Return view of custom Listview.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_custom_list_view, null);
        TextView textView = convertView.findViewById(R.id.tv_custom_list);
        ImageView imageView = convertView.findViewById(R.id.iv_listview);
        textView.setText(listItems.get(position));
        Context context = parent.getContext();
        Glide.with(context).load(listIcons.get(position)).into(imageView);
        return convertView;
    }
}
