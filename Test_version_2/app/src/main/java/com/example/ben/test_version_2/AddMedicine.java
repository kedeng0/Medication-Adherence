package com.example.ben.test_version_2;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddMedicine extends AppCompatActivity {
    private List list;
    private ListView listView;
    EditText medicineType;
    TimePicker timePicker;
    private final static int TIME_PICKER_INTERVAL = 5;
    private final String TAG = this.getClass().getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_add_medicine);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        medicineType = (EditText) findViewById(R.id.medicine_type);
        timePicker=(TimePicker)findViewById(R.id.timePicker); // initiate a time picker
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

        Button btn = (Button) findViewById(R.id.button);
        final TextView result = (TextView) findViewById(R.id.result);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String hour = timePicker.getCurrentHour().toString();
                String min = timePicker.getCurrentMinute().toString();
                result.setText(new StringBuilder().append(medicineType.getText()).append(", ").append(hour).append(" : ").append(min)
                        .append(" "));
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
