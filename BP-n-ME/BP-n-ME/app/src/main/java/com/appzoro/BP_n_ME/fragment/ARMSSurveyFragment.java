package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Appzoro_ 5 on 12/12/2017.
 */

public class ARMSSurveyFragment extends Fragment implements View.OnClickListener {
    View view;
    ImageView img_back;
    TextView submit, next, questionsView;
    Button q1, q2, q3, q4, q5, q6, q7;
    ScrollView scrollView;
    RadioGroup rg;
    Button button1;
    final ArrayList<String> questions = new ArrayList<>();
    String[] questionArray = new String[questions.size()];
    ArrayList<String[]> questionchoices = new ArrayList<>();
    ArrayList<String[]> key = new ArrayList<>();
    ArrayList<Button> questionlist = new ArrayList<Button>();
    int[] naArray;
    int[] answers;
    int[] rightorwrong;
    int selected = 0;
    int number, questionnumber, qnum, i;
    String UID;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_armssurvey, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        LinearLayout main_layer = view.findViewById(R.id.ll_questionNo);
        questionsView = view.findViewById(R.id.questionView);
        rg = view.findViewById(R.id.radiogroup);

        questionlist.add(q1);
        questionlist.add(q2);
        questionlist.add(q3);
        questionlist.add(q4);
        questionlist.add(q5);
        questionlist.add(q6);
        questionlist.add(q7);


        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.ArmsSurvey);
        final ArrayList<String> questions = new ArrayList<>();
        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }
        System.out.println(mArray.toString());
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        /*//get key from xml file
        final String[] keyArray = getResources().getStringArray(R.array.LifestyleSurveyAnswers);
        for (String full : keyArray) {
            String[] split = full.split("_");
            key.add(Arrays.copyOfRange(split, 0, split.length));
        }

        //get notApplicable list from xml file
        String[] naArray1 = getResources().getStringArray(R.array.LifestyleSurveyAnswersNA);
        naArray = new int[naArray1.length];
        int counter = 0;
        for (String full : naArray1) {
            naArray[counter] = Integer.parseInt(full);
            counter++;
        }*/

        //initialize results arrays
        answers = new int[mArray.length];
        Arrays.fill(answers, -1);          //default for unanswered
        rightorwrong = new int[mArray.length];
        Arrays.fill(rightorwrong, 1);      //default for wrong

        // set default que on question View
        i = 0;
        if (!questionsView.getText().equals(Integer.toString(qnum + 1 + i) + ".  " + questionArray[qnum + i])) {
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
                if (i < 6) {
                    if (answers[qnum + i] != -1) {
                        rg.removeAllViews();
                        i++;
                        add(i);
                    } else {
                        Toast.makeText(getActivity(), "Please select the answer", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv_submit:
                selected = 0;
                int nonselected = 0;
                for (int x : answers) {
                    if (x != -1) {
                        selected++;
                    } else {
                        nonselected++;
                    }
                }
                if (selected > 6) {
                    Snackbar.make(view, "ARMS-7 Survey Successfully Completed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    System.out.println(dateFormat.format(cal.getTime()));
                    final String currentDate = dateFormat.format(cal.getTime());

                    mDatabase.child("app").child("users").child(UID).child("arms7surveyanswers").child(currentDate).setValue(Arrays.toString(answers));

                    prefs.setArmsSurveyAnswers(answers);
                    prefs.setArmsDate(currentDate);

                    ARMSFeedbackFragment fragment = new ARMSFeedbackFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("date", currentDate);
                    bundle.putIntArray("answers", answers);
                    Log.e("date", currentDate);
                    fragment.setArguments(bundle);

                    getFragmentManager().beginTransaction().replace
                            (R.id.Fragment_frame_main_activity, fragment).commit();
                } else {
                    Snackbar.make(view, "Please complete all 7 questions. " + nonselected + " questions remain.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
        }
    }

    public void add(int i) {
        if (i >= 0 && i <= 6) {
            questionsView.setText(Integer.toString(qnum + 1 + i) + ".  " + questionArray[qnum + i]);
            number = questionchoices.get(qnum + i).length - 1;
            addRadioButtons(number, qnum + i);

            if (answers[qnum + i] + 7 != rg.getCheckedRadioButtonId()) {
                //Log.e("access","radiogr");
                rg.check(answers[qnum + i] + 7);
            }
        }

        if (i == 6) {
            submit.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
        } else {
            submit.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }
    }

    public void addRadioButtons(int number, final int question) {
        final int numberRB = number;
        questionnumber = question;
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId > 6) {
                    answers[questionnumber] = checkedId - 7;
                    if (group.getCheckedRadioButtonId() != answers[questionnumber] + 7) {
                        group.check(answers[questionnumber] + 7);
                    }
                }
                for (int i = 0; i < answers.length; i++) {
                    Log.e("answer", String.valueOf(answers[i]));
                }
            }
        });

        for (int i = 7; i <= number + 7; i++) {
            final RadioButton rdbtn = new RadioButton(getActivity());
            rdbtn.setText(questionchoices.get(questionnumber)[i - 7] + " ");
            rdbtn.setTextSize(18);
            rdbtn.setId(i);

            Space space = new Space(getActivity());
            space.setMinimumHeight(20);
            rg.addView(space);
            rg.addView(rdbtn);

            if (answers[question] == i - 7) {
                if (!rdbtn.isChecked()) {
                    rdbtn.setChecked(true);
                }
            } else {
                rdbtn.setChecked(false);
            }
            rdbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (b) {
                        for (int i = 7; i <= numberRB + 7; i++) {
                            if (i != (int) compoundButton.getId()) {
                                questionlist.get(questionnumber).setBackgroundResource(R.drawable.question_green);
                                questionlist.get(questionnumber).setTextColor(getResources().getColor(R.color.white));
                                ((RadioButton) view.findViewById(i)).setBackgroundColor(Color.TRANSPARENT);
                            } else {
                                if (!rdbtn.isChecked()) {
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
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }
}
