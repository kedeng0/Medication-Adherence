package com.example.joelclarke.snakeforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Exit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        LinearLayout set = (LinearLayout) findViewById(R.id.activity_exit);
        SharedPreferences setting = getSharedPreferences("background", Context.MODE_PRIVATE);
        int bg = setting.getInt("background_resource", getResources().getColor(R.color.colorPrimary));
        set.setBackgroundColor(bg);

        Button yes_exit = (Button) findViewById(R.id.yes_exit);
        Button no_exit = (Button) findViewById(R.id.no_exit);

        yes_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
        no_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Exit.this, MainMenu.class));
            }
        });
    }
}
