package com.example.joelclarke.snakeforandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Highscores extends AppCompatActivity {
    //the different names that are in the top 10 of the table
    String name1, name2, name3, name4, name5, name6, name7, name8, name9, name10;

    //the scores to go with the names.
    int score1, score2, score3, score4, score5, score6, score7, score8, score9, score10;

    //new details to add to the score sheet
    int newScore;
    String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        //get current background colour
        LinearLayout set = (LinearLayout) findViewById(R.id.activity_highscores);
        SharedPreferences setting = getSharedPreferences("background", Context.MODE_PRIVATE);
        int bg = setting.getInt("background_resource", getResources().getColor(R.color.colorPrimary));
        set.setBackgroundColor(bg);

        //get new highscore if any.
        SharedPreferences score = getSharedPreferences("scores", Context.MODE_PRIVATE);
        newScore = score.getInt("newScore", 0);


        //load old scores
        score1 = score.getInt("score1", 0);
        score2 = score.getInt("score2", 0);
        score3 = score.getInt("score3", 0);
        score4 = score.getInt("score4", 0);
        score5 = score.getInt("score5", 0);
        score6 = score.getInt("score6", 0);
        score7 = score.getInt("score7", 0);
        score8 = score.getInt("score8", 0);
        score9 = score.getInt("score9", 0);
        score10 = score.getInt("score10", 0);

        //load the old names
        name1 = score.getString("name1", "");
        name2 = score.getString("name2", "");
        name3 = score.getString("name3", "");
        name4 = score.getString("name4", "");
        name5 = score.getString("name5", "");
        name6 = score.getString("name6", "");
        name7 = score.getString("name7", "");
        name8 = score.getString("name8", "");
        name9 = score.getString("name9", "");
        name10 = score.getString("name10", "");






        //get the name textviews...
        TextView nm1 = (TextView) findViewById(R.id.nm_row_1);
        TextView nm2 = (TextView) findViewById(R.id.nm_row_2);
        TextView nm3 = (TextView) findViewById(R.id.nm_row_3);
        TextView nm4 = (TextView) findViewById(R.id.nm_row_4);
        TextView nm5 = (TextView) findViewById(R.id.nm_row_5);
        TextView nm6 = (TextView) findViewById(R.id.nm_row_6);
        TextView nm7 = (TextView) findViewById(R.id.nm_row_7);
        TextView nm8 = (TextView) findViewById(R.id.nm_row_8);
        TextView nm9 = (TextView) findViewById(R.id.nm_row_9);
        TextView nm10 = (TextView) findViewById(R.id.nm_row_10);

        //get the score textviews...
        TextView sc1 = (TextView) findViewById(R.id.sc_row_1);
        TextView sc2 = (TextView) findViewById(R.id.sc_row_2);
        TextView sc3 = (TextView) findViewById(R.id.sc_row_3);
        TextView sc4 = (TextView) findViewById(R.id.sc_row_4);
        TextView sc5 = (TextView) findViewById(R.id.sc_row_5);
        TextView sc6 = (TextView) findViewById(R.id.sc_row_6);
        TextView sc7 = (TextView) findViewById(R.id.sc_row_7);
        TextView sc8 = (TextView) findViewById(R.id.sc_row_8);
        TextView sc9 = (TextView) findViewById(R.id.sc_row_9);
        TextView sc10 = (TextView) findViewById(R.id.sc_row_10);


        //fill in the table
        //1
        nm1.setText(name1);
        sc1.setText(String.valueOf(score1));
        //2
        nm2.setText(name2);
        sc2.setText(String.valueOf(score2));
        //3
        nm3.setText(name3);
        sc3.setText(String.valueOf(score3));
        //4
        nm4.setText(name4);
        sc4.setText(String.valueOf(score4));
        //5
        nm5.setText(name5);
        sc5.setText(String.valueOf(score5));
        //6
        nm6.setText(name6);
        sc6.setText(String.valueOf(score6));
        //7
        nm7.setText(name7);
        sc7.setText(String.valueOf(score7));
        //8
        nm8.setText(name8);
        sc8.setText(String.valueOf(score8));
        //9
        nm9.setText(name9);
        sc9.setText(String.valueOf(score9));
        //10
        nm10.setText(name10);
        sc10.setText(String.valueOf(score10));




        //check for achievements
        achievementChecker();
    }

    public void achievementChecker()
    {
        SharedPreferences achievement = getSharedPreferences("achievements", 0);
        SharedPreferences.Editor editor = achievement.edit();
        SharedPreferences scores = getSharedPreferences("scores", 0);
        int score = scores.getInt("score10", 0);

        //onTheScoreboard
        if (score != 0)
        {
            editor.putInt("On The Scoreboard", 1);
            editor.apply();
        }


    }

}
