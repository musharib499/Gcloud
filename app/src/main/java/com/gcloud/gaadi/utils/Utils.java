package com.gcloud.gaadi.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.View;
import android.widget.Toast;

import com.gcloud.gaadi.constants.Constants;

import java.io.IOException;

/**
 * Created by ankit on 13/12/14.
 */
public class Utils {

    public static void setOnClickListener(View containerView, int list[], View.OnClickListener onClickListener) {
        for (int i = 0; i < list.length; i++) {
            try {
                containerView.findViewById(list[i]).setOnClickListener(onClickListener);

            } catch (Exception e) {
                GCLog.e("Wrong View Id Send at Index " + i);
                if (Constants.ENVIRONMENT.contains("DEV")) {
                    Toast.makeText(containerView.getContext(), "Wrong View Id Send at Index " + i, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {

            GCLog.e("cannot read exif", ex);

        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }

    // Gaurav Negi :Added
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
