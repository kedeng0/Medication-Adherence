package com.example.ben.smap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {
    private Button repage;
    private Button status;
    private Button drop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        // Navigate to Reload Refill Page once clicked
        repage = findViewById(R.id.btn_repage);
        repage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(HomePage.this, RePage.class);
                startActivity(intent);
            }
        });
        // Navigate to Status Page once clicked
        status = findViewById(R.id.btn_status);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(HomePage.this, StatusPage.class);
                startActivity(intent);
            }
        });
        // Navigate to Drop Page once clicked
        drop = findViewById(R.id.btn_drop);
        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(HomePage.this, DropPage.class);
                startActivity(intent);
            }
        });
    }


}
