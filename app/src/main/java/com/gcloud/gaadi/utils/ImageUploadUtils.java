package com.gcloud.gaadi.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.gcloud.gaadi.constants.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ImageUploadUtils {

    public static String root = Environment.getExternalStorageDirectory().toString();

    public static Bitmap decodeFile(String path) {
        Bitmap b = null;
        File f = new File(path);
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int IMAGE_MAX_SIZE = 1024; // maximum dimension limit
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return b;
    }

    public static String SaveCompressedFile(String path) {

        File myDir;

        myDir = new File(root + "/android/data/com.gcloud.gaadi/temp");

        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpeg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            Bitmap finalBitmap = decodeFile(path);
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        GCLog.e("File size after compressing: " + file.length() / Math.pow(1024, 2));
        return file.getPath();
    }

    public static boolean exceedFileMaxSize(long size) {
        int maxsize = Constants.MAX_IMAGE_UPLOAD_SIZE; // Maximum Image size limit in mb

        GCLog.e("Image size: " + size);
        double filesize = size / Math.pow(1024, 2); // file size in mb
        GCLog.e(filesize + "");
        if (filesize > maxsize) {
            return true;
        } else
            return false;

    }

    public static void DeleteTempFile() {
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/android/data/com.gcloud.gaadi/temp");
        for (File file : dir.listFiles())
            file.delete();
    }

    // public static File getImagepath() {
    // File myDir = new File(root + "/Android/data/com.nnacres.app/temp");
    // myDir.mkdirs();
    // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
    // Locale.getDefault()).format(new Date());
    // String fname = "Image_" + timeStamp + ".jpeg";
    // File file = new File(myDir, fname);
    // return file;
    // }

    public static File getImagepath() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fname = "Image_" + timeStamp + ".jpeg";

        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), fname);
    }

    public static boolean CheckValidImage(String fileName) {
        //  GCLog.v("CheckValidImage", fileName);
        GCLog.e(fileName);
        if (fileName != null) {
            if (fileName.toLowerCase().endsWith("jpg")) {
                return true;
            }
            if (fileName.toLowerCase().endsWith("jpeg")) {
                return true;
            } else if (fileName.toLowerCase().endsWith("png")) {
                return true;
            } else if (fileName.toLowerCase().endsWith("bmp")) {
                return true;
            } else if (fileName.toLowerCase().endsWith("gif")) {
                return true;
            } else if (fileName.toLowerCase().endsWith("pjpeg")) {
                return true;
            } else if (fileName.startsWith("http://")) {
                return true;
            } else if (fileName.startsWith("https://")) {
                return true;
            } else
                return false;
        } else
            return false;
    }
}
