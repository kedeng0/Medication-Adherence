package com.appzoro.BP_n_ME.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;

import java.util.ArrayList;

/**
 * Created by Appzoro_ 5 on 8/9/2017.
 */

public class InformationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> data;
    private ArrayList<String> dataname;
    private static LayoutInflater inflater = null;

    public InformationAdapter(Context context, ArrayList<String> data, ArrayList<String> dataName) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        this.dataname = dataName;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.information_list, null);
        }

        TextView text = (TextView) vi.findViewById(R.id.tv_data);
        TextView name = (TextView) vi.findViewById(R.id.tv_name);
        String textmodified = data.get(position);
        text.setText(textmodified);
        String nameField = dataname.get(position);
        name.setText(nameField);

        return vi;
    }
}
