package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.adapter.SurveyAdapter;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Appzoro_ 5 on 9/20/2017.
 */

public class SurveyRecordFragment extends Fragment implements AdapterView.OnItemClickListener {
    View view;
    ListView surveyslist;
    ArrayList<String> surveyList, medsList;
    SurveyAdapter sAdapter;
    MedasolPrefs prefs;
    String medName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_surveyrecords, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Patient Monitor");
        ((PatientMonitor) getActivity()).getSupportActionBar().setSubtitle("Survey Record");
        init();
        return view;
    }

    private void init() {
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        surveyslist = view.findViewById(R.id.surveysList);
        surveyList = new ArrayList<>();
        medsList = new ArrayList<>();
        //surveyList.add("Daily Symptom Survey");
        surveyList.add("ARMS-7 Survey");
        surveyList.add("LifeStyle Survey");
        surveyList.add("STOFHLA Survey");
        surveyList.add("Mobile App Satisfaction Survey");
        List<String> medication =  new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")));
        Log.e("meds", String.valueOf(medication));
        /*for (int i = 0; i < medication.size(); i++) {
            medName = Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")).get(i);
            surveyList.add("Medication Adherence: "+medName);
            medsList.add(medName);
        }*/

        sAdapter = new SurveyAdapter(getActivity(), surveyList);
        surveyslist.setAdapter(sAdapter);
        surveyslist.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        SurveyRecordCalendar fragment = new SurveyRecordCalendar();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        if (position > 3){
            bundle.putString("medName", medsList.get(position-3));
        }
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace
                (R.id.Fragment_frame1, fragment).addToBackStack("SurveyRecordCalendar").commit();
    }
}
