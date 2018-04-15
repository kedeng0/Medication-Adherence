package com.example.ben.test_version_2;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddMedicine extends AppCompatActivity {
    EditText medicineType;

    TextView frequencyPicker;
    TextView frequencyResult;
    TextView amountPicker;
    TextView amountResult;
    TextView timePrompt;
    TextView timeResult;



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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_add_medicine);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        medicineType = (EditText) findViewById(R.id.medicine_type);


        //*************************
        // Time picker
        timeResult = (TextView) findViewById(R.id.timeResult);
        timePrompt = (TextView) findViewById(R.id.time_prompt);
        // set the current time as default value
        Calendar c = Calendar.getInstance();
        current_hour = c.get(Calendar.HOUR_OF_DAY);
        current_minute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        final DurationTimePickDialog  timePickerDialog = new DurationTimePickDialog (this, new DurationTimePickDialog .OnTimeSetListener() {
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
        final AlertDialog.Builder freqBuilder = new AlertDialog.Builder(this);
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
        frequencyResult = (TextView) findViewById(R.id.frequencyResult);
        frequencyPicker = (TextView) findViewById(R.id.frequencyPicker_prompt);
        frequencyPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frequency_dialog.show();
            }
        });



        //*****************************
        // Amount dialog
        final AlertDialog.Builder amountBuilder = new AlertDialog.Builder(this);
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
        amountResult = (TextView) findViewById(R.id.amountResult);
        amountPicker = (TextView) findViewById(R.id.amountPicker_prompt);
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
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.action_save:
                Intent i = new Intent();
                Bundle extras = new Bundle();
                extras.putString("EXTRA_NAME",medicineType.getText().toString());
                extras.putInt("EXTRA_HOUR",hour);
                extras.putInt("EXTRA_MINUTE",minute);
                extras.putInt("EXTRA_AMOUNT",amount);
                i.putExtras(extras);
                setResult(RESULT_OK, i);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_save_item, menu);
        return true;
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
}
