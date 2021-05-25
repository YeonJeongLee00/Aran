package com.hackathon.aran2.Record;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hackathon.aran2.R;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    ArrayList<String> strings;
    Context context;

    public SpinnerAdapter(ArrayList<String> strings, Context context){
        super();
        this.context = context;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return this.strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_custom,null);
        }
        TextView tv_item = convertView.findViewById(R.id.spinner_text);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "BinggraeMelona.ttf");
        tv_item.setTypeface(typeface);
        tv_item.setText(strings.get(position));
        return convertView;
    }
}
