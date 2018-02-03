package com.appzoro.BP_n_ME.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class DemoSurvey extends AppCompatActivity implements View.OnClickListener {

    TextView questionsView, submit, next;
    ImageView img_Back;
    Button q1, q2, q3, q4, q5, q6, q7;
    ScrollView scrollView;
    RadioGroup rg;
    int qnum;
    int i;
    int[] naArray;
    int[] answers;
    int[] rightorwrong;
    String[] ailments = new String[7];
    int selected = 0;
    int number;
    MedasolPrefs prefs;
    Button button1;


    final ArrayList<String> questions = new ArrayList<>();
    String[] questionArray = new String[questions.size()];
    ArrayList<String[]> questionchoices = new ArrayList<>();
    ArrayList<String[]> key = new ArrayList<>();
    ArrayList<Button> questionlist = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_survey);
        getSupportActionBar().hide();
        init();
        Listener();
    }

    private void init() {
        prefs = new MedasolPrefs(getApplicationContext());
        img_Back = (ImageView) findViewById(R.id.iv_back);
        submit = (TextView) findViewById(R.id.tv_submit);
        next = (TextView) findViewById(R.id.tv_next);
        q1 = (Button) findViewById(R.id.q1);
        q2 = (Button) findViewById(R.id.q2);
        q3 = (Button) findViewById(R.id.q3);
        q4 = (Button) findViewById(R.id.q4);
        q5 = (Button) findViewById(R.id.q5);
        q6 = (Button) findViewById(R.id.q6);
        q7 = (Button) findViewById(R.id.q7);
        questionlist.add(q1);
        questionlist.add(q2);
        questionlist.add(q3);
        questionlist.add(q4);
        questionlist.add(q5);
        questionlist.add(q6);
        questionlist.add(q7);

        /*LinearLayout main_layer = (LinearLayout) findViewById(R.id.ll_demo);
        for (int i = 1; i < 8; i++) {
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            button1 = new Button(this);
            button1.setLayoutParams(params1);
            button1.setText("" + i);
            button1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            button1.setGravity(Gravity.CENTER);
            button1.setPadding(10, 10, 10, 10);
            button1.setBackground(getResources().getDrawable(R.drawable.question_button_border));

            params1.setMargins(10, 10, 0, 0);
            button1.setLayoutParams(params1);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(80, 80));

            layout.addView(button1);
            questionlist.add(button1);
            main_layer.addView(layout);
        }*/

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        questionsView = (TextView) findViewById(R.id.questionView);
        rg = (RadioGroup) findViewById(R.id.radiogroup);

        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.DemographicsSurvey);
        final ArrayList<String> questions = new ArrayList<>();

        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }

        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        //get key from xml file
        final String[] keyArray = getResources().getStringArray(R.array.DemographicsSurveyAnswers);
        for (String full : keyArray) {
            String[] split = full.split("_");
            key.add(Arrays.copyOfRange(split, 0, split.length));
        }

        //get notApplicable list from xml file
        String[] naArray1 = getResources().getStringArray(R.array.DemographicsSurveyAnswersNA);
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
        i = 0;
        if (!questionsView.getText().equals(Integer.toString(qnum + 1 + i) + ".  " + questionArray[qnum + i])) {
            rg.removeAllViews();
            add(i);
        }
    }

    private void Listener() {
        img_Back.setOnClickListener(this);
        submit.setOnClickListener(this);

        next.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
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
                        Toast.makeText(this, "Please select the answer", Toast.LENGTH_SHORT).show();
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
                    Snackbar.make(view, "Demo Survey Successfully Completed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Calendar now = Calendar.getInstance();
                    String year = Integer.toString(now.get(Calendar.YEAR));
                    String month = Integer.toString(now.get(Calendar.MONTH) + 1); // Note: zero based!
                    String day = Integer.toString(now.get(Calendar.DAY_OF_MONTH));
                    prefs.setMedicalConditions(ailments);
                    prefs.setDemographicsSurveyAnswers(answers);

                    Intent i = new Intent(DemoSurvey.this, AddMedicationActivity.class);
                    i.putExtra("answer", answers);
                    i.putExtra("ailments", ailments);
                    startActivity(i);
                } else {
                    Snackbar.make(view, "Please complete all 7 questions. " + nonselected + " questions remain.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.e("Answers", Arrays.toString(answers));
                }
                break;
        }
    }

    private void add(int i) {

        if (i >= 0 && i <= 6) {
            questionsView.setText(Integer.toString(qnum + 1 + i) + ".  " + questionArray[qnum + i]);
            number = questionchoices.get(qnum + i).length - 1;
            Log.e("Question Choices", Arrays.toString(questionchoices.get(qnum + i)));

            if ((qnum + i) != 3) {
                addRadioButtons(number, qnum + i);
                if (answers[qnum + i] + 8 != rg.getCheckedRadioButtonId()) {
                    //Log.e("access","radiogr");
                    rg.check(answers[qnum + i] + 8);
                }
            } else {
                addCheckBoxes(number, qnum + i);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checked) {
                        int numChecked = 0;
                        for (int y = 60; y < number + 60; y++) {

                            if (((CheckBox) findViewById(y)).isChecked()) {
                                ailments[y - 60] = questionchoices.get(qnum)[y - 60];
                                numChecked++;
                                Log.e("AILMENTS", Arrays.toString(ailments));
                            } else {
                                ailments[y - 60] = "";
                            }
                        }
                        if (numChecked > 0) {
                            answers[qnum] = 0;
                        } else {
                            answers[qnum] = -1;
                        }
                    }
                });
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

    public void addCheckBoxes(int number, int question) {
        //Log.e("AILMENTS",Arrays.toString(ailments));
        final int numberCB = number;
        final int questionnumber = question;

        for (int i = 60; i <= number + 60; i++) {
            final CheckBox cbx = new CheckBox(this);
            //rdbtn.setId((row * 2) + i);
            cbx.setText(questionchoices.get(questionnumber)[i - 60] + " ");
            int textColor = Color.parseColor("#A9A9A9");

            cbx.setTextSize(20);
            cbx.setId(i);
            rg.addView(cbx);
            if (ailments[i - 60] != null) {
                cbx.setChecked(true);
            }
            if (i - 60 != number) {
                Space space = new Space(this);
                space.setMinimumHeight(50);
                rg.addView(space);
            }
            cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int numChecked = 0;
                    for (int y = 60; y <= numberCB + 60; y++) {
                        if (((CheckBox) findViewById(y)).isChecked()) {
                            ailments[y - 60] = questionchoices.get(questionnumber)[y - 60];
                            numChecked++;
                            Log.e("AILMENTS", Arrays.toString(ailments));
                        } else {
                            ailments[y - 60] = null;
                        }
                    }
                    if (numChecked > 0) {
                        //compoundButton.setBackgroundColor(Color.parseColor("#70FFFF"));
                        answers[questionnumber] = 0;
                        questionlist.get(questionnumber).setBackgroundResource(R.drawable.question_green);
                        questionlist.get(questionnumber).setTextColor(getResources().getColor(R.color.white));
                        // ((Button)findViewById(questionnumber)).setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        //compoundButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        answers[questionnumber] = -1;
                        questionlist.get(questionnumber).setBackgroundResource(R.drawable.question_button_border);
                        questionlist.get(questionnumber).setTextColor(getResources().getColor(R.color.black));
                        //((Button)findViewById(questionnumber)).setBackgroundColor(Color.LTGRAY);
                    }
                }
            });
        }
    }

    public void addRadioButtons(int number, final int question) {
        //Log.e("Create","Buttons");
        final RadioGroup rg = ((RadioGroup) findViewById(R.id.radiogroup));

        final int numberRB = number;
        final int questionnumber = question;
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId > 7) {
                    answers[questionnumber] = checkedId - 8;
                    //((Button) findViewById(questionnumber)).setBackgroundColor(Color.TRANSPARENT);
                    Log.e("ID", Integer.toString(group.getCheckedRadioButtonId()));
                    if (group.getCheckedRadioButtonId() != answers[questionnumber] + 8) {
                        group.check(answers[questionnumber] + 8);
                    }
                }
                for (int i = 0; i < answers.length; i++) {
                    //Log.e("answer", String.valueOf(answers[i]));
                }
            }
        });

        for (int i = 8; i <= number + 8; i++) {
            final RadioButton rdbtn = new RadioButton(this);
            //rdbtn.setId((row * 2) + i);

            rdbtn.setText(questionchoices.get(questionnumber)[i - 8] + " ");
            int textColor = Color.parseColor("#A9A9A9");
            rdbtn.setTextSize(18);
            rdbtn.setId(i);

            Space space = new Space(this);
            space.setMinimumHeight(20);
            rg.addView(space);
            rg.addView(rdbtn);

            if (answers[question] == i - 8) {
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
                        for (int i = 8; i <= numberRB + 8; i++) {
                            if (i != (int) compoundButton.getId()) {
                                questionlist.get(questionnumber).setBackgroundResource(R.drawable.question_green);
                                questionlist.get(questionnumber).setTextColor(getResources().getColor(R.color.white));
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}
