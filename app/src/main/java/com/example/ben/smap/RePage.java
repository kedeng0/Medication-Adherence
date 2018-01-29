package com.example.ben.smap;
// This sub page is for prescription reload.
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RePage extends AppCompatActivity {
    private Button reload;
    private Button refill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_page);
        reload = findViewById(R.id.button_reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(RePage.this, Reload.class);
                startActivity(intent);
            }
        });
        refill = findViewById(R.id.button_refill);
        refill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(RePage.this, Refill.class);
                startActivity(intent);
            }
        });
    }


}
