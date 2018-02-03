package com.example.joelclarke.snakeforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.joelclarke.snakeforandroid.engine.GameEngine;
import com.example.joelclarke.snakeforandroid.enums.Difficulty;
import com.example.joelclarke.snakeforandroid.enums.Direction;
import com.example.joelclarke.snakeforandroid.enums.GameState;
import com.example.joelclarke.snakeforandroid.views.SnakeView;
import java.util.ArrayList;

public class NewGame extends AppCompatActivity implements RecognitionListener {

    private GameEngine gameEngine;
    private SnakeView snakeView;
    private final Handler handler = new Handler();
    private long updateDelay = 250;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private TextView highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        SharedPreferences achievement = getSharedPreferences("achievements", 0);
        SharedPreferences.Editor editor = achievement.edit();
        editor.putInt("New Gamer", 1);
        editor.apply();

        SharedPreferences scores = getSharedPreferences("scores", 0);
        highscore = (TextView) findViewById(R.id.high_score_text);
        int x = scores.getInt("score1", 0);
        highscore.setText("High Score: " + x);

        //set the current difficulty setting
        SharedPreferences difficultyLevel = getSharedPreferences("difficulty", Context.MODE_PRIVATE);
        int d = difficultyLevel.getInt("speed", 2);

        gameEngine = new GameEngine();
        gameEngine.initGame();

        switch(d){
            case 1:
                gameEngine.UpdateDifficulty(Difficulty.Easy);
                updateDelay = 400;
                break;
            case 2:
                gameEngine.UpdateDifficulty(Difficulty.Medium);
                updateDelay = 250;
                break;
            case 3:
                gameEngine.UpdateDifficulty(Difficulty.Hard);
                updateDelay = 120;
                break;
        }

        //set the background to current setting
        LinearLayout set = (LinearLayout) findViewById(R.id.activity_new_game);
        SharedPreferences setting = getSharedPreferences("background", Context.MODE_PRIVATE);
        int bg = setting.getInt("background_resource", getResources().getColor(R.color.colorPrimary));
        set.setBackgroundColor(bg);

        //create the gameboard
        snakeView = (SnakeView) findViewById(R.id.snake_view);
        //update the gameboard background to match the settings
        snakeView.back=bg;
        startUpdateHandler();
        ImageButton left = (ImageButton) findViewById(R.id.left_btn);
        ImageButton right = (ImageButton) findViewById(R.id.right_btn);
        ImageButton up = (ImageButton) findViewById(R.id.up_btn);
        ImageButton down = (ImageButton) findViewById(R.id.down_btn);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameEngine.UpdateDirection(Direction.West);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameEngine.UpdateDirection(Direction.East);
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameEngine.UpdateDirection(Direction.North);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameEngine.UpdateDirection(Direction.South);
            }
        });




        SharedPreferences voiceCommands = getSharedPreferences("voiceCommands", 0);
        boolean v = voiceCommands.getBoolean("toggle", false);
        //check to see if voice commands is turned on.
        if (v)
        {
            //check for permission to use voice commands
            int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            {
                //ask user permission then restart
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 0);
            }
            //start voice recognition
            voiceCommands();
        }


    }




    private int score;

    private void startUpdateHandler() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //keep the score in bottom left of screen
                TextView txtScore = (TextView) findViewById(R.id.score_text);
                gameEngine.Update();
                score = gameEngine.getScore();
                String s = "Score: " + Integer.toString(score);
                txtScore.setText(s);

                if (gameEngine.getCurrentGameState() == GameState.Running) {
                    handler.postDelayed(this,updateDelay);

                }
                if (gameEngine.getCurrentGameState() == GameState.Lost) {
                    GameOver();
                }
                snakeView.setSnakeViewMap(gameEngine.getMap());
                snakeView.invalidate();
            }
        }, updateDelay);
    }

    private void GameOver() {

        SharedPreferences voiceCommands = getSharedPreferences("voiceCommands", 0);
        boolean v = voiceCommands.getBoolean("toggle", false);
        //check to see if voice commands is turned on.
        if (v)
        {
            speech.stopListening();
            speech.destroy();
        }

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(NewGame.this);
        final View mView = getLayoutInflater().inflate(R.layout.game_over, null);

        Button restart = (Button) mView.findViewById(R.id.restart);
        Button menuBtn = (Button) mView.findViewById(R.id.main_menu);
        TextView game_score = (TextView) mView.findViewById(R.id.game_score);
        final EditText NameInput = (EditText) mView.findViewById(R.id.name_input);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        LinearLayout set = (LinearLayout) mView.findViewById(R.id.popup_game_over);
        SharedPreferences setting = getSharedPreferences("background", Context.MODE_PRIVATE);
        int bg = setting.getInt("background_resource", getResources().getColor(R.color.colorPrimary));
        set.setBackgroundColor(bg);

        final int score = gameEngine.getScore();
        achievementChecker(score);
        String S = Integer.toString(score);
        game_score.setText("Score: " + S);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = String.valueOf(NameInput.getText());
                sendScore(s, score);
                dialog.dismiss();
                restart();
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = String.valueOf(NameInput.getText());
                sendScore(s, score);
                dialog.dismiss();
                MenuRturn();
            }
        });

    }

    //check whether any achievements criteria have been met
    private void achievementChecker(int score)
    {
        SharedPreferences achievements = getSharedPreferences("achievements", 0);
        SharedPreferences.Editor editor = achievements.edit();
        //check whether they've beaten the achievements

        //The Hunger Games (over 5000 points)
        if (score >= 5000)
        {
            //has it already been achieved?
            if (achievements.getInt("Snack Attack", 0) == 0)
            {
                editor.putInt("Snack Attack", 1);
                editor.apply();
                Toast.makeText(this, "achievement unlocked: 'Snack Attack'", Toast.LENGTH_SHORT).show();
            }
        }

        //3 course meal
        if (score >= 3000)
        {
            if (achievements.getInt("3 Course Meal", 0) == 0)
            {
                editor.putInt("3 Course Meal", 1);
                editor.apply();
                Toast.makeText(this, "achievement unlocked: '3 Course Meal'", Toast.LENGTH_SHORT).show();
            }
        }

        //How Grand
        if (score >= 1000)
        {
            if (achievements.getInt("How Grand", 0) == 0)
            {
                editor.putInt("How Grand", 1);
                editor.apply();
                Toast.makeText(this, "achievement unlocked: 'How Grand'", Toast.LENGTH_SHORT).show();
            }
        }

        //Hungry Hungry Snakes
        if (score >= 2000)
        {
            if (achievements.getInt("Hungry Snake", 0) == 0)
            {
                editor.putInt("Hungry Snake", 1);
                editor.apply();
                Toast.makeText(this, "achievement unlocked: 'Hungry Hungry Snakes'", Toast.LENGTH_SHORT).show();
            }
        }

        //Mile High Club
        if (score >= 1600)
        {
            if (achievements.getInt("Mile High Club", 0) == 0)
            {
                editor.putInt("Mile High Club", 1);
                editor.apply();
                Toast.makeText(this, "achievement unlocked: 'Mile High Club'", Toast.LENGTH_SHORT).show();
            }
        }

        //Oooops
        if (score == 0)
        {
            if (achievements.getInt("Oooops", 0) == 0)
            {
                editor.putInt("Oooops", 1);
                editor.apply();
                Toast.makeText(this, "achievement unlocked: 'Oooops!'", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendScore(String name, int score)
    {
        SharedPreferences newScore = getSharedPreferences("scores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = newScore.edit();
        editor.putString("newName", name);
        editor.putInt("newScore", score);
        editor.apply();
        challengerChecker(name);
        HighscoreTable();
    }

    public void restart()
    {
        this.recreate();
    }

    public void MenuRturn()
    {
        startActivity(new Intent(NewGame.this, MainMenu.class));
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(NewGame.this, MainMenu.class));
    }

    public void voiceCommands()
    {
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBegginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float v) {
        Log.i(LOG_TAG, "onRmsChanged: "+v);
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferRecieved: " + bytes);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");


    }

    @Override
    public void onError(int errorCode) {
        Log.d(LOG_TAG, "Failed" +errorCode);
        voiceCommands();
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (matches.contains("up"))
        {
            gameEngine.UpdateDirection(Direction.North);
            matches.clear();
            voiceCommands();
        }
        else if (matches.contains("down"))
        {
            gameEngine.UpdateDirection(Direction.South);
            matches.clear();
            voiceCommands();
        }
        else if (matches.contains("left"))
        {
            gameEngine.UpdateDirection(Direction.West);
            matches.clear();
            voiceCommands();
        }
        else if (matches.contains("right"))
        {
            gameEngine.UpdateDirection(Direction.East);
            matches.clear();
            voiceCommands();
        }

    }

    @Override
    public void onPartialResults(Bundle results) {
        Log.i(LOG_TAG, "onPartialResults");

        ArrayList matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches.contains("up"))
        {
            gameEngine.UpdateDirection(Direction.North);
            matches.clear();
            voiceCommands();
        }
        if (matches.contains("down"))
        {
            gameEngine.UpdateDirection(Direction.South);
            matches.clear();
            voiceCommands();
        }
        if (matches.contains("left"))
        {
            gameEngine.UpdateDirection(Direction.West);
            matches.clear();
            voiceCommands();
        }
        if (matches.contains("right"))
        {
            gameEngine.UpdateDirection(Direction.East);
            matches.clear();
            voiceCommands();
        }

    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");
    }

    private void challengerChecker(String name)
    {
        SharedPreferences achievement = getSharedPreferences("achievements", 0);
        SharedPreferences challenger = getSharedPreferences("challengerChecker", 0);
        SharedPreferences.Editor editChallenge = challenger.edit();
        SharedPreferences.Editor editor = achievement.edit();

        //get the first name and check whether it's the same...
        String one = challenger.getString("challenger1", "");
        String two = challenger.getString("challenger2", "");
        String three = challenger.getString("challenger3", "");
        if (one != name)
        {
            if (one == "")
            {
                editChallenge.putString("challenger1", name);
                editChallenge.apply();
            }
            if (two != name)
            {
                if (two == "")
                {
                    editChallenge.putString("challenger2", name);
                    editChallenge.apply();
                }
                else
                {
                    editor.putInt("Challenger", 1);
                    editor.apply();
                }
            }
        }

    }


    //fill in the highscore table
    public void HighscoreTable ()
    {
        //the different names that are in the top 10 of the table
        String name1, name2, name3, name4, name5, name6, name7, name8, name9, name10;

        //the scores to go with the names.
        int score1, score2, score3, score4, score5, score6, score7, score8, score9, score10;

        //new details to add to the score sheet
        int newScore;
        String newName;
        SharedPreferences score = getSharedPreferences("scores", Context.MODE_PRIVATE);
        newName = score.getString("newName", "");
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




        //time to check the newScore against the existing scores
        if ((newScore > score10) && (newScore <= score9))
        {
            score10 = newScore;
            name10 = newName;

            edit(name10, "name10", "score10", score10);
        }

        //9
        if ((newScore > score9) && (newScore <= score8))
        {
            score10 = score9;
            name10 = name9;

            edit(name10, "name10", "score10", score10);

            score9 = newScore;
            name9 = newName;

            edit(name9, "name9", "score9", score9);
        }

        //8
        if ((newScore > score8) && (newScore <= score7))
        {
            score10 = score9;
            name10 = name9;

            edit(name10, "name10", "score10", score10);

            score9 = score8;
            name9 = name8;

            edit(name9, "name9", "score9", score9);

            score8 = newScore;
            name8 = newName;

            edit(name8, "name8", "score8", score8);
        }

        //7
        if ((newScore > score7) && (newScore <= score6))
        {
            score10 = score9;
            name10 = name9;

            edit(name10, "name10", "score10", score10);

            score9 = score8;
            name9 = name8;

            edit(name9, "name9", "score9", score9);

            score8 = score7;
            name8 = name7;

            edit(name8, "name8", "score8", score8);

            score7 = newScore;
            name7 = newName;
            edit(name7, "name7", "score7", score7);
        }

        //6
        if ((newScore > score6) && (newScore <= score5))
        {
            score10 = score9;
            name10 = name9;

            edit(name10, "name10", "score10", score10);

            score9 = score8;
            name9 = name8;

            edit(name9, "name9", "score9", score9);

            score8 = score7;
            name8 = name7;

            edit(name8, "name8", "score8", score8);

            score7 = score6;
            name7 = name6;
            edit(name7, "name7", "score7", score7);

            score6 = newScore;
            name6 = newName;
            edit(name6, "name6", "score6", score6);
        }

        //5
        if ((newScore > score5) && (newScore <= score4))
        {
            score10 = score9;
            name10 = name9;

            edit(name10, "name10", "score10", score10);

            score9 = score8;
            name9 = name8;

            edit(name9, "name9", "score9", score9);

            score8 = score7;
            name8 = name7;

            edit(name8, "name8", "score8", score8);

            score7 = score6;
            name7 = name6;
            edit(name7, "name7", "score7", score7);

            score6 = score5;
            name6 = name5;
            edit(name6, "name6", "score6", score6);

            score5 = newScore;
            name5 = newName;
            edit(name5, "name5", "score5", score5);
        }

        //4
        if ((newScore > score4) && (newScore <= score3))
        {
            score10 = score9;
            name10 = name9;

            edit(name10, "name10", "score10", score10);

            score9 = score8;
            name9 = name8;

            edit(name9, "name9", "score9", score9);

            score8 = score7;
            name8 = name7;

            edit(name8, "name8", "score8", score8);

            score7 = score6;
            name7 = name6;
            edit(name7, "name7", "score7", score7);

            score6 = score5;
            name6 = name5;
            edit(name6, "name6", "score6", score6);

            score5 = score4;
            name5 = name4;
            edit(name5, "name5", "score5", score5);

            score4 = newScore;
            name4 = newName;
            edit(name4, "name4", "score4", score4);
        }

        //3
        if ((newScore > score3) && (newScore <= score2))
        {
            score10 = score9;
            name10 = name9;

            edit(name10, "name10", "score10", score10);

            score9 = score8;
            name9 = name8;

            edit(name9, "name9", "score9", score9);

            score8 = score7;
            name8 = name7;

            edit(name8, "name8", "score8", score8);

            score7 = score6;
            name7 = name6;
            edit(name7, "name7", "score7", score7);

            score6 = score5;
            name6 = name5;
            edit(name6, "name6", "score6", score6);

            score5 = score4;
            name5 = name4;
            edit(name5, "name5", "score5", score5);

            score4 = score3;
            name4 = name3;
            edit(name4, "name4", "score4", score4);

            score3 = newScore;
            name3 = newName;
            edit(name3, "name3", "score3", score3);
        }

        //2
        if ((newScore > score2) && (newScore <= score1))
        {
            score10 = score9;
            name10 = name9;

            edit(name10, "name10", "score10", score10);

            score9 = score8;
            name9 = name8;

            edit(name9, "name9", "score9", score9);

            score8 = score7;
            name8 = name7;

            edit(name8, "name8", "score8", score8);

            score7 = score6;
            name7 = name6;
            edit(name7, "name7", "score7", score7);

            score6 = score5;
            name6 = name5;
            edit(name6, "name6", "score6", score6);

            score5 = score4;
            name5 = name4;
            edit(name5, "name5", "score5", score5);

            score4 = score3;
            name4 = name3;
            edit(name4, "name4", "score4", score4);

            score3 = score2;
            name3 = name2;
            edit(name3, "name3", "score3", score3);

            score2 = newScore;
            name2 = newName;
            edit(name2, "name2", "score2", score2);
        }

        //1
        if (newScore > score1)
        {
            score10 = score9;
            name10 = name9;

            edit(name10, "name10", "score10", score10);

            score9 = score8;
            name9 = name8;

            edit(name9, "name9", "score9", score9);

            score8 = score7;
            name8 = name7;

            edit(name8, "name8", "score8", score8);

            score7 = score6;
            name7 = name6;
            edit(name7, "name7", "score7", score7);

            score6 = score5;
            name6 = name5;
            edit(name6, "name6", "score6", score6);

            score5 = score4;
            name5 = name4;
            edit(name5, "name5", "score5", score5);

            score4 = score3;
            name4 = name3;
            edit(name4, "name4", "score4", score4);

            score3 = score2;
            name3 = name2;
            edit(name3, "name3", "score3", score3);

            score2 = score1;
            name2 = name1;
            edit(name2, "name2", "score2", score2);

            score1 = newScore;
            name1 = newName;
            edit(name1, "name1", "score1", score1);

            //clear the newScore from Shared Preferences
            SharedPreferences.Editor editor = score.edit();
            editor.remove("newName");
            editor.remove("newScore");
            editor.apply();
        }
    }
    public void edit (String newName, String posName, String posScore, int score)
    {
        SharedPreferences scores = getSharedPreferences("scores", 0);
        SharedPreferences.Editor editor = scores.edit();

        editor.putInt(posScore, score);
        editor.putString(posName, newName);
        editor.apply();

    }
}
