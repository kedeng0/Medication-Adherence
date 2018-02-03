package com.appzoro.BP_n_ME.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.adapter.mListAdapter;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.util;

import java.util.ArrayList;

public class AddMedicationActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ImageView img_Back;
    TextView addMedication, submit;
    EditText medicationName;
    Spinner bpSpinner,freqSpinner;
    ListView mListView;
    String frequency = "";
    String bloodPressureGoal = "";
    ArrayList<String> mList = new ArrayList<>();
    ArrayList<String> fList = new ArrayList<>();
    MedasolPrefs prefs;
    LinearLayout layout;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);
        getSupportActionBar().hide();
        init();
        Listener();
    }

    private void init() {
        prefs = new MedasolPrefs(getApplicationContext());

        img_Back = (ImageView) findViewById(R.id.iv_back);
        bpSpinner = (Spinner) findViewById(R.id.bpspinner);
        freqSpinner = (Spinner) findViewById(R.id.frequency);
        medicationName = (EditText) findViewById(R.id.medicationName);
        addMedication = (TextView) findViewById(R.id.tv_addMedicaion);
        submit = (TextView) findViewById(R.id.tv_submit);
        mListView = (ListView) findViewById(R.id.mList);

        //freqSpinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(adapter);

        //bpSpinner
        adapter = ArrayAdapter.createFromResource(this,
                R.array.bloodPressureGoal, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bpSpinner.setAdapter(adapter);

        initializeMedList();
    }

    private void Listener() {
        img_Back.setOnClickListener(this);
        addMedication.setOnClickListener(this);
        submit.setOnClickListener(this);
        freqSpinner.setOnItemSelectedListener(this);
        bpSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                util.hideSOFTKEYBOARD(img_Back);
                finish();
                break;
            case R.id.tv_addMedicaion:
                util.hideSOFTKEYBOARD(addMedication);
                if(medicationName.getText().toString().equals("")){
                    Toast.makeText(this, "Please Enter Medication Name!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String meds = medicationName.getText().toString().trim();
                    if (meds.contains(".") | meds.contains("/") | meds.contains("[") | meds.contains("]") | meds.contains("#") | meds.contains("$")){
                        Toast.makeText(this, "Please Enter Valid Information, can't accept '.$#[]/'!", Toast.LENGTH_LONG).show();
                    } else {
                        mList.add(medicationName.getText().toString().trim());
                        fList.add(frequency);
                        medicationName.getText().clear();
                        initializeMedList();
                    }
                }
                break;
            case R.id.tv_submit:
                util.hideSOFTKEYBOARD(submit);
                if (mList.size() == 0) {
                    Snackbar.make(view, "Please enter at least one medication!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Log.d("submitButton", "null fields");
                } else {
                    prefs.setBloodPressureGoal(bloodPressureGoal);

                    //add medication in prefs
                    prefs.setMeds(mList);
                    Log.e("medicationList size", String.valueOf(mList.size()));

                    //add frequency in prefs
                    prefs.setFrequency(fList);
                    Log.e("FrequencyList size", String.valueOf(fList.size()));

                    Intent i = new Intent(AddMedicationActivity.this, DatabaseActivity.class);
                    startActivity(i);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        switch (parent.getId()) {
            case R.id.frequency:
                frequency = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.bpspinner:
                bloodPressureGoal = parent.getItemAtPosition(pos).toString();
                break;
        }
        System.out.println(frequency + " " + bloodPressureGoal);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void initializeMedList() {
        String[] mArray = new String[mList.size()];
        mArray = mList.toArray(mArray);

        mListAdapter medsAdapter = new mListAdapter(this,mArray);
        mListView.setAdapter(medsAdapter);

        final String[] finalMArray = mArray;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int item, long id) {
                long viewId = view.getId();
                if (viewId == R.id.iv_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Delete Medication");

                    // set dialog message
                    builder
                            .setMessage("Would you like to delete " + finalMArray[item] + "?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                   //Log.e("finalMArray", finalMArray[item]);
                                    mList.remove(item);
                                    fList.remove(item);
                                    initializeMedList();
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    initializeMedList();
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                if (viewId == R.id.iv_freq) {
                    //Spinner
                    layout = new LinearLayout(view.getContext());
                    //Setup Layout Attributes
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layout.setLayoutParams(params);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    Spinner spinner = new Spinner(view.getContext());
                    String[] string_list = getResources().getStringArray(R.array.frequency);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, string_list);
                    spinner.setAdapter(adapter);

                    if (fList.get(item).equals("Daily")){
                        spinner.setSelection(0);
                    } else if (fList.get(item).equals("Twice daily")){
                        spinner.setSelection(1);
                    } else if (fList.get(item).equals("Three times daily")){
                        spinner.setSelection(2);
                    } else {
                        spinner.setSelection(3);
                    }

                    spinner.setGravity(Gravity.CENTER);
                    spinner.setPadding(20, 8, 12, 8);
                    spinner.setBackgroundResource(R.drawable.spinner_border);
                    spinner.setPopupBackgroundResource(R.color.white);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    param.setMargins(75, 20, 75, 0);//pass int values for left,top,right,bottom
                    spinner.setLayoutParams(param);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

                            frequency = parent.getItemAtPosition(pos).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    //   layout.addView(spinner);
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Change Frequency");
                    builder.setView(spinner);
                    layout.addView(spinner);
                    builder.setView(layout);
                    // set dialog message
                    builder
                            .setMessage("Would you like to change frequency " + finalMArray[item] + " from "+ fList.get(item)+ "?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    fList.remove(item);
                                    fList.add(item,frequency);
                                    dialog.cancel();
                                    initializeMedList();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
