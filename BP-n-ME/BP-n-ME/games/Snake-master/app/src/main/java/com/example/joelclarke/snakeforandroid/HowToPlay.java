package com.example.joelclarke.snakeforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class HowToPlay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);
        LinearLayout set = (LinearLayout) findViewById(R.id.activity_how_to_play);
        SharedPreferences setting = getSharedPreferences("background", Context.MODE_PRIVATE);
        int bg = setting.getInt("background_resource", getResources().getColor(R.color.colorPrimary));
        set.setBackgroundColor(bg);
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(HowToPlay.this, MainMenu.class));
    }
}
