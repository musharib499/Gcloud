package com.gcloud.gaadi.ui.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.gcloud.gaadi.R;

/**
 * Created by Mushareb Ali on 24-08-2015.
 */
public class CircularView extends View {

    private int completionPercent=0;
    private Paint paint = new Paint();
    private int radius = 70;
    private int strokeSize = 20;
    private int textSize = 20;
    private int diameter = radius * 2;
    private float approvedPercentage, rejectedPercentage, pendingPercentage;

    /*  public CircularView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
          super(context, attrs, defStyleAttr, defStyleRes);
      }
  */
    public CircularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    public CircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CircularView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setCompletionPercentage(int completion){
        completionPercent = completion;//size should change for different Resolution screens
        invalidate();
    }

    public void setTextSize(int size){
        textSize = size;//size should change for different Resolution screens
        invalidate();
    }


    public void setStrokeSize(int size){
        strokeSize = size;//size should change for different Resolution screens
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        if(width > height){
            width = height;
        }
        else{
            height = width;
        }

        diameter = width;
        radius = diameter/2;

        int newWidthMeasureSpec=MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int newHeightMeasureSpec=MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
    }

    public void setData(int approved, int rejected, int pending, float total) {
        this.approvedPercentage = (approved / total) * 100;
        this.rejectedPercentage = (rejected / total) * 100;
        this.pendingPercentage = 100 - (this.approvedPercentage + this.rejectedPercentage);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.parseColor("#dedede"));  // circle stroke color- grey
        paint.setStrokeWidth(strokeSize);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
       /* Path segmentPath = new Path();
        final RectF oval1 = new RectF();
        segmentPath.addArc(oval1, 90, 180 );
        canvas.drawPath(segmentPath, paint);*/
        canvas.drawCircle(radius, radius, radius - 10, paint);

        paint.setColor(Color.parseColor("#04B404"));  // circle stroke color(indicating completion Percentage) - green
        paint.setStrokeWidth(strokeSize);
        paint.setStyle(Paint.Style.FILL);

        final RectF oval = new RectF();
        paint.setStyle(Paint.Style.STROKE);
        oval.set(10, 10, (diameter) - 10, (diameter) - 10);

        float approvedAngle = ((approvedPercentage * 360) / 100);
        float rejectedAngle = ((rejectedPercentage * 360) / 100);
        float pendingAngle = 360 - (approvedAngle + rejectedAngle);
        canvas.drawArc(oval, 270, approvedAngle, false, paint);
        paint.setColor(Color.parseColor("#ff0000"));   //yellow color
        canvas.drawArc(oval, 270 + approvedAngle, rejectedAngle, false, paint);
        paint.setColor(Color.parseColor("#FFFFEA1C"));  // red color
        canvas.drawArc(oval, 270 + approvedAngle + rejectedAngle, pendingAngle, false, paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.parseColor("#282828"));  // text color - dark grey
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(getResources().getDimension(R.dimen.circle_text_Size));

        canvas.drawText(completionPercent +"", radius, radius+(paint.getTextSize()/2), paint);

    }

}

