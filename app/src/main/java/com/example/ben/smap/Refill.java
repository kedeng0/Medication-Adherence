package com.example.ben.smap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Refill extends AppCompatActivity {
    private TextView description_text;
    private String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refill);
        description = "This page is for pill refill.";
        description_text = (TextView) findViewById(R.id.refill_description);
        description_text.setText(description);
    }
}
