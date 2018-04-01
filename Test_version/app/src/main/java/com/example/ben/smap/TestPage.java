package com.example.ben.smap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestPage extends AppCompatActivity {
    private Button ble;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);
        ble = findViewById(R.id.btn_ble);
        ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Start your second activity
            Intent intent = new Intent(TestPage.this, Ble.class);
            startActivity(intent);
            }
        });
    }
}
