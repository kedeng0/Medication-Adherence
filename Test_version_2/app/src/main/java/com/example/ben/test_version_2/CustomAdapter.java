package com.example.ben.test_version_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Ben on 4/14/2018.
 */

public class CustomAdapter extends ArrayAdapter<Pill>{
    private ArrayList<Pill> pillList;
    Context mContext;
    private int lastPosition = -1;

    private static class ViewHolder {
        TextView txtName;
        TextView txtTime;
        TextView txtAmount;
    }
    public CustomAdapter(ArrayList<Pill> pill, Context context) {
        super(context, R.layout.row_item, pill);
        this.pillList = pill;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Pill pill = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.time);
            viewHolder.txtAmount = (TextView) convertView.findViewById(R.id.amount);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txtName.setText(pill.getName());
        viewHolder.txtTime.setText(Integer.toString(pill.getHour()) + ":" + Integer.toString(pill.getMinute()));
        viewHolder.txtAmount.setText(Integer.toString(pill.getAmount()));
        return convertView;
    }


    // sort
//    Collections.sort(l, new Comparator<String>() {
//
//        @Override
//        public int compare(String o1, String o2) {
//            try {
//                return new SimpleDateFormat("hh:mm a").parse(o1).compareTo(new SimpleDateFormat("hh:mm a").parse(o2));
//            } catch (ParseException e) {
//                return 0;
//            }
//        }
//    });

}
