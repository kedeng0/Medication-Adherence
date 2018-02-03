package com.example.joelclarke.snakeforandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Achievements extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        //set background
        LinearLayout set = (LinearLayout) findViewById(R.id.activity_achievements);
        SharedPreferences setting = getSharedPreferences("background", Context.MODE_PRIVATE);
        int bg = setting.getInt("background_resource", getResources().getColor(R.color.colorPrimary));
        set.setBackgroundColor(bg);

        //add the title TextViews for the achievements
        TextView NewGamer = ( TextView ) findViewById(R.id.new_gamer_title);
        TextView OnTheScoreboard = ( TextView ) findViewById(R.id.on_the_scoreboard_title);
        TextView MileHighClub = ( TextView ) findViewById(R.id.mile_high_club_title);
        TextView Challenger = ( TextView ) findViewById(R.id.challenger_title);
        TextView Oooops = ( TextView ) findViewById(R.id.oooops_title);
        TextView HowGrand = ( TextView ) findViewById(R.id.how_grand_title);
        TextView AppleOfEden = ( TextView ) findViewById(R.id.apple_of_eden_title);
        TextView HungrySnake = ( TextView ) findViewById(R.id.hungry_snake_title);
        TextView SnackAttack = ( TextView ) findViewById(R.id.snack_attack_title);
        TextView ThreeCourseMeal = ( TextView ) findViewById(R.id.three_course_meal_title);

        //now add the explanations...
        TextView NewGamer1 = ( TextView ) findViewById(R.id.new_gamer_exp);
        TextView OnTheScoreboard1 = ( TextView ) findViewById(R.id.on_the_scoreboard_exp);
        TextView MileHighClub1 = ( TextView ) findViewById(R.id.mile_high_club_exp);
        TextView Challenger1 = ( TextView ) findViewById(R.id.challenger_exp);
        TextView Oooops1 = ( TextView ) findViewById(R.id.oooops_exp);
        TextView HowGrand1 = ( TextView ) findViewById(R.id.how_grand_exp);
        TextView AppleOfEden1 = ( TextView ) findViewById(R.id.apple_of_eden_exp);
        TextView HungrySnake1 = ( TextView ) findViewById(R.id.hungry_snake_exp);
        TextView SnackAttack1 = ( TextView ) findViewById(R.id.snack_attack_exp);
        TextView ThreeCourseMeal1 = ( TextView ) findViewById(R.id.three_course_meal_exp);

        //get the achievement shared preferences
        SharedPreferences achievement = getSharedPreferences("achievements", 0);

        //check if they've been achieved & change the colour...

        //new gamer
        if (achievement.getInt("New Gamer", 0) == 1)
        {
            NewGamer.setTextColor(getResources().getColor(R.color.writing));
            NewGamer1.setTextColor(getResources().getColor(R.color.writing));
        }
        //on the scoreboard
        if (achievement.getInt("On The Scoreboard", 0) == 1)
        {
            OnTheScoreboard.setTextColor(getResources().getColor(R.color.writing));
            OnTheScoreboard1.setTextColor(getResources().getColor(R.color.writing));
        }
        //mile high club
        if (achievement.getInt("Mile High Club", 0) == 1)
        {
            MileHighClub.setTextColor(getResources().getColor(R.color.writing));
            MileHighClub1.setTextColor(getResources().getColor(R.color.writing));
        }
        //challenger
        if (achievement.getInt("Challenger", 0) == 1)
        {
            Challenger.setTextColor(getResources().getColor(R.color.writing));
            Challenger1.setTextColor(getResources().getColor(R.color.writing));
        }
        //oooops
        if (achievement.getInt("Oooops", 0) == 1)
        {
            Oooops.setTextColor(getResources().getColor(R.color.writing));
            Oooops1.setTextColor(getResources().getColor(R.color.writing));
        }
        //How Grand
        if (achievement.getInt("How Grand", 0) == 1)
        {
            HowGrand.setTextColor(getResources().getColor(R.color.writing));
            HowGrand1.setTextColor(getResources().getColor(R.color.writing));
        }
        //Apple of Eden
        if (achievement.getInt("Apple of Eden", 0) == 1)
        {
            AppleOfEden.setTextColor(getResources().getColor(R.color.writing));
            AppleOfEden1.setTextColor(getResources().getColor(R.color.writing));
        }
        //Hungry Snake
        if (achievement.getInt("Hungry Snake", 0) == 1)
        {
            HungrySnake.setTextColor(getResources().getColor(R.color.writing));
            HungrySnake1.setTextColor(getResources().getColor(R.color.writing));
        }
        //Snack Attack
        if (achievement.getInt("Snack Attack", 0) == 1)
        {
            SnackAttack.setTextColor(getResources().getColor(R.color.writing));
            SnackAttack1.setTextColor(getResources().getColor(R.color.writing));
        }
        //3 course meal
        if (achievement.getInt("3 Course Meal", 0) == 1)
        {
            ThreeCourseMeal.setTextColor(getResources().getColor(R.color.writing));
            ThreeCourseMeal1.setTextColor(getResources().getColor(R.color.writing));
        }

    }
}
