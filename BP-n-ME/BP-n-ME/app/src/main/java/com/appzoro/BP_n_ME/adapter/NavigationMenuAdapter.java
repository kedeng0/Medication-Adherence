package com.appzoro.BP_n_ME.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.appzoro.BP_n_ME.model.NavigationMenuItems;
import com.appzoro.BP_n_ME.R;

import java.util.List;

/**
 * Created by Appzoro_ 5 on 9/21/2017.
 */

public class NavigationMenuAdapter extends BaseAdapter {
    private Context mContext;
    private List<NavigationMenuItems> mListDataItem;
    private static LayoutInflater inflater = null;

    public NavigationMenuAdapter(Context context, List<NavigationMenuItems> listDataItem) {
        this.mContext = context;
        this.mListDataItem = listDataItem;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mListDataItem.size();
    }

    @Override
    public Object getItem(int position) {
        return mListDataItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listheader, null);
        }
        TextView ListHeader = convertView.findViewById( R.id.submenu);
        ImageView headerIcon = convertView.findViewById(R.id.iconimage);

        NavigationMenuItems headerTitle = (NavigationMenuItems) getItem(position);
        ListHeader.setText(headerTitle.getItemName());
        Glide.with(mContext).load(headerTitle.getItemImg()).into(headerIcon);

        return convertView;
    }
}
