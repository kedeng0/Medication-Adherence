package com.example.joelclarke.snakeforandroid.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.joelclarke.snakeforandroid.R;
import com.example.joelclarke.snakeforandroid.enums.TileType;

/**
 * Created by joelclarke on 21/08/2017.
 */

public class SnakeView extends View {
    private Paint mPaint = new Paint();
    private TileType snakeViewMap[][];

    public int back;
    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSnakeViewMap (TileType[][] map) {this.snakeViewMap = map;}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if( snakeViewMap != null){
            int tileSizeX = canvas.getWidth() / snakeViewMap.length;
            int tileSizeY = canvas.getHeight()/snakeViewMap[0].length;


            mPaint.setStyle(Paint.Style.FILL);

            int background = getContext().getResources().getColor(R.color.colorPrimary);

            for (int x = 0; x < snakeViewMap.length; x++)
                for (int y = 0; y < snakeViewMap[x].length; y++) {
                    switch (snakeViewMap[x][y]) {
                        case Nothing:
                            mPaint.setColor(back);
                            break;
                        case Wall:
                            mPaint.setColor(getResources().getColor(R.color.writing));
                            break;
                        case Food:
                            mPaint.setColor(getResources().getColor(R.color.no_achieve));
                            break;
                        case SnakeHead:
                            mPaint.setColor(getResources().getColor(R.color.snakeHead));
                            break;
                        case SnakeTail:
                            mPaint.setColor(getResources().getColor(R.color.snaketail));
                            break;
                    }




                    canvas.drawRect(x * tileSizeX , y * tileSizeY , x * tileSizeX + tileSizeX, y * tileSizeY + tileSizeY, mPaint);
                }
        }
    }

}
