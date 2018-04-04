package com.example.ben.test_version_2;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddMedicine extends AppCompatActivity {
    private List list;
    private ListView listView;
    EditText medicineType;
    TimePicker timePicker;
    TextView frequencyPicker;
    TextView frequencyResult;
    TextView amountPicker;
    TextView amountResult;

    private final static int TIME_PICKER_INTERVAL = 5;
    private final String TAG = this.getClass().getName();
    AlertDialog frequency_dialog;
    AlertDialog amount_dialog;
    String frequency;
    String amount;
    String hour;
    String min;
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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_time, null);
        dialogBuilder.setView(view);
        // Time picker setup
        timePicker = (TimePicker) findViewById(R.id.timePicker); // initiate a time picker
        timePicker.setIs24HourView(true);
        // set the current time as default value
        Calendar c = Calendar.getInstance();
        timePicker.setCurrentHour(c.get(Calendar.HOUR));
        timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        setTimePickerInterval(timePicker);

        // Configure displayed time
        if (((minute % TIME_PICKER_INTERVAL) != 0)) {
            int minuteFloor = (minute + TIME_PICKER_INTERVAL) - (minute % TIME_PICKER_INTERVAL);
            minute = minuteFloor + (minute == (minuteFloor + 1) ? TIME_PICKER_INTERVAL : 0);
            if (minute >= 60) {
                minute = minute % 60;
                hour++;
            }
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute / TIME_PICKER_INTERVAL);
        }

        dialogBuilder.setTitle("Time Picker");
        dialogBuilder.setMessage("Pick the dosing time");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                 = timePicker.getCurrentHour().toString();
                String min = timePicker.getCurrentMinute().toString();
                amountResult.setText(amount);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        amount_dialog = dialogBuilder.create();
        amountResult = (TextView) findViewById(R.id.amountResult);
        amountPicker = (TextView) findViewById(R.id.amountPicker_prompt);
        amountPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_dialog.show();
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

        //*****************************
        // Frequency dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an Option");
        final String[] options = { "Everyday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0: // Delete
                        frequency = options[item];
                        break;
                    case 1: // Copy
                        frequency = options[item];
                        break;
                    case 2: // Edit
                        frequency = options[item];
                        break;
                    case 3: // Delete
                        frequency = options[item];
                        break;
                    case 4: // Copy
                        frequency = options[item];
                        break;
                    case 5: // Edit
                        frequency = options[item];
                        break;
                    case 6: // Delete
                        frequency = options[item];
                        break;
                    case 7: // Copy
                        frequency = options[item];
                        break;
                    default:
                        break;
                }
                frequencyResult.setText(frequency);// Display result
            }
        });
        builder.setNegativeButton("Cancel", null);
        frequency_dialog = builder.create();
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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_amount, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.amountPicker);
        dialogBuilder.setTitle("Amount Picker");
        dialogBuilder.setMessage("Enter amount of pills");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                amount = edt.getText().toString();
                amountResult.setText(amount);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        amount_dialog = dialogBuilder.create();
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
                String hour = timePicker.getCurrentHour().toString();
                String min = timePicker.getCurrentMinute().toString();
                Intent i = new Intent();
                Bundle extras = new Bundle();
                extras.putString("EXTRA_HOUR",hour);
                extras.putString("EXTRA_MINUTE",min);
                extras.putString("EXTRA_MEDICINE",medicineType.getText().toString());
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
