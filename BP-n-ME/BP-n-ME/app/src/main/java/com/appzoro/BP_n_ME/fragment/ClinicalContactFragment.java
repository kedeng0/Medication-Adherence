package com.appzoro.BP_n_ME.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.MyAlarmReceiver;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Appzoro_ 5 on 6/17/2017.
 */

public class ClinicalContactFragment extends Fragment implements View.OnClickListener {
    View view;
    TextView logout;
    ImageView iv_mail,iv_mails, emailManigault, emailThurston, emailPatel, emailNewsom;
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private FirebaseAuth mAuth;
    PendingIntent pi1, pi2,pi3,pi4,pi5,pi6;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_clinical_contact ,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Study Contact");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
        init();
        Listener();
        return view;
    }

    private void init() {
        progress_d = Progress_dialog.getInstance();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        //iv_mail = view.findViewById(R.id.iv_mail);
        emailManigault = view.findViewById(R.id.emailManigault);
        emailThurston = view.findViewById(R.id.emailThurston);
        emailPatel = view.findViewById(R.id.emailPatel);
        emailNewsom = view.findViewById(R.id.emailNewsom);
        //iv_mails = view.findViewById(R.id.iv_mails);
        logout = view.findViewById(R.id.logout);
    }

    private void Listener() {
        //iv_mail.setOnClickListener(this);
        emailManigault.setOnClickListener(this);
        emailThurston.setOnClickListener(this);
        emailPatel.setOnClickListener(this);
        emailNewsom.setOnClickListener(this);
        //iv_mails.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.iv_mail:
                email(view);
                break;*/
            case R.id.emailManigault:
                emailManigault(view);
                break;
            case R.id.emailThurston:
                emailThurston(view);
                break;
            case R.id.emailPatel:
                emailPatel(view);
                break;
            case R.id.emailNewsom:
                emailNewsom(view);
                break;
            /*case R.id.iv_mails:
                emails(view);
                break;*/
            case R.id.logout:
                Logout();
                break;
        }
    }

    private void emails(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:Alyssa.Elrod@wellstar.org");
        intent.setData(data);
        startActivity(intent);
    }

    public void email (View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:MOYE_PM@mercer.edu?");
        intent.setData(data);
        startActivity(intent);
    }

    public void emailManigault (View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:manigault_kr@mercer.edu?");
        intent.setData(data);
        startActivity(intent);
    }

    public void emailThurston (View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:thurston_mm@mercer.edu?");
        intent.setData(data);
        startActivity(intent);
    }

    public void emailPatel (View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:patel_sm@mercer.edu?");
        intent.setData(data);
        startActivity(intent);
    }

    public void emailNewsom (View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:newsom_lc@mercer.edu?");
        intent.setData(data);
        startActivity(intent);
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
