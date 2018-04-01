package com.example.ben.smap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {
    private Button repage;
    private Button ble;
    private Button dispense;

    private BleManager mBleManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mBleManager = BleManager.getInstance(this);

        repage = findViewById(R.id.button_repage);
        repage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(HomePage.this, RePage.class);
                startActivity(intent);
            }
        });
        ble = findViewById(R.id.button_ble);
        ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(HomePage.this, Ble.class);
                startActivity(intent);
            }
        });
        dispense = findViewById(R.id.button_dispense);
        dispense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                mBleManager.dispense();
            }
        });


    }


}
