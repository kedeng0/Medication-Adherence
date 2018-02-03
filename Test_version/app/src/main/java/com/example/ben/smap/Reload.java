package com.example.ben.smap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Reload extends AppCompatActivity {
    private TextView description_text;
    private String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload);
        description = "This page is for prescription reload.";
        description_text = (TextView) findViewById(R.id.reload_description);
        description_text.setText(description);
    }
}
