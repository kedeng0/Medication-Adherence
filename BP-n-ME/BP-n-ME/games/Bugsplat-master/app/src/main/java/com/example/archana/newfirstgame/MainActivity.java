package com.example.archana.newfirstgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity {
    TextView tvOut;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void rulesGame(View v)
    {
        btnOk = (Button) findViewById(R.id.rulesButton);
        OnClickListener oclBtnOk = new OnClickListener()
        {
            public void onClick(View v)
            {

               Intent intent = new Intent(MainActivity.this, RulesActivity.class);
               startActivity(intent);
            }
        };
        btnOk.setOnClickListener(oclBtnOk);
    }
    public void newGame(View v)
    {
        btnOk = (Button) findViewById(R.id.startButton);
        OnClickListener oclBtnOk = new OnClickListener()
        {
            public void onClick(View v)
            {

                Intent intent = new Intent(MainActivity.this, gameActivity.class);
                startActivity(intent);
            }
        };
        btnOk.setOnClickListener(oclBtnOk);
    }
}
