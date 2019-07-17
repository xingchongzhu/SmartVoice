package com.data.smardvoicedemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySimpleAdapter extends SimpleAdapter {
    private List<? extends Map<String, ?>> mData;
    private int mResource;
    private LayoutInflater layoutInflater;
    public MySimpleAdapter(Activity context,
                           List<? extends Map<String, ?>> data, int resource) {
        super(context, data, resource, null, null);
        layoutInflater = context.getLayoutInflater();
        this.mResource = resource;
        this.mData = data;
    }

    public void setmData(List<? extends Map<String, ?>> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group) {

        View view = layoutInflater.inflate(mResource, null);
        TextView http = view.findViewById(R.id.http);
        TextView port = view.findViewById(R.id.port);
        http.setText(mData.get(position).get("http").toString());
        port.setText(mData.get(position).get("port").toString());

        return view;
    }
}
