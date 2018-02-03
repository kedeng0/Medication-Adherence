package com.appzoro.BP_n_ME.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.util;

public class SplashActivity extends AppCompatActivity {
    MedasolPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        prefs = new MedasolPrefs(getApplicationContext());
        final int type = prefs.getType();

        if ( util.isInternetOn(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (type == 1){
                        Log.e("type", "type "+ type);
                        Intent intent = new Intent(SplashActivity.this, PatientSelection.class);
                        startActivity(intent);
                        finish();
                    } else if (type == 2){
                        Log.e("type", "type "+ type);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("type", "type not found "+ type );
                        Intent i = new Intent(SplashActivity.this, SigninActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, 3000);
        } else {
            Toast.makeText(this, "We can't detect network connectivity, connect to the internet and try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
