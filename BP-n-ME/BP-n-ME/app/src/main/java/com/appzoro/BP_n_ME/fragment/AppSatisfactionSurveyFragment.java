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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Appzoro_ 5 on 7/29/2017.
 */

public class AppSatisfactionSurveyFragment extends Fragment implements View.OnClickListener {
    View view;
    ImageView img_back;
    TextView submit, next, questionsView;
    ScrollView scrollView, scroll;
    RadioGroup rg;
    LinearLayout layout;
    Button q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15, q16, q17, q18, q19, q20, q21;
    EditText editText;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    int selected = 0;
    int prevClick = -1;
    int qnum;
    int i;
    ArrayList<String> questionsArray = new ArrayList<>();
    final ArrayList<String> answers = new ArrayList<>(Collections.nCopies(18, "-1"));
    ArrayList<Button> questionlist = new ArrayList<Button>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_appsatisfactionsurvey, container, false);
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
        /*q19 = view.findViewById(R.id.q19);
        q20 = view.findViewById(R.id.q20);
        q21 = view.findViewById(R.id.q21)*/;
        questionsView = view.findViewById(R.id.questionView);
        rg = view.findViewById(R.id.radiogroup);
        layout = view.findViewById(R.id.layout);

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
        /*questionlist.add(q19);
        questionlist.add(q20);
        questionlist.add(q21);*/

        //questionsArray.add("What is your Android device version? ");//make, model, and
        questionsArray.add("Overall, I find this application useful.");// mobile
        questionsArray.add("I find the medication reminders useful.");
        questionsArray.add("I find the blood pressure log useful.");
        //4  questionsArray.add("I find the body weight log useful.");////////////////////////////
        questionsArray.add("I find the medication utilization log useful.");
        questionsArray.add("I find the tips on how to stay healthy and adhere to my medication useful.");
        questionsArray.add("I find the call your pharmacist button useful.");
        questionsArray.add("How often do you use the feature: Blood Pressure Log");
        //9  questionsArray.add("How often do you use the feature: Body Weight Log");//////////////////
        questionsArray.add("How often do you use the feature: Medication Reminders");
        questionsArray.add("How often do you use the feature: Medication Utilization Log");
        questionsArray.add("How often do you use the feature: Call Your Pharmacist Feature");
        questionsArray.add("How would you rate the ease of the feature use: Blood Pressure Log");
        //14  questionsArray.add("How would you rate the ease of the feature use: Body Weight Log");///////////
        questionsArray.add("How would you rate the ease of the feature use: Medication Reminders");
        questionsArray.add("How would you rate the ease of the feature use: Medication Utilization Log");
        questionsArray.add("How would you rate the ease of the feature use: Call Your Pharmacist Feature");
        questionsArray.add("How likely are you to recommend this app to someone else?");
        questionsArray.add("What is your favorite feature of this application and why is it your favorite feature?");// mobile
        questionsArray.add("What is your least favorite feature of this application and why is it your least favorite feature?");// mobile
        questionsArray.add("What is your Android device version? ");//make, model, and

        editText = new EditText(getActivity());
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setSingleLine(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setBackgroundResource(R.drawable.edittext_border);
        editText.setPaddingRelative(30, 20, 0, 20);

        // set default que on question View
        i = 0;
        if (!questionsView.getText().equals(Integer.toString(qnum + 1 + i) + ".  " + questionsArray.get(qnum + i))) {
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
                util.hideSOFTKEYBOARD(img_back);
                getFragmentManager().popBackStack();
                break;
            case R.id.tv_next:
                util.hideSOFTKEYBOARD(next);
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_UP);
                    }
                });
                if (i== 15 | i==16 | i== 17){
                     if(!editText.getText().toString().equals("")){
                         questionlist.get(i).setBackgroundResource(R.drawable.question_green);
                         questionlist.get(i).setTextColor(getResources().getColor(R.color.white));
                     }
                }
                if (i < 17) {
                    if (Integer.parseInt(answers.get(qnum + i)) != -1 || editText.getText().length() > 0) {
                        rg.removeAllViews();
                        i++;
                        add(i);
                    } else {
                        Toast.makeText(getActivity(), "Please select the answer", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv_submit:
                util.hideSOFTKEYBOARD(submit);
                String UID = prefs.getUID();
                int nonselected;
                selected = 0;
                Log.e("ANSWERS", answers.toString());
                nonselected = 0;
                for (int x = 0; x < answers.size(); x++) {
                    try {
                        if (Integer.parseInt(answers.get(x)) != -1) {
                            selected++;
                        } else {
                            nonselected++;
                        }
                    } catch (Exception e) {
                        selected++;
                    }
                }
                if(editText.getText().length()>0){
                    selected++;
                    Log.e("Edit Text",editText.getText().toString());
                    answers.set(17,editText.getText().toString());
                    nonselected--;
                }
                Log.e("Selected", Integer.toString(selected));
                Log.e("toAnswer", Integer.toString(questionsArray.size()));
                if (selected == questionsArray.size()) {
                    Snackbar.make(view, "Mobile App Satisfaction Survey Successfully Completed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    System.out.println(dateFormat.format(cal.getTime()));
                    final String currentDate = dateFormat.format(cal.getTime());

                    Log.e("Mobil App Survey", "ans " + answers.toString());
                    mDatabase.child("app").child("users").child(UID).child("appsatisfactionanswers").child(currentDate).setValue(answers.toString());

                    getFragmentManager().beginTransaction().replace
                            (R.id.Fragment_frame_main_activity, new SurveysFragment()).commit();
                } else {
                    Snackbar.make(view, "Please complete all 18 questions. " + nonselected + " questions remain.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
        }
    }

    private void add(final int i) {
        if (prevClick != -1 & (prevClick == 15 | prevClick == 16 | prevClick == 17)) {
            Log.e("prev Click", Integer.toString(prevClick));
            if (editText.getText().length() > 0) {
                answers.set(prevClick, editText.getText().toString());
                editText.setText("");
            }
        }

        // Set Format for Questions
        if ((qnum + i) == 15 | (qnum + i) == 16 | (qnum + i) == 17) {
            try {
                if (Integer.parseInt(answers.get(qnum + i)) == -1) {
                    editText.setText("");
                }
            } catch (Exception e) {
                Log.e("Answer" + Integer.toString(qnum + i), answers.get(qnum + i));
                editText.setText(answers.get(qnum + i));

            }
            rg.removeAllViews();
            layout.removeAllViews();
            layout.addView(editText);

        } else {
            editText.clearFocus();
            rg.removeAllViews();
            layout.removeAllViews();
            addRadioButtons(qnum + i);

            if (Integer.parseInt(answers.get(qnum + i)) + 22 != rg.getCheckedRadioButtonId()) {
                Log.e("access", "radiogr");
                rg.check(Integer.parseInt(answers.get(qnum + i)) + 22);
                //rg.check(answers[qnum+i]+8);
            }
        }
        questionsView.setText(Integer.toString(qnum + 1 + i) + ".  " + questionsArray.get(qnum + i));
        // Check Previous Click
        prevClick = (qnum + i);

        if (i == 17) {
            submit.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
        } else {
            submit.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }
    }

    public void addRadioButtons(int question) {
        String[] choice = new String[5];
        String[] choices1 = {"Strongly agree", "Somewhat agree", "Do not agree or disagree", "Somewhat disagree", "Strongly disagree"};
        String[] choices2 = {"Not At All Likely", "Slightly Likely", "Moderately Likely", "Very Likely", "Completely"};
        String[] choices3 = {"Never", "Rarely", "Sometimes", "Often", "Always"};
        String[] choices4 = {"Very Easy", "Easy", "Moderate", "Difficult", "Very Difficult"};
        question++;
        if (question == 1 | question == 2 | question == 3 | question == 4 | question == 5 | question == 6 ) {
            for (int x = 0; x < choices1.length; x++) {
                choice[x] = choices1[x];
            }
        } else if (question == 7 | question == 8 | question == 9 | question == 10 ) {
            for (int x = 0; x < choices3.length; x++) {
                choice[x] = choices3[x];
            }
        } else if (question == 11 | question == 12 | question == 13 | question == 14 ) {
            for (int x = 0; x < choices4.length; x++) {
                choice[x] = choices4[x];
            }
        } else if (question == 15) {
            for (int x = 0; x < choices2.length; x++) {
                choice[x] = choices2[x];
            }
        }
        question--;
        final int numberRB = 5;
        final int questionnumber = question;
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId > 21) {
                    answers.set(questionnumber, Integer.toString(checkedId - 22));

                    if (group.getCheckedRadioButtonId() != (Integer.parseInt(answers.get(questionnumber))) + 22) {
                        group.check(Integer.parseInt(answers.get(questionnumber)) + 22);
                    }
                }
                for (int i=0;i<answers.size();i++) {
                    Log.e("answer", String.valueOf(answers.get(i)));
                }
            }
        });

        for (int i = 22; i < 27; i++) {
            final RadioButton rdbtn = new RadioButton(getActivity());
            rdbtn.setText(choice[i - 22]);
            rdbtn.setTextSize(18);
            rdbtn.setId(i);

            rg.addView(rdbtn);
            if (i - 22 != 5) {
                Space space = new Space(getActivity());
                space.setMinimumHeight(20);
                rg.addView(space);
            }

            if (Integer.parseInt(answers.get(question)) == i - 22) {
                //Log.e("Set", "Blue");
                if (!rdbtn.isChecked()) {
                    //Log.e("access","rdbtn");
                    rdbtn.setChecked(true);
                }
            } else {
                rdbtn.setChecked(false);
            }

            rdbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //Log.e("RBID", Integer.toString(compoundButton.getId()));
                    if (b) {
                        for (int i = 22; i < 27; i++) {
                            if (i != (int) compoundButton.getId()) {
                                //Log.e("Color" + i, "TRANS" + numberRB);
                                questionlist.get(questionnumber).setBackgroundResource(R.drawable.question_green);
                                questionlist.get(questionnumber).setTextColor(getResources().getColor(R.color.white));
                                ((RadioButton) view.findViewById(i)).setBackgroundColor(Color.TRANSPARENT);
                            } else {
                                if (!rdbtn.isChecked()) {
                                    rdbtn.setChecked(b);
                                }
                            }
                        }
                        //Log.e("Color" + compoundButton.getId(), "BLUE");
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }
}
