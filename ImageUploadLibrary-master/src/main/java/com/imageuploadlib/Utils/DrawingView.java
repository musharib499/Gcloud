package com.imageuploadlib.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Lakshay on 21-07-2015.
 */


public class DrawingView extends View {
    private boolean haveTouch = false;
    private Rect touchArea;
    private Paint paint;

    public DrawingView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(0xee00FF00);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        haveTouch = false;
    }

    public void setHaveTouch(boolean val, Rect rect) {
        haveTouch = val;
        touchArea = rect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (haveTouch) {
            canvas.drawRect(
                    touchArea.left, touchArea.top, touchArea.right, touchArea.bottom,
                    paint);
        }
    }
}