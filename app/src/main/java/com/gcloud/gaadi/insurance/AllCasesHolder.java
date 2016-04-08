package com.gcloud.gaadi.insurance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.utils.GCLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Gaurav on 16-07-2015.
 */
public class AllCasesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView requestNumber, status, makeModel, inspected, registrationNumber, requestDate, insurer, premium,
            bookingDate, cancelMessage;
    private ImageView makeIcon;
    private LinearLayout inspectedLayout, cancelledLayout;
    private Button downloadPolicy;
    private String policyUrl, requestNo, makeModelVersion, insurerName;
    private Context context;
    private View bottomDivider;

    public AllCasesHolder(View itemView, Context context) {
        super(itemView);

        requestNumber = (TextView) itemView.findViewById(R.id.requestNumber);
        status = (TextView) itemView.findViewById(R.id.status);
        makeIcon = (ImageView) itemView.findViewById(R.id.makeIcon);
        makeModel = (TextView) itemView.findViewById(R.id.makeModel);
        inspected = (TextView) itemView.findViewById(R.id.inspected);
        registrationNumber = (TextView) itemView.findViewById(R.id.registrationNumber);
        requestDate = (TextView) itemView.findViewById(R.id.requestDate);
        insurer = (TextView) itemView.findViewById(R.id.insurer);
        premium = (TextView) itemView.findViewById(R.id.premium);
        bookingDate = (TextView) itemView.findViewById(R.id.bookingDate);
        cancelMessage = (TextView) itemView.findViewById(R.id.cancelMessage);

        inspectedLayout = (LinearLayout) itemView.findViewById(R.id.inspectedLayout);
        cancelledLayout = (LinearLayout) itemView.findViewById(R.id.cancelledLayout);

        downloadPolicy = (Button) itemView.findViewById(R.id.downloadPolicy);

        bottomDivider = itemView.findViewById(R.id.bottomDivider);

        this.context = context;
    }

    public TextView getRequestNumber() {
        return requestNumber;
    }

    public TextView getStatus() {
        return status;
    }

    public TextView getMakeModel() {
        return makeModel;
    }

    public TextView getInspected() {
        return inspected;
    }

    public TextView getRegistrationNumber() {
        return registrationNumber;
    }

    public TextView getRequestDate() {
        return requestDate;
    }

    public TextView getInsurer() {
        return insurer;
    }

    public TextView getPremium() {
        return premium;
    }

    public TextView getBookingDate() {
        return bookingDate;
    }

    public TextView getCancelMessage() {
        return cancelMessage;
    }

    public LinearLayout getInspectedLayout() {
        return inspectedLayout;
    }

    public LinearLayout getCancelledLayout() {
        return cancelledLayout;
    }

    public Button getDownloadPolicy() {
        return downloadPolicy;
    }

    public String getPolicyUrl() {
        return policyUrl;
    }

    public void setPolicyUrl(String policyUrl) {
        this.policyUrl = policyUrl;
    }

    public Context getContext() {
        return context;
    }

    public View getBottomDivider() {
        return bottomDivider;
    }

    public void setInsurerName(String insurerName) {
        this.insurerName = insurerName;
    }

    public void setMakeModelVersion(String makeModelVersion) {
        this.makeModelVersion = makeModelVersion;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public ImageView getMakeIcon() {
        return makeIcon;
    }

    @Override
    public void onClick(View view) {
        switch (0) {
            case R.id.downloadPolicy:/*
                String endPoint = policyUrl.substring(0, policyUrl.lastIndexOf("/"));
                String fileName = policyUrl.substring(policyUrl.lastIndexOf("/"));*/
                File directory = new File(Environment.getExternalStorageDirectory(), "Gaadi Gcloud/Insurance Docs");
                if (!directory.exists())
                    if (!directory.mkdirs()) {
                        GCLog.e("directory can't be created");
                        return;
                    }

                String fileName = requestNo + "_" + makeModelVersion + "_" + insurerName + policyUrl.substring(policyUrl.lastIndexOf("."));
                fileName = fileName.replaceAll(" ", "_");

                GCLog.e("filePath: " + directory.getAbsolutePath() + "/" + fileName);

                File file = new File(directory, fileName);
                if (!file.exists()) {
                    /*Ion.with(getContext())
                            .load(getPolicyUrl())
                            .progressDialog(new ProgressDialog(getContext()))
                            .write(new File(directory, fileName))
                            .setCallback(new FutureCallback<File>() {
                                @Override
                                public void onCompleted(Exception e, File result) {
                                    GCLog.e("file: " + result.getAbsolutePath());
                                }
                            });*/
                    new FileDownLoadAsyncTask(getContext(), file).execute(getPolicyUrl());
                } else {
                    openFile(file);
                }

                break;

            default:
                break;
        }
    }

    private void openFile(File file) {
        Intent fileOpenIntent = new Intent(Intent.ACTION_VIEW);
        fileOpenIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
        fileOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

//                    Intent intent = Intent.createChooser(target, "Open File");
        try {
            getContext().startActivity(Intent.createChooser(fileOpenIntent, "Open File"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class FileDownLoadAsyncTask extends AsyncTask<String, Integer, File> {

        private Context context;
        private File file;
        private ProgressDialog dialog;

        public FileDownLoadAsyncTask(Context context, File file) {
            this.context = context;
            this.file = file;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Downloading file. Please wait...");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);

            if (!((Activity) context).isFinishing() && !dialog.isShowing()) {
                dialog.show();
            }
        }

        @Override
        protected File doInBackground(String... strings) {
            int count;
            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return this.file;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (dialog != null) {
                dialog.dismiss();
            }
            if (file == null) {
                GCLog.e("file not saved");
            } else {
                GCLog.e("file is saved");
                openFile(file);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (dialog != null) {
                dialog.setProgress(values[0]);
            }
        }
    }
}
