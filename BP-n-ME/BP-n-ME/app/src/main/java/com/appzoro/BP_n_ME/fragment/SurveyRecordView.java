package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.adapter.RecyclerViewAdapter;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Appzoro_ 5 on 9/20/2017.
 */

public class SurveyRecordView extends Fragment implements View.OnClickListener {
    View view;
    String date;
    ImageView img_Back;
    TextView headerText, noRecords;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    final List<String> questions = new ArrayList<>();
    ArrayList<String[]> questionchoices = new ArrayList<>();
    ArrayList<String> choicesList = new ArrayList<>();
    String[] questionArray = new String[questions.size()];
    int position;
    String UID, medName;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_surveyrecordview, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        return view;
    }

    private void init() {
        Bundle bundle = this.getArguments();
        date = bundle.getString("date");
        position = bundle.getInt("position");
        medName = bundle.getString("medName");
        //Log.e("position", ""+ position);

        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();
        img_Back = view.findViewById(R.id.iv_back);
        headerText = view.findViewById(R.id.text_heading);
        recyclerView = view.findViewById(R.id.recyclerView);
        noRecords = view.findViewById(R.id.noRecord);

        img_Back.setOnClickListener(this);

        /*if (position == 0){
            headerText.setText("Symptom Survey Record");
            DailySymptomSurvey();
        }
        else*/
        if (position == 0){
            headerText.setText("ARMS-7 Survey Record");
            ArmsSurvey();
        }
        else if (position == 1){
            headerText.setText("LifeStyle Survey Record");
            LifeStyleSurvey();
        }
        else if (position == 2){
            headerText.setText("STOFHLA Record");
            HealthSurvey();
        }
        else if (position == 3){
            headerText.setText("App Satisfaction Survey Record");
            AppSatisfactionSurvey();
        }
        else if (position > 3){
            headerText.setText(medName+" Survey Record");
            MedicationSurvey(medName);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                getFragmentManager().popBackStack();
                break;
        }
    }

    public void DailySymptomSurvey(){
        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.DailySymptomSurvey);
        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        //get Daily Symptom Survey Answers
        getSymptomSurveyAns();
    }

    public void ArmsSurvey(){
        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.ArmsSurvey);
        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        //get Daily Symptom Survey Answers
        getArmsSurveyAns();
    }

    private void LifeStyleSurvey() {
        //get questions from xml file

        //String[] mArray = getResources().getStringArray(R.array.LifestyleSurvey);
        String[] mArray = {
                "Do you exercise (ex. walking, jogging, running, chair exercises, swimming, or lifting weights)?_Never_1–2 times per month or less_3-4 times per month_2-4 times per week_5 times per week or more",
                "Do you smoke cigarettes or use smokeless tobacco products?_Never_1-2 packs/cans per month or less_3-4 packs/cans per month_2-4 packs/cans per week_5 packs/cans per week or more",
                "Do you plan on quitting smoking cigarettes or using smokeless tobacco products in the next 2 weeks?_Yes_No_N/A",
                "Do you plan on reducing the number of cigarettes smoked or frequency of smokeless tobacco product use in the next 2 weeks?_Yes_No_N/A",
                "Do you drink alcohol?_Never_7 times per month or less_2-4 times a week_5-7 times per week_More than once per day",
                "Do you plan on quitting drinking alcohol in the next 2 weeks?_Yes_No_N/A",
                "Do you plan on reducing the number of alcoholic beverages in the next 2 weeks?_Yes_No_N/A",
                "Do you follow a low salt diet?_Never_1 meal per week or less_2-6 meals per week_1-2 meals per day_Every meal" };
        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }
        //System.out.println(mArray.toString());
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        getLifeStyleSurveyAns();
    }

    private void HealthSurvey() {
        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.LiteracySurvey);
        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }
        //System.out.println(mArray.toString());
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        getHealthSurveyAns();
    }

    private void AppSatisfactionSurvey() {
        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.AppSatisfactionSurvey);
        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }
        //System.out.println(mArray.toString());
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        getAppSatisfactionSurveyAns();
    }

    private void MedicationSurvey(String medName) {
        String[] mArray = {"I find it easy to take "+medName+" every day at the correct times._Strongly agree_Somewhat agree_Do not agree or disagree_Somewhat disagree_Strongly disagree",
                "The frequency at which I am supposed to take it is the biggest barrier to taking "+medName+" correctly._Strongly agree_Somewhat agree_Do not agree or disagree_Somewhat disagree_Strongly disagree",
                "The shape or taste of the pill is the biggest barrier to taking "+medName+" correctly._Strongly agree_Somewhat agree_Do not agree or disagree_Somewhat disagree_Strongly disagree",
                "The price of "+medName+" is the biggest barrier to taking it correctly._Strongly agree_Somewhat agree_Do not agree or disagree_Somewhat disagree_Strongly disagree",
                "I do not take "+medName+" according to the prescribed schedule because it does not work for me._Strongly agree_Somewhat agree_Do not agree or disagree_Somewhat disagree_Strongly disagree_N/A",
                "I do not take "+medName+" according to the prescribed schedule because it has an unpleasant side effect._Strongly agree_Somewhat agree_Do not agree or disagree_Somewhat disagree_Strongly disagree_N/A",
                "I do not take "+medName+" according to the prescribed schedule because I have trouble remembering to do so._Strongly agree_Somewhat agree_Do not agree or disagree_Somewhat disagree_Strongly disagree_N/A",
                "What factors make "+medName+" easy or difficult to take according to the prescribed schedule?_ans" };

        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        getMedicationSurveyAns(medName);
    }

    public void getSymptomSurveyAns() {
        final ArrayList<String> responseDate = new ArrayList<>();
        final ArrayList<String> responseArray = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("symptomsurveyanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            responseDate.add(lifedate);
                            responseArray.add(attempts);
                        }
                        setSurveyAns(responseDate, responseArray);
                        if (responseDate.isEmpty()){
                            noRecords.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getArmsSurveyAns() {
        final ArrayList<String> responseDate = new ArrayList<>();
        final ArrayList<String> responseArray = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("arms7surveyanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            responseDate.add(lifedate);
                            responseArray.add(attempts);
                        }
                        setSurveyAns(responseDate, responseArray);
                        if (responseDate.isEmpty()){
                            noRecords.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getLifeStyleSurveyAns() {
        final ArrayList<String> responseDate = new ArrayList<>();
        final ArrayList<String> responseArray = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("lifestylesurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            if (!attempts.contains("-1")){
                                responseDate.add(lifedate);
                                responseArray.add(attempts);
                            }
                        }
                        setLifeStyleSurveyAns(responseDate, responseArray);
                        if (responseDate.isEmpty()){
                            noRecords.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getHealthSurveyAns() {
        final ArrayList<String> responseDate = new ArrayList<>();
        final ArrayList<String> responseArray = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("literacysurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            if (!attempts.contains("-1")){
                                responseDate.add(lifedate);
                                responseArray.add(attempts);
                            }
                        }
                        setSurveyAns(responseDate, responseArray);
                        if (responseDate.isEmpty()){
                            noRecords.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getAppSatisfactionSurveyAns() {
        final ArrayList<String> responseDate = new ArrayList<>();
        final ArrayList<String> responseArray = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("appsatisfactionanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            responseDate.add(lifedate);
                            responseArray.add(attempts);
                        }
                        setAppSatisfactionSurveyAns(responseDate, responseArray);
                        if (responseDate.isEmpty()){
                            noRecords.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getMedicationSurveyAns(String medName) {
        final ArrayList<String> responseDate = new ArrayList<>();
        final ArrayList<String> responseArray = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child(medName +"medadherenceanswers").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String lifedate = medicine.getKey();
                            String attempts = medicine.getValue().toString();
                            responseDate.add(lifedate);
                            responseArray.add(attempts);
                        }
                        setMedicationSurveyAns(responseDate, responseArray);
                        if (responseDate.isEmpty()){
                            noRecords.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setSurveyAns(ArrayList<String> responseDate, ArrayList<String> responseArray) {
        for (int i=0; i < responseDate.size();i++){
            if (date.equals(responseDate.get(i).trim())){
                ArrayList<String> ansList =  new ArrayList<>(Arrays.asList(responseArray.get(i).replace("[","").replace("]","").replace(" ","").split(",")));
                for (int j=0; j<ansList.size(); j++){
                    int choice = Integer.parseInt(ansList.get(j));
                    choicesList.add(questionchoices.get(j)[choice]);
                }
            }
            if (i == responseDate.size()-1){
                RecyclerView(choicesList);
            }
        }
    }

    private void setLifeStyleSurveyAns(ArrayList<String> responseDate, ArrayList<String> responseArray) {
        for (int i=0; i < responseDate.size();i++){
            if (date.equals(responseDate.get(i).trim())){
                ArrayList<String> ansList =  new ArrayList<>(Arrays.asList(responseArray.get(i).replace("[","").replace("]","").replace(" ","").split(",")));
                for (int j=0; j<ansList.size(); j++){
                    int choice = Integer.parseInt(ansList.get(j).trim().replace("-2","2"));
                    //int choice = Integer.parseInt(ansList.get(j));
                    choicesList.add(questionchoices.get(j)[choice]);
                }
            }
            if (i == responseDate.size()-1){
                RecyclerView(choicesList);
            }
        }
    }

    private void setAppSatisfactionSurveyAns(ArrayList<String> responseDate, ArrayList<String> responseArray) {
        for (int i=0; i < responseDate.size();i++){
            if (date.equals(responseDate.get(i).trim())){
                ArrayList<String> ansList =  new ArrayList<String>(Arrays.asList(responseArray.get(i).replace("[","").replace("]","").trim().split(",")));
                for (int j=0; j<ansList.size(); j++){
                    if (j==18 || j==19 || j==20){
                        choicesList.add(ansList.get(j));
                    }
                    else {
                        int choice = Integer.parseInt(ansList.get(j).trim());
                        choicesList.add(questionchoices.get(j)[choice]);
                    }
                }
            }
            if (i == responseDate.size()-1){
                RecyclerView(choicesList);
            }
        }
    }

    private void setMedicationSurveyAns(ArrayList<String> responseDate, ArrayList<String> responseArray) {
        for (int i=0; i < responseDate.size();i++){
            if (date.equals(responseDate.get(i).trim())){
                ArrayList<String> ansList =  new ArrayList<String>(Arrays.asList(responseArray.get(i).replace("[","").replace("]","").trim().split(",")));
                for (int j=0; j<ansList.size(); j++){
                    if (j==7){
                        choicesList.add(ansList.get(j));
                    }
                    else {
                        int choice = Integer.parseInt(ansList.get(j).trim().replace("-1","5"));
                        choicesList.add(questionchoices.get(j)[choice]);
                    }
                }
            }
            if (i == responseDate.size()-1){
                RecyclerView(choicesList);
            }
        }
    }

    private void RecyclerView(ArrayList<String> answerList) {
        //Log.e("answerList",""+ answerList);
        if (answerList.isEmpty()){
            noRecords.setVisibility(View.VISIBLE);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), questionArray, answerList, recyclerView);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((PatientMonitor)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((PatientMonitor)getActivity()).getSupportActionBar().show();
    }
}
