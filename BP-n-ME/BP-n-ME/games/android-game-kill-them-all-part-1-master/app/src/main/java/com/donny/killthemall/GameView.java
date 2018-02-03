package com.donny.killthemall;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by donny on 9/12/15.
 */
public class GameView extends SurfaceView {

    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
    private List<Sprite> sprites = new ArrayList<Sprite>();
    private long lastClick;

    private Bitmap bmpBlood;
    private List<TempSprite> temps = new ArrayList<TempSprite>();

    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    private SoundPool soundPool;
    private int goodGuyScream = -1;
    private int badGuyScream = -1;

    public GameView(Context context) {
        super(context);

        // get holder
        holder = getHolder();

        // get thread
        gameLoopThread = new GameLoopThread(this);

        // register callback
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // when view is created
                createSpaceDust();
                createSprites();
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // when view is changed
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // when view is destroyed
            }
        });

        bmpBlood = BitmapFactory.decodeResource(getResources(), R.drawable.blood1);

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try{
            //Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //create our three fx in memory ready for use
            descriptor = assetManager.openFd("female_scream.wav");
            goodGuyScream = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("male_scream.wav");
            badGuyScream = soundPool.load(descriptor, 0);

        } catch(IOException e) {
            //Print an error message to the console
            Log.e("error", "failed to load sound files " + e.toString());
        }
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        //Draw the dust from our arrayList
        for (SpaceDust spaceDust : dustList) {
            spaceDust.onDraw(canvas);
        }

        for (int i = temps.size() - 1; i >= 0; i--) {
            temps.get(i).onDraw(canvas);
        }

        for (Sprite sprite : sprites) {
            sprite.onDraw(canvas);
        }
    }

    private void createSpaceDust() {
        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++) {
            // Where will the dust spawn?
            SpaceDust spec = new SpaceDust(getWidth(), getHeight());
            dustList.add(spec);
        }
    }

    private void createSprites() {
        int[] goodResources = {R.drawable.good1, R.drawable.good2, R.drawable.good3, R.drawable.good4, R.drawable.good5, R.drawable.good6};
        int[] badResources = {R.drawable.bad1, R.drawable.bad2, R.drawable.bad3, R.drawable.bad4, R.drawable.bad5, R.drawable.bad6};
        int goodResourcesLength = goodResources.length;
        int badResourcesLength = badResources.length;
        int numGood = 30;
        int numBad = 30;

        Random rnd = new Random();

        for (int i = 0; i < numGood; i++) {
            int goodResourceIndex = rnd.nextInt(goodResourcesLength - 1);
            sprites.add(createSprite(goodResources[goodResourceIndex], true));
        }

        for (int i = 0; i < numBad; i++) {
            int badResourceIndex = rnd.nextInt(badResourcesLength - 1);
            sprites.add(createSprite(badResources[badResourceIndex], false));
        }
    }

    private Sprite createSprite(int resouce, boolean goodGuy) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
        return new Sprite(this, bmp, goodGuy);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - lastClick > 300) {
            lastClick = System.currentTimeMillis();
            float x = event.getX();
            float y = event.getY();
            synchronized (getHolder()) {
                for (int i = sprites.size() - 1; i >= 0; i--) {
                    Sprite sprite = sprites.get(i);
                    if (sprite.isCollition(x, y)) {
                        sprites.remove(sprite);
                        // add blood
                        temps.add(new TempSprite(temps, this, x, y, bmpBlood));
                        if (sprite.isGoodGuy()) {
                            soundPool.play(goodGuyScream, 1, 1, 0, 0, 1);
                        } else {
                            soundPool.play(badGuyScream, 1, 1, 0, 0, 1);
                        }

                        break;
                    }
                }
            }
        }

        return true;
    }
}
