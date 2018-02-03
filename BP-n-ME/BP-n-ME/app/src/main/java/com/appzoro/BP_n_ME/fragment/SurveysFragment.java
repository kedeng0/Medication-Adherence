package com.appzoro.BP_n_ME.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Appzoro_ 5 on 6/17/2017.
 */

public class SurveysFragment extends Fragment implements View.OnClickListener{
    View view;
    LinearLayout surveys;
    TextView lifestylesurvey_tv, healthLiteracySurvey_tv, mobileappsurvey_tv, symptomSurvey, armsSurvey;
    int[] surveyResponse;
    int[] LiteracysurveyResponse;
    String LifeStyleDate, LifeStyleParseDate, LiteracyDate, LiteracyParseDate, symptomDate, symptomParseDate, armsDate, armsParseDate;
    MedasolPrefs prefs;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                              Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_surveys ,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Survey List");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");

        prefs = new MedasolPrefs(getActivity().getApplicationContext());

        try{
            String sampleString = prefs.getLifestyleSurveyAnswersRW(); //"101,203,405";
            //Log.e("string", sampleString);
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

        LifeStyleDate = prefs.getLifestyleDate();
        //LiteracyDate = prefs.getLiteracyDate();
        symptomDate = prefs.getSymptomSurveyDate();
        armsDate = prefs.getArmsDate();
        Log.e("armsDate", "  date "+armsDate);
        //Log.e("symptomDate", "  date "+symptomDate);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date Date = inputFormat.parse(LifeStyleDate);
            LifeStyleParseDate = outputFormat.format(Date);

            /*Date Date1 = inputFormat.parse(symptomDate);
            symptomParseDate = outputFormat.format(Date1);*/

            Date Date2 = inputFormat.parse(armsDate);
            armsParseDate = outputFormat.format(Date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (surveyResponse[0]!= -1) {
            ((TextView) view.findViewById(R.id.tv_dateLifeStyleSurvey)).setText("Lifestyle Survey:  " + LifeStyleParseDate);
        }
        else{
            ((TextView) view.findViewById(R.id.tv_dateLifeStyleSurvey)).setText("Lifestyle Survey:  Not Taken Yet");
        }

        Log.e("armsParseDate", "  date "+armsParseDate);
        if (!armsDate.equals("")) {
            ((TextView) view.findViewById(R.id.tv_datesymptomSurvey)).setText("ARMS-7 Survey:  " + armsParseDate);
        }
        else{
            ((TextView) view.findViewById(R.id.tv_datesymptomSurvey)).setText("ARMS-7 Survey:  Not Taken Yet");
        }

        /*try{
            String sampleString1 = prefs.getLiteracySurveyAnswers();   //"101,203,405";
            String[] stringArray1 = sampleString1.split(",");
            LiteracysurveyResponse = new int[stringArray1.length];
            for (int i = 0; i < stringArray1.length; i++) {
                String numberAsString = stringArray1[i];
                LiteracysurveyResponse[i] = Integer.parseInt(numberAsString.replace("[","").replace("]","").replace(" ",""));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        if (LiteracysurveyResponse[0] != -1) {
            ((TextView) view.findViewById(R.id.tv_dateLiteracySurvey)).setText("Literacy Survey:  " + LiteracyParseDate );
        }
        else{
            ((TextView) view.findViewById(R.id.tv_dateLiteracySurvey)).setText("Literacy Survey:  Not Taken Yet");
        }*/

       /* if (!symptomDate.equals("")) {
            ((TextView) view.findViewById(R.id.tv_datesymptomSurvey)).setText("Symptom Survey:  " + symptomParseDate);
        }
        else{
            ((TextView) view.findViewById(R.id.tv_datesymptomSurvey)).setText("Symptom Survey:  Not Taken Yet");
        }*/

        init();
        Listener();
        return view;
    }

    private void init() {
        surveys = view.findViewById(R.id.ll_surveys);
        //symptomSurvey =  view.findViewById(R.id.tv_symptomSurvey);
        armsSurvey =  view.findViewById(R.id.tv_armsSurvey);
        lifestylesurvey_tv =  view.findViewById(R.id.tv_lifeStyleSurvey);
        healthLiteracySurvey_tv =  view.findViewById(R.id.tv_healthLiteracySurvey);
        mobileappsurvey_tv = view.findViewById(R.id.tv_mobileSurvey);
        List<String> medication =  new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")));
        Log.e("meds", String.valueOf(medication));
        /*for (int i = 0; i < medication.size(); i++) {
            final String medName = Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")).get(i);
            TextView med = new TextView(getActivity());
            LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            med.setText("MEDICATION ADHERENCE: "+medName);
            med.setTextColor(Color.BLACK);
            med.setGravity(Gravity.CENTER|Gravity.START);
            med.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_black, 0);
            med.setBackgroundResource(R.drawable.edit_text_dynamic_border);
            params.setMargins(0,30,0,0);//pass int values for left,top,right,bottom
            med.setLayoutParams(params);
            med.setId(i);
            med.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MedicationSurveyFragment fragment = new MedicationSurveyFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("medName",medName);
                    fragment.setArguments(bundle);

                    getFragmentManager().beginTransaction().add
                            (R.id.Fragment_frame_main_activity, fragment).addToBackStack("MedicationSurveyFragment").commit();
                }
            });
            if (!medName.equals("")){
                surveys.addView(med);
            }
        }*/
    }

    private void Listener() {
        //symptomSurvey.setOnClickListener(this);
        armsSurvey.setOnClickListener(this);
        lifestylesurvey_tv.setOnClickListener(this);
        mobileappsurvey_tv.setOnClickListener(this);
        healthLiteracySurvey_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_lifeStyleSurvey:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Take Survey or See Feedback");
                alert.setMessage("Would you like to take the lifestyle survey or see previous feedback?");
                alert.setPositiveButton("Survey", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getFragmentManager().beginTransaction().add
                                (R.id.Fragment_frame_main_activity, new LifeStyleSurveyFragment()).addToBackStack("LifeStyleSurveyFragment").commit();
                    }
                });
                alert.setNegativeButton("Feedback", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton) {
                        if (surveyResponse[0]!=-1) {
                            String lastDate = prefs.getLifestyleDate();
                            LifestyleFeedbackFragment fragment = new LifestyleFeedbackFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("date", lastDate);
                            fragment.setArguments(bundle);
                            getFragmentManager().beginTransaction().add
                                    (R.id.Fragment_frame_main_activity, fragment).addToBackStack("LifestyleFeedbackFragment").commit();
                        } else {
                            Toast.makeText(getActivity(), "No Previous FeedBack", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                break;
            case R.id.tv_mobileSurvey:
                getFragmentManager().beginTransaction().add
                        (R.id.Fragment_frame_main_activity, new AppSatisfactionSurveyFragment()).addToBackStack("MobileSurveyFragment").commit();
                break;
            case R.id.tv_healthLiteracySurvey:
                getFragmentManager().beginTransaction().add
                        (R.id.Fragment_frame_main_activity, new HealthInfoFragment()).addToBackStack("HealthInfoFragment").commit();
                break;
            /*case R.id.tv_symptomSurvey:
                getFragmentManager().beginTransaction().add
                        (R.id.Fragment_frame_main_activity, new SymptomSurveyFragment()).addToBackStack("SymptomSurveyFragment").commit();
                break;*/
            case R.id.tv_armsSurvey:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle("Take Survey or See Feedback");
                alertBuilder.setMessage("Would you like to take the ARMS-7 survey or see previous feedback?");
                alertBuilder.setPositiveButton("Survey", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getFragmentManager().beginTransaction().add
                                (R.id.Fragment_frame_main_activity, new ARMSSurveyFragment()).addToBackStack("ARMSSurveyFragment").commit();
                    }
                });
                alertBuilder.setNegativeButton("Feedback", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton) {
                        if (!armsDate.equals("")) {
                            ARMSFeedbackFragment fragment = new ARMSFeedbackFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("date", armsDate);
                            fragment.setArguments(bundle);
                            getFragmentManager().beginTransaction().add
                                    (R.id.Fragment_frame_main_activity, fragment).addToBackStack("ARMSFeedbackFragment").commit();
                        } else {
                            Toast.makeText(getActivity(), "No Previous FeedBack", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    }
                });
                AlertDialog alertDialog1 = alertBuilder.create();
                alertDialog1.show();
                break;
        }
    }

}