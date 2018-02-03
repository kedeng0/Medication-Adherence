package com.appzoro.BP_n_ME.fragment;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Appzoro_ 5 on 8/4/2017.
 */

public class LifestyleFeedbackFragment extends Fragment implements View.OnClickListener {
    View view;
    ImageView img_back;
    TextView finish;
    ListView FeedbackList;
    ArrayAdapter<String> adapter;
    String surDate;
    int[] surveyResponse;
    MedasolPrefs prefs;
    public static final String LSURVEY_FILENAME = "lsurvey_file";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lifestylefeedback ,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        img_back = view.findViewById(R.id.iv_back);
        FeedbackList = view.findViewById(R.id.LifestyleFeedbackView);
        finish = view.findViewById(R.id.tv_finish);

        final String[] posArray = getResources().getStringArray(R.array.LifestylePositiveMessagesArray);
        final String[] negArray = getResources().getStringArray(R.array.LifestyleNegativeMessagesArray);
        final int[] correctChoice = {4, 1, 2, 2, 1, 2, 2, 4};
        final int[] wrongChoice = {0, 0, 1, 1, 0, 1, 1, 0};
        ArrayList<String> responsePosArray = new ArrayList<>();
        ArrayList<String> responseNegArray = new ArrayList<>();
        ArrayList<String> responseArray = new ArrayList<>();

        Bundle bundle = this.getArguments();
        surDate = bundle.getString("date");
        Log.e("date ",surDate);

        getActivity().deleteFile(LSURVEY_FILENAME);
        try {
            FileOutputStream fos = getActivity().openFileOutput(LSURVEY_FILENAME, Context.MODE_APPEND);
            fos.write(surDate.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.e("Feedback surDate",surDate);

        try {
            String sampleString = prefs.getLifestyleSurveyAnswersRW(); //"101,203,405";
            Log.e("string", sampleString);

            String[] stringArray = sampleString.split(",");
            surveyResponse = new int[stringArray.length];
            for (int i = 0; i < stringArray.length; i++) {
                String numberAsString = stringArray[i];
                surveyResponse[i] = Integer.parseInt(numberAsString.replace("[","").replace("]","").replace(" ",""));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        Log.e("eee", Arrays.toString(surveyResponse));

        if (surveyResponse[0] !=-1) {
            for (int ind = 0; ind < surveyResponse.length; ind++) {
                if (surveyResponse[ind] == correctChoice[ind]) {
                    responsePosArray.add(Integer.toString(ind + 1) + ". " + posArray[ind]);
                } else if (wrongChoice[ind] == 0 | surveyResponse[ind] == wrongChoice[ind]) {
                    responseNegArray.add(Integer.toString(ind + 1) + ". " + negArray[ind]);
                }
            }

            if (!responsePosArray.isEmpty()) {
                responseArray.add("Positive Feedback");
                responseArray.addAll(responsePosArray);
            }
            if (!responsePosArray.isEmpty() & !responseNegArray.isEmpty()) {
                responseArray.add(" ");
            }
            if (!responseNegArray.isEmpty()) {
                responseArray.add("Educational Feedback");
                responseArray.addAll(responseNegArray);
            }

            adapter = new ArrayAdapter<String>(getActivity(), R.layout.tip_of_the_day, responseArray);
            FeedbackList.setAdapter(adapter);
        }
    }

    private void Listener() {
        img_back.setOnClickListener(this);
        finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, new SurveysFragment()).commit();
                break;
            case R.id.tv_finish:
                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, new SurveysFragment()).commit();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity)getActivity()).getSupportActionBar().show();
    }
}
