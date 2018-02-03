package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Appzoro_ 5 on 9/15/2017.
 */

public class DiagnosisFragment extends Fragment {
    View view;
    ListView diagsList;
    ArrayList<String> patientDiags;
    String UID;
    MedasolPrefs prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_diagnosis ,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Patient Monitor");
        ((PatientMonitor) getActivity()).getSupportActionBar().setSubtitle("Diagnosis");
        init();
        return view;
    }

    private void init() {
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();

        diagsList = view.findViewById(R.id.diagList);
        patientDiags =  new ArrayList<>(Arrays.asList(prefs.getDiagList().trim().replace("[","").replace("]","").split(", ")));
        Log.e("patientDiags", String.valueOf(patientDiags));

        setDiagAlert(patientDiags);
    }

    public void setDiagAlert(ArrayList<String> patientdiags){
        ArrayAdapter medsAdapter= new ArrayAdapter<>(getActivity(),R.layout.listview_layout,patientdiags);
        diagsList.setAdapter(medsAdapter);
    }
}
