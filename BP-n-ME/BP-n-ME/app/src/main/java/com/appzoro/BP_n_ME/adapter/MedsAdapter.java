package com.appzoro.BP_n_ME.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;

import java.util.List;


/**
 * Created by Appzoro_ 5 on 8/22/2017.
 */
public class MedsAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> medsdata;
    private List<String> freqdata;
    private static LayoutInflater inflater = null;

    public MedsAdapter(Context context, List<String> medsdata, List<String> freqdata) {
        this.mContext = context;
        this.medsdata = medsdata;
        this.freqdata = freqdata;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return medsdata.size();
    }

    @Override
    public Object getItem(int i) {
        return medsdata.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.listview_medsname_layout, null);
        }
        TextView meds_name = (TextView) vi.findViewById(R.id.medsname);
        TextView meds_freq = (TextView) vi.findViewById(R.id.medsfreq);

        String med = medsdata.get(position);
        //Log.e("medsssss>>>", med);
        String freq = freqdata.get(position);
        //Log.e("freqssss>>>", freq);

        if (!med.equals("")){
            meds_name.setText(med + " :");
            meds_freq.setText(freq);
        }else {
            meds_name.setVisibility(View.GONE);
            meds_freq.setVisibility(View.GONE);
        }
        return vi;
    }
}

