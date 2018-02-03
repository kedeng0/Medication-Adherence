package com.appzoro.BP_n_ME.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Appzoro_ 5 on 10/31/2017.
 */

public class NotificationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> notificationList;
    private ArrayList<String> notificationDate;
    private ArrayList<String> notificationTime;
    private static LayoutInflater inflater = null;

    public NotificationAdapter(Context context, ArrayList<String> notificationList, ArrayList<String> notificationDate, ArrayList<String> notificationTime) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.notificationList = notificationList;
        this.notificationDate = notificationDate;
        this.notificationTime = notificationTime;
        Collections.reverse(notificationList);
        Collections.reverse(notificationDate);
        Collections.reverse(notificationTime);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        if (vi == null) {
            vi = inflater.inflate(R.layout.notification_list, null);
        }

        TextView text =  vi.findViewById(R.id.notification_text);
        TextView date =  vi.findViewById(R.id.notification_date);

        String s1 = notificationList.get(position);
        text.setText(s1);
        /*String s2 = notificationDate.get(position);
        date.setText(s2);*/

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date Date = inputFormat.parse(notificationDate.get(position));
            date.setText(notificationTime.get(position) + "  " + outputFormat.format(Date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return vi;
    }
}
