package com.example.joelclarke.snakeforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Settings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button green = (Button) findViewById(R.id.green_back);
        final Button red = (Button) findViewById(R.id.red_back);
        final Button blue = (Button) findViewById(R.id.blue_back);
        final Button yellow = (Button) findViewById(R.id.yellow_back);
        Button easy = (Button) findViewById(R.id.easy);
        Button medium = (Button) findViewById(R.id.medium);
        Button hard = (Button) findViewById(R.id.hard);
        Button clearHigh = (Button) findViewById(R.id.clear_scores);
        Button clearAch = (Button) findViewById(R.id.clear_achievements);

        //get current difficulty setting
        final SharedPreferences difficultyLevel = getSharedPreferences("difficulty", Context.MODE_PRIVATE);
        int d = difficultyLevel.getInt("speed", 2);
        //set the backgrounds to show currently selected
        switch(d){
            case 1:
                easy.setBackgroundResource(R.drawable.game_over_buttons);
                break;
            case 2:
                medium.setBackgroundResource(R.drawable.game_over_buttons);
                break;
            case 3:
                hard.setBackgroundResource(R.drawable.game_over_buttons);
                break;
        }

        //clear high scores
        clearHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences scores = getSharedPreferences("scores", 0);
                SharedPreferences.Editor edit = scores.edit();
                edit.clear();
                edit.apply();
                Toasty("Highscores removed");
            }
        });

        //clear achievements
        clearAch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences ach = getSharedPreferences("achievements", 0);
                SharedPreferences.Editor edit = ach.edit();
                edit.clear();
                edit.apply();
                Toasty("Achievements removed");
            }
        });

        //change difficulty depending on what the user selects
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = difficultyLevel.edit();
                editor.putInt("speed",1);
                editor.apply();
                reload();
            }
        });
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = difficultyLevel.edit();
                editor.putInt("speed",2);
                editor.apply();
                reload();
            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = difficultyLevel.edit();
                editor.putInt("speed",3);
                editor.apply();
                reload();
            }
        });

        //set the current background
        final SharedPreferences settings = getSharedPreferences("background", Context.MODE_PRIVATE);
        LinearLayout set = (LinearLayout) findViewById(R.id.activity_settings);
        int bg = settings.getInt("background_resource", getResources().getColor(R.color.colorPrimary));
        set.setBackgroundColor(bg);

        //select green background
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("background_resource", getResources().getColor(R.color.colorPrimary));
                editor.apply();
                reload();

            }
        });
        //select red background
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("background_resource", getResources().getColor(R.color.red_background));
                editor.apply();
                reload();
            }
        });
        //select blue background
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("background_resource", getResources().getColor(R.color.blue_background));
                editor.apply();
                reload();
            }
        });
        //select yellow background
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("background_resource", getResources().getColor(R.color.yellow_background));
                editor.apply();
                reload();
            }
        });


        //voice commands toggle button
        SharedPreferences voiceCommands = getSharedPreferences("voiceCommands", 0);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.voice_toggle);
        //set toggle buttons to current state
        boolean voice = voiceCommands.getBoolean("toggle", false);
        if (voice)
        {
            toggle.setChecked(true);
        }
        else
        {
            toggle.setChecked(false);
        }

        //change voice commands on or off with toggle button
        final SharedPreferences.Editor voiceEdit = voiceCommands.edit();
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    voiceEdit.putBoolean("toggle", true);
                    voiceEdit.apply();
                }
                else
                {
                    voiceEdit.putBoolean("toggle", false);
                    voiceEdit.apply();
                }
            }
        });
    }

    //reload the page with the new background
    public void reload()
    {
        this.recreate();
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(Settings.this, MainMenu.class));
    }

    public void Toasty(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
