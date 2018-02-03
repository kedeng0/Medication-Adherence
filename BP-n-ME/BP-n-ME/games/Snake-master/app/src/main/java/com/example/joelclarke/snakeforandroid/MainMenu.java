package com.example.joelclarke.snakeforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        LinearLayout set = (LinearLayout) findViewById(R.id.activity_main_menu);
        SharedPreferences setting = getSharedPreferences("background", Context.MODE_PRIVATE);
        int bg = setting.getInt("background_resource", getResources().getColor(R.color.colorPrimary));
        set.setBackgroundColor(bg);

        Button newGame = (Button) findViewById(R.id.game_menu);
        Button highscores = (Button) findViewById(R.id.highscores_menu);
        Button achievements = (Button) findViewById(R.id.achievements_menu);
        Button settings = (Button) findViewById(R.id.settings_menu);
        Button exit = (Button) findViewById(R.id.exit_menu);
        Button how = (Button) findViewById(R.id.how_to_play);

        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainMenu.this, NewGame.class));
            }
        });

        highscores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, Highscores.class));
            }
        });

        achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, Achievements.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, Settings.class));
            }
        });
        how.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, HowToPlay.class));
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, Exit.class));
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(MainMenu.this, Exit.class));
    }
}


