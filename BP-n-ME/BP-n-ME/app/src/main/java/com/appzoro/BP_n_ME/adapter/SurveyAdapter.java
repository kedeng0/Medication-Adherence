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
 * Created by Appzoro_ 5 on 9/20/2017.
 */

public class SurveyAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> sList;
    private static LayoutInflater inflater = null;

    public SurveyAdapter(Context context, ArrayList<String> surveyList) {
        this.mContext = context;
        this.sList = surveyList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sList.size();
    }

    @Override
    public Object getItem(int position) {
        return sList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.survey_list, null);
        }

        TextView surveyName = convertView.findViewById(R.id.tv_survey);
        surveyName.setText(sList.get(position));

        return convertView;
    }
}
