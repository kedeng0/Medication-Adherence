package com.appzoro.BP_n_ME.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.MyAlarmReceiver;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Appzoro_ 5 on 9/6/2017.
 */

public class TermsFragment extends Fragment {
    View view;
    WebView termsview;
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private FirebaseAuth mAuth;
    PendingIntent pi1, pi2,pi3,pi4,pi5,pi6;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_terms,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Informed Consent");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
        init();
        return view;
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void init() {
        progress_d = Progress_dialog.getInstance();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        termsview = view.findViewById(R.id.termsview);
        //termsview.getSettings().setJavaScriptEnabled(true);
        termsview.loadUrl("file:///android_asset/index.html");

        /*termsview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void performClick() throws Exception {
                Logout();
            }
        }, "logout");*/

    }

    public void Logout(){
        new AlertDialog.Builder(getActivity()).setTitle("Logout")
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progress_d.showProgress(getActivity());

                        Intent myIntent = new Intent(getActivity(), MyAlarmReceiver.class);
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        pi1 = PendingIntent.getBroadcast(getActivity(), 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pi1);
                        pi2 = PendingIntent.getBroadcast(getActivity(), 2, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pi2);
                        pi3 = PendingIntent.getBroadcast(getActivity(), 3, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pi3);
                        pi4 = PendingIntent.getBroadcast(getActivity(), 4, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pi4);
                        pi5 = PendingIntent.getBroadcast(getActivity(), 5, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pi5);
                        pi6 = PendingIntent.getBroadcast(getActivity(), 6, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pi6);

                        getActivity().stopService(myIntent);
                        prefs.clear();
                        mAuth.signOut();
                    }
                }).setNegativeButton("no", null).show();

    }
}
