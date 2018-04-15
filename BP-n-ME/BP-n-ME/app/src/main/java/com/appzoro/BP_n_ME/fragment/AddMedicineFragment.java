package com.appzoro.BP_n_ME.fragment;


import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.fragment.ScheduleFragment;
import com.appzoro.BP_n_ME.util.DurationTimePickDialog;
import com.appzoro.BP_n_ME.util.util;

public class AddMedicineFragment extends Fragment {
    EditText medicineType;

    TextView frequencyPicker;
    TextView frequencyResult;
    TextView amountPicker;
    TextView amountResult;
    TextView timePrompt;
    TextView timeResult;
    View view;


    private final static int TIME_PICKER_INTERVAL = 5;
    private final String TAG = this.getClass().getName();
    AlertDialog frequency_dialog;
    AlertDialog amount_dialog;
    AlertDialog time_dialog;
    int frequency;
    int amount;
    int current_hour;
    int current_minute;
    int hour;
    int minute;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_addmedicine, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getActivity().setTitle("Add Medicine");
        setHasOptionsMenu(true);
        init();
        return view;

    }

    private void init() {

        //Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar_add_medicine);
        //((MainActivity)getActivity()).getSupportActionBar().hide();
        //((MainActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        medicineType = (EditText) view.findViewById(R.id.medicine_type);


        //*************************
        // Time picker
        timeResult = (TextView) view.findViewById(R.id.timeResult);
        timePrompt = (TextView) view.findViewById(R.id.time_prompt);
        // set the current time as default value
        Calendar c = Calendar.getInstance();
        current_hour = c.get(Calendar.HOUR_OF_DAY);
        current_minute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        final DurationTimePickDialog timePickerDialog = new DurationTimePickDialog(getActivity(), new DurationTimePickDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
                hour = hourOfDay;
                minute = minuteOfDay;
                timeResult.setText(hour + ":" + minute);
            }
        }, current_hour, current_minute, true, 5);
        timePickerDialog.setTitle("");
        timePickerDialog.setMessage("Pick the dosing time");
        timePrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

//        // Test only start
//        Button btn = (Button) findViewById(R.id.button);
//        final TextView result = (TextView) findViewById(R.id.result);
//        btn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                String hour = timePicker.getCurrentHour().toString();
//                String min = timePicker.getCurrentMinute().toString();
//                result.setText(new StringBuilder().append(medicineType.getText()).append(", ").append(hour).append(" : ").append(min)
//                        .append(" "));
//            }
//        });//Test only end


        //***************************
        // Freq picker
        final AlertDialog.Builder freqBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater_freq = this.getLayoutInflater();
        final View dialogView_freq = inflater_freq.inflate(R.layout.freq_amount, null);
        freqBuilder.setView(dialogView_freq);
        final EditText input_freq = (EditText) dialogView_freq.findViewById(R.id.freqPicker);
        freqBuilder.setTitle("Frequency Picker");
        freqBuilder.setMessage("Enter times of dosing");
        freqBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                frequency = Integer.parseInt(input_freq.getText().toString());
                frequencyResult.setText(input_freq.getText().toString());
            }
        });
        freqBuilder.setNegativeButton("Cancel", null);
        frequency_dialog = freqBuilder.create();
        frequencyResult = (TextView) view.findViewById(R.id.frequencyResult);
        frequencyPicker = (TextView) view.findViewById(R.id.frequencyPicker_prompt);
        frequencyPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frequency_dialog.show();
            }
        });


        //*****************************
        // Amount dialog
        final AlertDialog.Builder amountBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater_amount = this.getLayoutInflater();
        final View dialogView_amount = inflater_amount.inflate(R.layout.dialog_amount, null);
        amountBuilder.setView(dialogView_amount);
        final EditText input_amount = (EditText) dialogView_amount.findViewById(R.id.amountPicker);
        amountBuilder.setTitle("Amount Picker");
        amountBuilder.setMessage("Enter amount of pills");
        amountBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                amount = Integer.parseInt(input_amount.getText().toString());
                amountResult.setText(input_amount.getText().toString());
            }
        });
        amountBuilder.setNegativeButton("Cancel", null);
        amount_dialog = amountBuilder.create();
        amountResult = (TextView) view.findViewById(R.id.amountResult);
        amountPicker = (TextView) view.findViewById(R.id.amountPicker_prompt);
        amountPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_dialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle presses on the action bar items
        ScheduleFragment fragment = new ScheduleFragment();
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                getFragmentManager().beginTransaction().replace
//                        (R.id.Fragment_frame_main_activity, new ScheduleFragment()).commit();

//                return true;
                getFragmentManager().popBackStack();
                break;
            case R.id.action_save:
                Bundle extras = new Bundle();
                extras.putString("EXTRA_NAME",medicineType.getText().toString());
                extras.putInt("EXTRA_HOUR",hour);
                extras.putInt("EXTRA_MINUTE",minute);
                extras.putInt("EXTRA_AMOUNT",amount);
                fragment.setArguments(extras);
//                getFragmentManager().beginTransaction().replace
//                        (R.id.Fragment_frame_main_activity, new ScheduleFragment()).addToBackStack("AddMedicineFragment").commit();
                getFragmentManager().popBackStack();
                util.hideSOFTKEYBOARD(view);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_save_item, menu);
    }
    /**
     * Set TimePicker interval by adding a custom minutes list
     *
     * @param timePicker
     */
    private void setTimePickerInterval(TimePicker timePicker) {
        try {
            NumberPicker minutePicker = (NumberPicker) timePicker.findViewById(getResources().getSystem().getIdentifier(
                    "minute", "id", "android"));
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        }
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        ((MainActivity)getActivity()).getSupportActionBar().hide();
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        ((MainActivity)getActivity()).getSupportActionBar().show();
//    }

}
