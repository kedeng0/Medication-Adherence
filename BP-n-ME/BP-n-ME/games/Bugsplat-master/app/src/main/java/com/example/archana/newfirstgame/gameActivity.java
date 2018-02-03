package com.example.archana.newfirstgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class gameActivity extends AppCompatActivity {

    AnimationDrawable rocketAnimation;
    ImageButton imagebutton1;
    TextView fingame;
    ArrayList <ImageButton> newImages = new ArrayList<ImageButton>();
    ArrayList <Integer> newImageint = new ArrayList<Integer>();
    int count = 0;
    TextView text;
    private final long startTime = 50000;
    private final long interval = 1000;
    private long timeElapsed;
    boolean isCanceled = false;
    Point SCREENSIZE = new Point();
    int NOOFBEES=5;
    //       ObjectAnimator objAnim1 = new ObjectAnimator();


    /** Property name. */
    private String mPropertyName;
    /** Duration. */
    private long mDuration;
    /** Value from as int. */
    private int mValueIntFrom;
    /** Value to as int. */
    private int mValueIntTo;
    /** Value from as float. */
    private float mValueFloatFrom;
    /** Value to as float. */
    private float mValueFloatTo;
    /** Start offset. */
    private int mStartOffset;
    /** Repeat count. */
    private int mRepeatCount;
    /** Repeat mode. */
    private String mRepeatMode;
    /** Value type. */
    private String mValueType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Display display = getWindowManager().getDefaultDisplay();

        display.getSize(SCREENSIZE);

        fingame = (TextView) findViewById(R.id.gameText);
        newImages.add((ImageButton) findViewById(R.id.viewimage));
        newImages.add((ImageButton) findViewById(R.id.viewimage2));

        newImages.add((ImageButton) findViewById(R.id.viewimage3));
        newImages.add((ImageButton) findViewById(R.id.viewimage4));
        newImages.add((ImageButton) findViewById(R.id.viewimage5));

        newImageint.add(R.id.viewimage);
        newImageint.add(R.id.viewimage2);
        newImageint.add(R.id.viewimage3);
        newImageint.add(R.id.viewimage4);
        newImageint.add(R.id.viewimage5);
        text = (TextView) this.findViewById(R.id.timeText);



        final CountDownTimer was =  new CountDownTimer(60000, 1000)
        {

            public void onTick(long millisUntilFinished) {
                text.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.println(0, "test", "done!");
//                objAnim1.cancel();
                // add new intent here to end game. in the new intent display the score
            }

        };  was.start();

        AnimateAllBees();


        View.OnClickListener oclBtnOk = new View.OnClickListener() {
            public void onClick(View v)
            {

                for (int i = 0; i < newImageint.size(); i++) {

                    if (v.getId() == newImageint.get(i)) {
                        imagebutton1 = (ImageButton) findViewById(newImageint.get(i));
                        imagebutton1.setImageResource(R.drawable.splat);
                        imagebutton1.postDelayed(new Runnable() {
                            public void run() {
                                imagebutton1.setVisibility(View.INVISIBLE);
                            }

                        }, 300);

                        // imagebutton1.setVisibility(View.GONE);
                        count = count + 1;
                        // gamepiece(newImages.get(i));
                        //newImages.get(1).setX(0);
                        //newImages.get(1).setY(0);
                        // newImages.get(i).setVisibility(View.VISIBLE);
                    }
                }
                if (count == 5)
                {

                    fingame.setText("GAME OVER");

                    was.cancel();

                }


            }
        };



        for (int i = 0; i < newImages.size(); i++) {
            newImages.get(i).setOnClickListener(oclBtnOk);
        }








    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
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

    void fillDataFromForm() {
        mPropertyName = "test";
        mDuration = 60* 1000;
        mValueIntFrom = 1;
        mValueIntTo = 400;
        mValueFloatFrom = 1;
        mValueFloatTo = 400;
        mStartOffset = 0;
        mRepeatCount = 50;
        mRepeatMode = "repeat";
        mValueType = "floatType";

    }

    public void gamepiece(ImageButton v) {
        boolean isTrue = true;
        float scale = getResources().getDisplayMetrics().density;
        Random rand = new Random();
        int sc = rand.nextInt(300) + 1;
        Random rand2 = new Random();
        int sc2 = rand2.nextInt(300) + 1;

        ImageButton someImage = v;

        ObjectAnimator[] animset = new ObjectAnimator[NOOFBEES];
        int repatMode = ValueAnimator.REVERSE;



        for (int i = 0; i < NOOFBEES; i++) {
            animset[i] = new ObjectAnimator();
            animset[i].setRepeatMode(repatMode);
            animset[i].setRepeatCount(mRepeatCount);
            animset[i].setDuration(1000);
        }

        for (int i = 0; i < NOOFBEES; i++) {


            animset[i].setInterpolator(new AccelerateDecelerateInterpolator());

            animset[i] = animset[i].ofFloat(someImage,"y", sc * scale, sc2 * scale);
            sc = rand.nextInt(300) + 1;
            sc2 = rand2.nextInt(300) + 1;


        }
        AnimatorSet set = new AnimatorSet();
        set.play(animset[0]);



        set.start();


    }
    public void AnimateAllBees() {

        float scale = getResources().getDisplayMetrics().density;
        Random rand = new Random();
        Random rand2 = new Random();

        int sc ;
        int sc2 ;
        int XMAX=SCREENSIZE.x - 100;
        int YMAX=SCREENSIZE.y - 225;

        sc = rand.nextInt(SCREENSIZE.y);
        sc2 = rand2.nextInt(SCREENSIZE.y);

        ObjectAnimator objAnim = new ObjectAnimator();

        Path path = new Path();
        Point p=new Point();
        p.x=rand.nextInt(XMAX);
        p.y=rand.nextInt(YMAX);
        path.lineTo(p.x, p.y);
        p.x=rand.nextInt(XMAX);
        p.y=rand.nextInt(YMAX);
        path.lineTo(p.x, p.y);
        p.x=rand.nextInt(XMAX);
        p.y=rand.nextInt(YMAX);
        path.lineTo(p.x, p.y);

        RectF rect = new RectF();
        int i=1;
        rect.set(30, 30, XMAX, YMAX);
        path.addRect(rect, i % 2 == 0 ? Path.Direction.CW : Path.Direction.CCW);
        path.addCircle(SCREENSIZE.x / 2, SCREENSIZE.y / 2, 100, Path.Direction.CCW);
        path.addCircle(80, 80, 45, Path.Direction.CW);
        path.setFillType(Path.FillType.WINDING);
        objAnim = objAnim.ofFloat(newImages.get(0),"x", "y", path);
        objAnim.setDuration(2 * 1000);
        objAnim.setInterpolator(new AccelerateInterpolator());
        objAnim.setRepeatCount(10);


        int repatMode;
        repatMode = ValueAnimator.RESTART;
        objAnim.setRepeatMode(repatMode);
        objAnim.start();

        sc = rand.nextInt(SCREENSIZE.y);
        sc2 = rand2.nextInt(SCREENSIZE.y);

        ObjectAnimator objAnim1 = new ObjectAnimator();
        objAnim1=objAnim1.ofFloat(newImages.get(1), "y", sc * scale, sc2 * scale);
        objAnim1.setDuration(1000);
       // objAnim1.setInterpolator(new AccelerateDecelerateInterpolator());
        objAnim1.setRepeatCount(10);
        repatMode = ValueAnimator.RESTART;
        objAnim1.setRepeatMode(repatMode);
       // objAnim1.start();

        final Path path2 = new Path();
        ObjectAnimator objAnim2 = new ObjectAnimator();
        path2.moveTo(400.0f, 400.0f);
        path2.addCircle(400, 400, 180, Path.Direction.CW);
        path2.cubicTo(400.0f, 400.0f, 50, 300, 300.0f, 300.0f);
        path2.cubicTo(300.0f, 300.0f, 600.0f, 300.0f, 400.0f, 600.0f);
        path2.lineTo(0, YMAX - 150);
        RectF rect2 = new RectF();
        rect2.set(XMAX, YMAX - 150, 400, 400);
        path2.addRect(rect2, i % 2 == 0 ? Path.Direction.CW : Path.Direction.CCW);
        path2.lineTo(400,400);


        objAnim2 = objAnim2.ofFloat(newImages.get(2), "x", "y", path2);
        objAnim2.setDuration(2 * 1000);
        objAnim2.setInterpolator(new AccelerateInterpolator());
        objAnim2.setRepeatCount(10);
        repatMode = ValueAnimator.RESTART;
        objAnim2.setRepeatMode(repatMode);
        objAnim2.start();

        final Path path3 = new Path();
        ObjectAnimator objAnim3 = new ObjectAnimator();
        path3.moveTo(400, 200);
        path3.cubicTo(400, 200, 450, YMAX - 100, 300, 100);
        path3.cubicTo(300.0f, 100.0f, 400.0f, 200.0f, 400.0f, 600.0f);
        path3.lineTo(500, 100);
        path3.lineTo(300, 300);
        path3.lineTo(600, 300);
        path3.lineTo(20, 500);
        path3.lineTo(100,YMAX-200);
        path3.lineTo(XMAX-200,200);
        path3.close();





        objAnim3 = objAnim2.ofFloat(newImages.get(3), "x", "y", path3);
        objAnim3.setDuration(2 * 1000);
        objAnim3.setInterpolator(new AccelerateInterpolator());
        objAnim3.setRepeatCount(10);
        repatMode = ValueAnimator.RESTART;
        objAnim3.setRepeatMode(repatMode);
        objAnim3.start();



    }
}