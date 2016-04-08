package com.gcloud.gaadi.ui.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Gaurav on 22-05-2015.
 */
public class ImageUploadImageView extends ImageView {

    final public static int NONE = 98;
    final public static int RETRY = 99;
    final public static int UPLOADING = 100;
    final public static int UPLOADED = 101;
    private int status = NONE;

    private Paint paint;

    public ImageUploadImageView(Context context) {
        this(context, null);
    }

    public ImageUploadImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageUploadImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(50);
        paint.setColor(Color.parseColor("#E6E6E6"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (status == UPLOADING || status == NONE)
            canvas.saveLayerAlpha(getScrollX(), getScrollY(), canvas.getWidth(), canvas.getHeight(), 200, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);

        super.onDraw(canvas);

        /*if (status == UPLOADED) {
            String str = "Uploaded";
            Paint.FontMetrics fm = new Paint.FontMetrics();
            paint.setColor(Color.parseColor("#3ADF00"));
            paint.setTextSize(28.0f);

            paint.getFontMetrics(fm);

            int margin = 10;
            int left = (int) ((getScrollX()+getWidth()/2)-(paint.measureText(str)/2)),
                    top = ((getScrollY()+getHeight()/2));
            canvas.drawRect(left - margin,
                    top + fm.top - margin,
                    left + paint.measureText(str) + margin,
                    top + fm.bottom + margin,
                    paint);

            paint.setColor(Color.WHITE);

            canvas.drawText(str, left, top, paint);
        }*/
    }

    public void changeStatus(int status) {
        this.status = status;
        invalidate();
    }
}