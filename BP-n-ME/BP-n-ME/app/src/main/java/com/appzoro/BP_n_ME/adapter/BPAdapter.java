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

import java.util.List;

/**
 * Created by Appzoro_ 4 on 8/22/2017.
 */

public class BPAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String[]marray;
    private String date;
    private List<String> heartrate;
    private int daysSince;

    // View lookup cache
    private static class ViewHolder {
        TextView heartrate,bp_namesecond,tv_date;
        ImageView delete;
    }
    public BPAdapter(Context context, String[] mArray, String date, List<String> heartrate, int daysSince) {
        super(context,R.layout.item_row_bp,mArray);
        this.marray=mArray;
        this.mContext = context;
        this.date = date;
        this.heartrate = heartrate;
        this.daysSince = daysSince;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        String dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_row_bp, parent, false);
            viewHolder.bp_namesecond = (TextView) convertView.findViewById(R.id.bp_namesecond);
            viewHolder.heartrate = (TextView) convertView.findViewById(R.id.heartrate);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);


            viewHolder.delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            if (daysSince != 0){
                viewHolder.delete.setVisibility(View.GONE);
            }
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
                }
            });

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        System.out.println("check position"+marray[position]);
        String CurrentString = marray[position];

        String[] separated = CurrentString.split("  ");
        System.out.println("ckeck string response"+separated[1]);
        viewHolder.bp_namesecond.setText(separated[1].trim());
        viewHolder.tv_date.setText(date+"  "+separated[0]);
        try {
            viewHolder.heartrate.setText(heartrate.get(position));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return convertView;
    }
}
