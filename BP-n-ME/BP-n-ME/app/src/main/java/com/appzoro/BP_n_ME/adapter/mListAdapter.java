package com.appzoro.BP_n_ME.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;

/**
 * Created by Appzoro_ 5 on 9/7/2017.
 */

public class mListAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String[] dataSet;

    private static class ViewHolder {
        TextView meds_name;
        ImageView delete, frequency ;
    }
    public mListAdapter(Context context, String[] mArray) {
        super(context, R.layout.list_item_row,mArray);
        this.dataSet=mArray;
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        String dataModel = getItem(position);
        mListAdapter.ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new mListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_row, parent, false);
            viewHolder.meds_name = (TextView) convertView.findViewById(R.id.meds_name);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            viewHolder.frequency = (ImageView) convertView.findViewById(R.id.iv_freq);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
                }
            });

            viewHolder.frequency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListView) parent).performItemClick(view,position,1); // Let the event be handled in onItemClick()
                }
            });
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (mListAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.meds_name.setText(dataSet[position]);
        return convertView;
    }
}

