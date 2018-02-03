package com.appzoro.BP_n_ME.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;

/**
 * Created by Appzoro_ 4 on 8/23/2017.
 */

public class MedicationRecordAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String[]marray;
    private String date;
    private int daysSince;

    private static class ViewHolder {
        TextView tv_date,tv_meds;
        ImageView delete;
    }
    public MedicationRecordAdapter(Context context, String[] mArray, String date, int daysSince) {
        super(context, R.layout.item_row_medication,mArray);
        this.marray=mArray;
        this.mContext = context;
        this.date = date;
        this.daysSince = daysSince;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        String dataModel = getItem(position);
        MedicationRecordAdapter.ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new MedicationRecordAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_row_medication, parent, false);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            viewHolder.tv_meds = (TextView) convertView.findViewById(R.id.tv_meds);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            if (daysSince != 0){
                viewHolder.delete.setVisibility(View.GONE);
            }
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e("medsDelete", String.valueOf(position));
                    ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
                }
            });

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MedicationRecordAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        System.out.println("check position"+marray[position]);
        String CurrentString =marray[position];
        String[] separated = CurrentString.split("  ");
        System.out.println("ckeck string response"+separated[1]);
        viewHolder.tv_date.setText(date+" "+separated[0]);
        viewHolder.tv_meds.setText(separated[1].trim());

        return convertView;
    }
}
