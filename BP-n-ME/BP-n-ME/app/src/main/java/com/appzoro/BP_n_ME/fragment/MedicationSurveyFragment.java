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

public class MedicationSurveyFragment extends Fragment implements View.OnClickListener {
    View view;
    ImageView img_back;
    TextView submit,next,questionsView;
    ScrollView scrollView;
    RadioGroup rg;
    LinearLayout layout;
    Button q1,q2,q3,q4,q5,q6,q7,q8;
    EditText editText;
    String medName, UID;
    int toAnswer = 5;
    int selected =0;
    int questionnumber, qnum, i;
    Boolean five=true;
    ArrayList<String> questionsArray = new ArrayList<>();
    final ArrayList<String> answers = new ArrayList<>(Collections.nCopies(8, "-1"));
    ArrayList<Button> questionlist = new ArrayList<Button>();
    MedasolPrefs prefs;
    private DatabaseReference mDatabase ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_medicationsurvey, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        Bundle bundle = this.getArguments();
        medName = bundle.getString("medName");
        Log.e("medName ",medName);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();
        scrollView = view.findViewById(R.id.scrollView);
        img_back = view.findViewById(R.id.iv_back);
        submit = view.findViewById(R.id.tv_submit);
        next = view.findViewById(R.id.tv_next);
        q1 = view.findViewById(R.id.q1);
        q2 = view.findViewById(R.id.q2);
        q3 = view.findViewById(R.id.q3);
        q4 = view.findViewById(R.id.q4);
        q5 = view.findViewById(R.id.q5);
        q6 = view.findViewById(R.id.q6);
        q7 = view.findViewById(R.id.q7);
        q8 = view.findViewById(R.id.q8);
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

        questionsArray.add("I find it easy to take "+medName+" every day at the correct times.");
        questionsArray.add("The frequency at which I am supposed to take it is the biggest barrier to taking "+medName+" correctly.");
        questionsArray.add("The shape or taste of the pill is the biggest barrier to taking "+medName+" correctly.");
        questionsArray.add("The price of "+medName+" is the biggest barrier to taking it correctly.");
        questionsArray.add("I do not take "+medName+" according to the prescribed schedule because it does not work for me.");
        questionsArray.add("I do not take "+medName+" according to the prescribed schedule because it has an unpleasant side effect.");
        questionsArray.add("I do not take "+medName+" according to the prescribed schedule because I have trouble remembering to do so.");
        questionsArray.add("What factors make "+medName+" easy or difficult to take according to the prescribed schedule?");


        editText = new EditText(getActivity());
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setSingleLine(true);
        editText.setBackgroundResource(R.drawable.edittext_border);
        editText.setPaddingRelative(30,20,0,20);

        // set default que on question View
        i=0;
        if(!questionsView.getText().equals(Integer.toString(qnum + 1+i) + ".  " + questionsArray.get(qnum+i))){
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
                if (toAnswer == 8){
                    if (i < 7){
                        if (Integer.parseInt(answers.get(qnum+i)) != -1) {
                            rg.removeAllViews();
                            i++;
                            add(i);
                        } else {
                            Toast.makeText(getActivity(), "Please select the answer", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (toAnswer == 5){
                    if (i < 4){
                        if (Integer.parseInt(answers.get(qnum+i)) != -1) {
                            rg.removeAllViews();
                            i++;
                            add(i);
                        } else {
                            Toast.makeText(getActivity(), "Please select the answer", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.tv_submit:
                util.hideSOFTKEYBOARD(submit);
                int nonselected;
                selected = 0;
                if(toAnswer==5) {
                    nonselected = -3;
                }
                else{
                    nonselected = 0;
                }
                for (int x = 0; x < answers.size(); x++) {
                    try{
                        if (Integer.parseInt(answers.get(x)) != -1) {
                            selected++;
                        } else {
                            nonselected++;
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
                if(editText.getText().length()>0){
                    selected++;
                    Log.e("Edit Text",editText.getText().toString());
                    answers.set(7,editText.getText().toString());
                    nonselected--;
                }
                Log.e("Selected",Integer.toString(selected));
                Log.e("toAnswer",Integer.toString(toAnswer));
                if (selected == toAnswer) {
                    Snackbar.make(view, "Medication '"+ medName + "' Survey Successfully Completed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    System.out.println(dateFormat.format(cal.getTime()));
                    final String currentDate = dateFormat.format(cal.getTime());

                    mDatabase.child("app").child("users").child(UID).child(medName +"medadherenceanswers").child(currentDate).setValue(answers.toString());

                    getFragmentManager().beginTransaction().replace
                            (R.id.Fragment_frame_main_activity, new SurveysFragment()).commit();

                } else {
                    Snackbar.make(view, "Please complete all "+Integer.toString(toAnswer)+" questions. " + nonselected + " questions remain.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
        }
    }

    private void add(int i) {

        // Set Format for Questions
        if((qnum +i) == toAnswer-1){
            if((qnum+i)==4 & five == true) {
                questionsView.setText(Integer.toString(qnum + 1+i) + ".  " + questionsArray.get(qnum + 3+i));
            }
            else{
                questionsView.setText(Integer.toString(qnum + 1+i) + ".  " + questionsArray.get(qnum+i));
            }
            rg.removeAllViews();
            layout.removeAllViews();
            layout.addView(editText);
        }
        else{
            questionsView.setText(Integer.toString(qnum + 1+i) + ".  " + questionsArray.get(qnum+i));
            editText.clearFocus();
            layout.removeAllViews();
            rg.removeAllViews();
            addRadioButtons(qnum+i);

            if (Integer.parseInt(answers.get(qnum + i)) + 8 != rg.getCheckedRadioButtonId()) {
                rg.check(Integer.parseInt(answers.get(qnum + i)) + 8);
                //rg.check(answers[qnum+i]+8);
            }
        }
        if (i==toAnswer-1){
            submit.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
        }
        else {
            submit.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }
    }

    public void addRadioButtons(final int question) {
        String[] choices = {"Strongly agree", "Somewhat agree", "Do not agree or disagree", "Somewhat disagree", "Strongly disagree"};
        final int numberRB = 5;
        questionnumber = question;
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("checkedID>>>>>","ID "+Integer.toString(checkedId - 8));
                Log.e("toAnswer>>>>>","toAnswer " + toAnswer);
                if(toAnswer == 5 & checkedId == 4) {
                    answers.set(questionnumber, Integer.toString(checkedId - 8+3));
                }
                else{
                    answers.set(questionnumber, Integer.toString(checkedId - 8));
                }
                if (questionnumber == 0) {
                    if ((checkedId - 8) == 2 | (checkedId - 8 == 3) | (checkedId - 8) == 4) {
                        q6.setVisibility(View.VISIBLE);
                        q7.setVisibility(View.VISIBLE);
                        q8.setVisibility(View.VISIBLE);
                        Log.e("checkedID////",Integer.toString(checkedId - 8));
                        if(toAnswer != 8) {
                            toAnswer = 8;

                            layout.removeAllViews();
                        }
                    }
                    else{
                        if(toAnswer != 5) {
                            q6.setVisibility(View.GONE);
                            q7.setVisibility(View.GONE);
                            q8.setVisibility(View.GONE);
                            toAnswer = 5;
                            layout.removeAllViews();
                        }
                    }
                }
                rg.check(checkedId);
            }
        });
        for (int i = 8; i < 13; i++) {
            final RadioButton rdbtn = new RadioButton(getActivity());
            rdbtn.setText(choices[i - 8]);
            rdbtn.setTextSize(18);
            rdbtn.setId(i);

            rg.addView(rdbtn);
            if (i - 8 != 5) {
                Space space = new Space(getActivity());
                space.setMinimumHeight(20);
                rg.addView(space);
            }

            if (Integer.parseInt(answers.get(question)) == i - 8) {
                if(!rdbtn.isChecked()) {
                    rdbtn.setChecked(true);
                }
            }
            else{
                rdbtn.setChecked(false);
            }

            rdbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                   // Log.e("RBID", Integer.toString(compoundButton.getId()));
                    if (b) {
                        for (int i = 8; i < 13; i++) {
                            if (i != (int) compoundButton.getId()) {
                                //Log.e("Color" + i, "TRANS" + numberRB);
                                questionlist.get(questionnumber).setBackgroundResource(R.drawable.question_green);
                                questionlist.get(questionnumber).setTextColor(getResources().getColor(R.color.white));
                                ((RadioButton) view.findViewById(i)).setBackgroundColor(Color.TRANSPARENT);
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
