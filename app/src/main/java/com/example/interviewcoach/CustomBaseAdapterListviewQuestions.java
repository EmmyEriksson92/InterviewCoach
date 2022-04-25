package com.example.interviewcoach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
/**
 * Custom Adapter for ListView showing interview questions.
 * @author Emmy
 */
public class CustomBaseAdapterListviewQuestions extends BaseAdapter {
    Context context;
    ArrayList<String> listItems;
    LayoutInflater inflater;
    public CustomBaseAdapterListviewQuestions(Context ctx, ArrayList<String> items){
        this.context = ctx;
        this.listItems = items;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_custom_list_view_questions,null);
        TextView textView = convertView.findViewById(R.id.tv_custom_list);
        textView.setText(listItems.get(position));
        return convertView;
    }
}
