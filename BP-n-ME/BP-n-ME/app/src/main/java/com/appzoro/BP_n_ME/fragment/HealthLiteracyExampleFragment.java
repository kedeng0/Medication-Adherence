package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.Space;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Appzoro_ 5 on 7/29/2017.
 */

public class HealthLiteracyExampleFragment extends Fragment implements View.OnClickListener {
    View view;
    ImageView img_back;
    Button q1,q2;
    RadioGroup rg;
    RadioButton opt1,opt2,opt3,opt4;
    ScrollView scrollView;
    TextView submit,next,questionsView;
    final ArrayList<String> questions = new ArrayList<>();
    final ArrayList<Integer> arrOpenParen = new ArrayList<>();
    final ArrayList<Integer> arrCloseParen = new ArrayList<>();
    final ArrayList<String> questionFilled = new ArrayList<>();
    final ArrayList<String> answersFilled = new ArrayList<>();
    ArrayList<Button> questionlist = new ArrayList<>();
    String[] questionArray = new String[questions.size()];
    ArrayList<String[]> questionchoices = new ArrayList<>();
    ArrayList<String[]> key = new ArrayList<>();
    int [] naArray;
    int [] answers;
    int [] rightorwrong;
    int qnum, i, number, questionnumber;
    int selected =0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_healthliteracyexample,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        img_back = view.findViewById(R.id.iv_back);
        scrollView = view.findViewById(R.id.scrollView);
        q1 = view.findViewById(R.id.q1);
        q2 = view.findViewById(R.id.q2);
        rg = view.findViewById(R.id.radiogroup);
        opt1 = view.findViewById(R.id.opt1);
        opt2 = view.findViewById(R.id.opt2);
        opt3 = view.findViewById(R.id.opt3);
        opt4 = view.findViewById(R.id.opt4);
        next = view.findViewById(R.id.tv_next);
        submit = view.findViewById(R.id.tv_submit);
        questionsView = view.findViewById(R.id.questionView);

        questionlist.add(q1);
        questionlist.add(q2);

        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.LiteracySurveyExample);
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
        final String[] keyArray = getResources().getStringArray(R.array.LitExampleSurveyAnswers);
        for (String full : keyArray) {
            String[] split = full.split("_");
            key.add(Arrays.copyOfRange(split, 0, split.length));
        }

        //get notApplicable list from xml file
        String[] naArray1 = getResources().getStringArray(R.array.LitExampleSurveyAnswersNA);
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
        /*i=0;
        if(!questionsView.getText().equals(Integer.toString(qnum + 1+i) + ".  " + parseQuestion(qnum+i))){
            rg.removeAllViews();
            add(i);
        }*/

        next.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.INVISIBLE);
    }

    private void Listener() {
        img_back.setOnClickListener(this);
        q1.setOnClickListener(this);
        q2.setOnClickListener(this);
        next.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.q1:
                i=0;
                if(!questionsView.getText().equals(Integer.toString(qnum + 1+i) + ".  " + parseQuestion(qnum+i))){
                    rg.removeAllViews();
                    add(i);
                }
                break;
            case R.id.q2:
                i=1;
                if(!questionsView.getText().equals(Integer.toString(qnum + 1+i) + ".  " + parseQuestion(qnum+i))){
                    rg.removeAllViews();
                    add(i);
                }
                break;
            case R.id.tv_next:
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_UP);
                    }
                });

                if (i < 1){
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
                    }
                    else{
                        nonselected++;
                    }
                }
                if (selected > 1) {
                    getFragmentManager().beginTransaction().replace
                            (R.id.Fragment_frame_main_activity, new HealthSurveyFragment()).addToBackStack("HealthSurveyFragment").commit();
                } else{
                    Snackbar.make(view, "Please complete all questions. "+nonselected+" questions remain.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
        }
    }

    public void add(int i){
        if( i >= 0 && i <= 1){
                questionsView.setText(Integer.toString(qnum+1+i)+".  "+ parseQuestion(qnum+i));
                number = questionchoices.get(qnum+i).length - 1;
                addRadioButtons(number, qnum+i);
                if(answers[qnum+i]+2 != rg.getCheckedRadioButtonId()){
                    Log.e("access","radiogr");
                    rg.check(answers[qnum+i]+2);
                }
        }

        if (i==1){
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
                if(checkedId > 1) {
                    Log.e("b","bbbb");
                    questionsView.setText(Integer.toString(questionnumber+1)+".  "+ parseQuestion(questionnumber));
                    answers[questionnumber] = checkedId - 2;
                    //((Button) view.findViewById(questionnumber)).setBackgroundColor(Color.TRANSPARENT);
                    //Log.e("ID", Integer.toString(group.getCheckedRadioButtonId()));
                    if(group.getCheckedRadioButtonId()!=answers[questionnumber]+2){
                        group.check(answers[questionnumber]+2);
                    }
                }
                for (int i=0;i<answers.length;i++) {
                    Log.e("answer", String.valueOf(answers[i]));
                }
            }
        });

        for (int i = 2; i <= number+2; i++) {
            final RadioButton rdbtn = new RadioButton(getActivity());
            rdbtn.setText(questionchoices.get(questionnumber)[i-2]+" ");
            rdbtn.setTextSize(18);
            rdbtn.setId(i);

            Space space = new Space(getActivity());
            space.setMinimumHeight(20);
            rg.addView(space);
            rg.addView(rdbtn);

            if(answers[question]==i-2){
                if(!rdbtn.isChecked()) {
                    //Log.e("access","rdbtn");
                    rdbtn.setChecked(true);
                }
            } else{
                rdbtn.setChecked(false);
            }
            rdbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //Log.e("RBID", Integer.toString(compoundButton.getId()));
                    if (b){
                        for (int i=2;i<=numberRB+2;i++){
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
        Log.e("Z", String.valueOf(z));
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
            } else{
                questionFilled.add(questionParse.substring(arrCloseParen.get(i-1)+1, arrOpenParen.get(i)));
            }
        }
        questionFilled.add(questionParse.substring(arrCloseParen.get(total-1)+1,questionParse.length()));
        for (int i=0;i<total;i++){
            difference = posOfQuestion-i;
            if (difference==0){
                if (answers[z] != -1){
                    answersFilled.add(i,questionchoices.get(z)[answers[z]] );
                } else{
                    answersFilled.add(i,"(?)");
                }
            }
            else{
                if (answers[z-difference] != -1){
                    answersFilled.add(i,questionchoices.get(z-difference)[answers[z-difference]] );
                } else{
                    answersFilled.add(i,"()");
                }
            }
        }

        for (int i=0;i<answersFilled.size();i++) {
            if (!answersFilled.get(i).contains("(")){
                newQuestion = newQuestion.concat(questionFilled.get(i)).concat("(").concat(answersFilled.get(i)).concat(")");
            } else {
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
