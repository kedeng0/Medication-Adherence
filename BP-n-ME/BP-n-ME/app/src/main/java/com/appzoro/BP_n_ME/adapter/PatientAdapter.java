package com.appzoro.BP_n_ME.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.model.Patient;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by Appzoro_ 5 on 9/29/2017.
 */

public class PatientAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Patient> PatientList;
    private static LayoutInflater inflater = null;
    private ArrayList<Patient> filterlist;
    private MedasolPrefs prefs;

    public PatientAdapter(Context context, ArrayList<Patient> PatientList) {
        this.mContext = context;
        this.PatientList = PatientList;
        Collections.reverse(PatientList);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.filterlist = new ArrayList<>();
        this.filterlist.addAll(PatientList);
        prefs = new MedasolPrefs(mContext.getApplicationContext());
    }

    @Override
    public int getCount() {
        return PatientList.size();
    }

    @Override
    public Object getItem(int position) {
        return PatientList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.patientlist_layout, null);
        }
        TextView PatientName = convertView.findViewById(R.id.patientName);
        PatientName.setText(StringUtils.capitalize(PatientList.get(position).getName().toLowerCase().trim()));

        /*if (PatientList.get(position).getColor().equals("green")){
            PatientName.setTextColor(Color.parseColor("#ffffff"));
            PatientName.setBackgroundResource(R.drawable.patient_green_bg);
        } else {
            PatientName.setBackgroundResource(R.drawable.patient_red_bg);
            PatientName.setTextColor(Color.parseColor("#ffffff"));
        }*/


        if(position % 2 == 0)
            convertView.setBackgroundColor(Color.parseColor("#e2e0e0"));
        else
            convertView.setBackgroundColor(Color.parseColor("#f4f2f2"));

        PatientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.setUID(PatientList.get(position).getId());
                Intent intent = new Intent(mContext, PatientMonitor.class);
                //intent.putExtra("puid", "" + PatientList.get(position).getId());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        PatientList.clear();
        if (charText.length() == 0) {
            PatientList.addAll(filterlist);
        } else {
            for (Patient p : filterlist) {
                if (p.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    PatientList.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }
}
