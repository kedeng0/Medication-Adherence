package com.appzoro.BP_n_ME.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Appzoro_ 4 on 8/29/2017.
 */

public class CallforClinicalCareFragment extends Fragment implements View.OnClickListener {
    View view;
    TextView pharmacyName, physicianName;
    ImageView pharmacyCall, physicianCall, gaQuitCall;
    String pharmaNo,physicianNo,gaQuitNo;
    MedasolPrefs prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_callforclinicalcare,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Call for Clinical Care");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
        init();
        Listener();
        return view;
    }

    private void init() {
        prefs = MedasolPrefs.getInstance(getActivity().getApplicationContext());

        pharmacyName = view.findViewById(R.id.pharmacyName);
        physicianName = view.findViewById(R.id.physicianName);
        pharmacyCall = view.findViewById(R.id.pharmacyCall);
        physicianCall = view.findViewById(R.id.physicianCall);
        gaQuitCall = view.findViewById(R.id.gaQuitCall);

        Log.e("pharmacyName",prefs.getPharmaName());
        Log.e("physicianName",prefs.getPhysicianName());

        pharmacyName.setText(StringUtils.capitalize(prefs.getPharmaName().toLowerCase().trim()));
        physicianName.setText(StringUtils.capitalize(prefs.getPhysicianName().toLowerCase().trim()));

        pharmaNo = prefs.getPharmaNumber();
        physicianNo = prefs.getPhysicianNumber();
        gaQuitNo = "1-877-270-STOP";
    }

    private void Listener() {
        pharmacyCall.setOnClickListener(this);
        physicianCall.setOnClickListener(this);
        gaQuitCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pharmacyCall:
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + pharmaNo));
                startActivity(i);
                break;
            case R.id.physicianCall:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + physicianNo));
                startActivity(intent);
                break;
            case R.id.gaQuitCall:
                Intent in = new Intent(Intent.ACTION_DIAL);
                in.setData(Uri.parse("tel:" + gaQuitNo));
                startActivity(in);
                break;
        }
    }
}
