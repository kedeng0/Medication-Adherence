package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.Space;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Appzoro_ 5 on 8/10/2017.
 */

public class HealthSurveyFragment extends Fragment implements View.OnClickListener {
    View view;
    ImageView img_back;
    TextView submit,next,questionsView;
    ScrollView scrollView, scroll;
    RadioGroup rg;
    Button q1,q2,q3,q4,q5,q6,q7,q8,q9,q10,q11,q12,q13,q14,q15,q16,q17,q18,q19,q20,q21,q22,q23,q24,q25,q26,q27,q28,q29,q30,q31,q32,q33,q34,q35,q36;
    ArrayList<String[]> questionchoices = new ArrayList<>();
    ArrayList<String[]> key = new ArrayList<>();
    final ArrayList<String> questions = new ArrayList<>();
    String[] questionArray = new String[questions.size()];
    final ArrayList<Integer> arrOpenParen = new ArrayList<>();
    final ArrayList<Integer> arrCloseParen = new ArrayList<>();
    final ArrayList<String> questionFilled = new ArrayList<>();
    final ArrayList<String> answersFilled = new ArrayList<>();
    ArrayList<Button> questionlist = new ArrayList<Button>();
    int [] naArray;
    int [] answers;
    int [] rightorwrong;
    int selected =0;
    int number, questionnumber, qnum, i;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_healthsurvey ,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        img_back = view.findViewById(R.id.iv_back);
        submit = view.findViewById(R.id.tv_submit);
        next = view.findViewById(R.id.tv_next);
        scroll = view.findViewById(R.id.scroll);
        scrollView = view.findViewById(R.id.scrollView);
        q1 = view.findViewById(R.id.q1);
        q2 = view.findViewById(R.id.q2);
        q3 = view.findViewById(R.id.q3);
        q4 = view.findViewById(R.id.q4);
        q5 = view.findViewById(R.id.q5);
        q6 = view.findViewById(R.id.q6);
        q7 = view.findViewById(R.id.q7);
        q8 = view.findViewById(R.id.q8);
        q9 = view.findViewById(R.id.q9);
        q10 = view.findViewById(R.id.q10);
        q11 = view.findViewById(R.id.q11);
        q12 = view.findViewById(R.id.q12);
        q13 = view.findViewById(R.id.q13);
        q14 = view.findViewById(R.id.q14);
        q15 = view.findViewById(R.id.q15);
        q16 = view.findViewById(R.id.q16);
        q17 = view.findViewById(R.id.q17);
        q18 = view.findViewById(R.id.q18);
        q19 = view.findViewById(R.id.q19);
        q20 = view.findViewById(R.id.q20);
        q21 = view.findViewById(R.id.q21);
        q22 = view.findViewById(R.id.q22);
        q23 = view.findViewById(R.id.q23);
        q24 = view.findViewById(R.id.q24);
        q25 = view.findViewById(R.id.q25);
        q26 = view.findViewById(R.id.q26);
        q27 = view.findViewById(R.id.q27);
        q28 = view.findViewById(R.id.q28);
        q29 = view.findViewById(R.id.q29);
        q30 = view.findViewById(R.id.q30);
        q31 = view.findViewById(R.id.q31);
        q32 = view.findViewById(R.id.q32);
        q33 = view.findViewById(R.id.q33);
        q34 = view.findViewById(R.id.q34);
        q35 = view.findViewById(R.id.q35);
        q36 = view.findViewById(R.id.q36);
        questionsView = view.findViewById(R.id.questionView);
        rg = view.findViewById(R.id.radiogroup);

        questionlist.add(q1);
        questionlist.add(q2);
        questionlist.add(q3);
        questionlist.add(q4);
        questionlist.add(q5);
        questionlist.add(q6);
        questionlist.add(q7);
        questionlist.add(q8);
        questionlist.add(q9);
        questionlist.add(q10);
        questionlist.add(q11);
        questionlist.add(q12);
        questionlist.add(q13);
        questionlist.add(q14);
        questionlist.add(q15);
        questionlist.add(q16);
        questionlist.add(q17);
        questionlist.add(q18);
        questionlist.add(q19);
        questionlist.add(q20);
        questionlist.add(q21);
        questionlist.add(q22);
        questionlist.add(q23);
        questionlist.add(q24);
        questionlist.add(q25);
        questionlist.add(q26);
        questionlist.add(q27);
        questionlist.add(q28);
        questionlist.add(q29);
        questionlist.add(q30);
        questionlist.add(q31);
        questionlist.add(q32);
        questionlist.add(q33);
        questionlist.add(q34);
        questionlist.add(q35);
        questionlist.add(q36);

        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.LiteracySurvey);
        final ArrayList<String> questions = new ArrayList<>();

        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }

        System.out.println(mArray.toString());
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        //get key from xml file
        final String[] keyArray = getResources().getStringArray(R.array.LiteracySurveyAnswers);
        for (String full : keyArray) {
            String[] split = full.split("_");
            key.add(Arrays.copyOfRange(split, 0, split.length));
        }

        //get notApplicable list from xml file
        String[] naArray1 = getResources().getStringArray(R.array.LiteracySurveyAnswersNA);
        naArray = new int[naArray1.length];
        int counter = 0;
        for (String full : naArray1) {
            naArray[counter] = Integer.parseInt(full);
            counter++;
        }

        //initialize results arrays
        answers = new int[mArray.length];
        Arrays.fill(answers, -1); //default for unanswered
        rightorwrong = new int[mArray.length];
        Arrays.fill(rightorwrong, 1); //default for wrong

        // set default que on question View
        i=0;
        if(!questionsView.getText().equals(Integer.toString(qnum + 1+i) + ".  " + parseQuestion(qnum+i))){
            rg.removeAllViews();
            add(i);
        }
    }

    private void Listener() {
        img_back.setOnClickListener(this);
        next.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.tv_next:
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_UP);
                    }
                });
                if (i < 35){
                    if (answers[qnum+i] != -1){
                        rg.removeAllViews();
                        i++;
                        add(i);
                    } else {
                        Toast.makeText(getActivity(), "Please select the answer", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv_submit:
                selected=0;
                int nonselected = 0;
                for (int x : answers) {
                    if (x != -1) {
                        selected++;
                    } else{
                        nonselected++;
                    }
                }
                if (selected>35) {
                    String UID = prefs.getUID();
                    Snackbar.make(view, "STOFHLA Survey Successfully Completed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    System.out.println(dateFormat.format(cal.getTime()));
                    final String currentDate = dateFormat.format(cal.getTime());
                    //***********************************************************************************************

                    mDatabase.child("app").child("users").child(UID).child("literacysurveyanswersRW").child(currentDate) .setValue(Arrays.toString(answers));
                    prefs.setLiteracySurveyAnswers(answers);
                    prefs.setLiteracyDate(currentDate);

                    getFragmentManager().beginTransaction().replace
                            (R.id.Fragment_frame_main_activity, new SurveysFragment()).commit();
                }
                else{
                    Snackbar.make(view, "Please complete all 36 questions. "+nonselected+" questions remain.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
        }
    }

    public void add(int i){
        if( i >= 0 && i <= 35){
            questionsView.setText(Integer.toString(qnum+1+i)+".  "+ parseQuestion(qnum+i));
            number = questionchoices.get(qnum+i).length - 1;
            addRadioButtons(number, qnum+i);
            if(answers[qnum+i]+36 != rg.getCheckedRadioButtonId()){
                //Log.e("access","radiogr");
                rg.check(answers[qnum+i]+36);
            }
        }

        if (i==35){
            submit.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
        }
        else {
            submit.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }
    }

    public void addRadioButtons(int number, final int question) {
        final int numberRB = number;
        questionnumber = question;
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId > 35) {
                    answers[questionnumber] = checkedId - 36;
                    if(group.getCheckedRadioButtonId()!=answers[questionnumber]+36){
                        group.check(answers[questionnumber]+36);
                    }
                }
                for (int i=0;i<answers.length;i++) {
                    //Log.e("answer", String.valueOf(answers[i]));
                }
            }
        });


        for (int i = 36; i <= number+36; i++) {
            final RadioButton rdbtn = new RadioButton(getActivity());
            rdbtn.setText(questionchoices.get(questionnumber)[i-36]+" ");
            rdbtn.setTextSize(18);
            rdbtn.setId(i);

            Space space = new Space(getActivity());
            space.setMinimumHeight(20);
            rg.addView(space);
            rg.addView(rdbtn);

            if(answers[question]==i-36){
                if(!rdbtn.isChecked()) {
                    //Log.e("access","rdbtn");
                    rdbtn.setChecked(true);

                }
            }

            else{
                rdbtn.setChecked(false);
            }
            rdbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //Log.e("RBID", Integer.toString(compoundButton.getId()));
                    if (b){
                        for (int i=36;i<=numberRB+36;i++){
                            if(i!=(int)compoundButton.getId()){
                                //Log.e("Color"+i,"TRANS"+numberRB);
                                questionlist.get(questionnumber).setBackgroundResource(R.drawable.question_green);
                                questionlist.get(questionnumber).setTextColor(getResources().getColor(R.color.white));
                                ((RadioButton)view.findViewById(i)).setBackgroundColor(Color.TRANSPARENT);
                            }
                            else{
                                if(!rdbtn.isChecked()) {
                                    rdbtn.setChecked(b);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public String parseQuestion(int z) {
        arrOpenParen.clear();
        arrCloseParen.clear();
        questionFilled.clear();
        answersFilled.clear();

        int difference=0;
        int posOfQuestion = 0;
        String questionParse = questionArray[z];
        String newQuestion="";
        int closeParen = 0;
        int openParen = 0;
        closeParen = questionParse.indexOf(")");
        openParen = questionParse.indexOf("(");

        while (openParen!=-1){
            arrOpenParen.add(openParen);
            arrCloseParen.add(closeParen);
            closeParen = questionParse.indexOf(")",closeParen+1);
            openParen = questionParse.indexOf("(",openParen+1);
        }
        int total = arrOpenParen.size();
        for (int i=0;i<arrCloseParen.size();i++){
            if (arrOpenParen.get(i)==(arrCloseParen.get(i)-2)){
                posOfQuestion = i;
            }
        }

        for(int i=0;i<total;i++){
            if (i==0) {
                questionFilled.add(questionParse.substring(0, arrOpenParen.get(i)));
            }
            else{
                questionFilled.add(questionParse.substring(arrCloseParen.get(i-1)+1, arrOpenParen.get(i)));
            }
        }
        questionFilled.add(questionParse.substring(arrCloseParen.get(total-1)+1,questionParse.length()));
        for (int i=0;i<total;i++){
            difference = posOfQuestion-i;
            if (difference==0){
                if (answers[z] != -1){
                    answersFilled.add(i,questionchoices.get(z)[answers[z]] );
                }
                else{
                    answersFilled.add(i,"(?)");
                }
            }
            else{
                if (answers[z-difference] != -1){
                    answersFilled.add(i,questionchoices.get(z-difference)[answers[z-difference]] );
                }
                else{
                    answersFilled.add(i,"()");
                }
            }
        }
        for (int i=0;i<answersFilled.size();i++) {
            if (!answersFilled.get(i).contains("(")){
                newQuestion = newQuestion.concat(questionFilled.get(i)).concat("(").concat(answersFilled.get(i)).concat(")");
            }
            else {
                newQuestion = newQuestion.concat(questionFilled.get(i)).concat(answersFilled.get(i));
            }
        }

        newQuestion = newQuestion.concat(questionFilled.get(answersFilled.size()));
        return newQuestion;
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
