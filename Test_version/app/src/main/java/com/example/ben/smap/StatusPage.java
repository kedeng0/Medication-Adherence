package com.example.ben.smap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class StatusPage extends AppCompatActivity {
    private TextView text_log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_page);
        text_log = (TextView) findViewById(R.id.textView_log);
        text_log.setText("This section shows the case log.");
    }
}
