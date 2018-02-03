package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.adapter.NotificationAdapter;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Appzoro_ 5 on 10/31/2017.
 */

public class NotificationFragment extends Fragment {
    View view;
    ListView NotificationList;
    ArrayList<String> patientNotification = new ArrayList<>();
    ArrayList<String> NotificationDate = new ArrayList<>();
    ArrayList<String> updateNotificationDate = new ArrayList<>();
    ArrayList<String> Notificationtime = new ArrayList<>();
    private DatabaseReference mDatabase;
    private Progress_dialog progress_d;
    MedasolPrefs prefs;
    String UID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_notification, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Notifications");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
        init();
        return view;
    }

    private void init() {
        progress_d = Progress_dialog.getInstance();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        UID = prefs.getUID();

        NotificationList = view.findViewById(R.id.notificationList);
        getNotificationDate();
    }

    public void getNotificationDate() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("Notification").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot notification = it.next();
                            String date = notification.getKey();
                            NotificationDate.add(date);
                        }
                        if (NotificationDate.size() > 7){
                            for (int i=0; i<NotificationDate.size()-7; i++){
                                mDatabase.child("app").child("users").child(UID).child("Notification").child(NotificationDate.get(i)).removeValue();
                            }
                            getNotificationDetails(NotificationDate);
                        } else {
                            getNotificationDetails(NotificationDate);
                        }
                        if (NotificationDate.isEmpty()){
                            progress_d.hideProgress();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void getNotificationDetails(final ArrayList<String> notificationDate) {
        for(int i=0;i<notificationDate.size();i++) {
            final int finalI = i;
            mDatabase.child("app").child("users").child(UID).child("Notification").child(notificationDate.get(i)).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                            while (it.hasNext()) {
                                DataSnapshot notification = it.next();
                                String time = notification.getKey();
                                String value = notification.getValue().toString().trim();
                                Log.e("value", time + " " +notificationDate.get(finalI) + " " + value);
                                Notificationtime.add(time.split("_")[0]);
                                patientNotification.add(value);
                                updateNotificationDate.add(notificationDate.get(finalI));
                            }
                            if (finalI == notificationDate.size()-1){
                               setNotification(patientNotification, updateNotificationDate, Notificationtime);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    private void setNotification(ArrayList<String> patientNotification, ArrayList<String> updateNotificationDate, ArrayList<String> notificationTime) {
        progress_d.hideProgress();
        Log.e("size>>>>","date " + NotificationDate.size() + "  "+ updateNotificationDate.size() +"  value " + patientNotification.size());
        NotificationList.setAdapter(new NotificationAdapter(getActivity(), patientNotification, updateNotificationDate, notificationTime));
    }
}
