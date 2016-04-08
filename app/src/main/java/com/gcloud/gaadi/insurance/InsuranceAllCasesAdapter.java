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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.InsuranceInspectedCarData;
import com.gcloud.gaadi.utils.GCLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Gaurav on 16-07-2015.
 */
public class InsuranceAllCasesAdapter extends RecyclerView.Adapter<AllCasesHolder> {

    private ArrayList<InsuranceInspectedCarData> dataList;
    private Context context;

    public InsuranceAllCasesAdapter(Context context, ArrayList<InsuranceInspectedCarData> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public AllCasesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AllCasesHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.insurance_all_cases_tuple, parent, false),
                parent.getContext());
    }

    @Override
    public void onBindViewHolder(AllCasesHolder holder, int position) {
        final InsuranceInspectedCarData data = dataList.get(position);
        holder.getInspectedLayout().setVisibility(View.GONE);
        holder.getCancelledLayout().setVisibility(View.GONE);
        holder.getBottomDivider().setVisibility(View.VISIBLE);
//        holder.setPolicyUrl(data.getPolicyDocUrl());
//        holder.setRequestNo(data.getRequestId());
//        holder.setMakeModelVersion(data.getMake() + "_" + data.getModel() + "_" + data.getCarVersion());
//        holder.setInsurerName(data.getInsurer());

        holder.getRequestNumber().setText("Request No. : " + data.getRequestId());
        holder.getMakeModel().setText(data.getModel() + " " + data.getCarVersion());
        holder.getInspected().setText(data.getCarType().toUpperCase());
        holder.getRegistrationNumber().setText("Reg. No. " + data.getRegNo());
        holder.getRequestDate().setText(getFormattedDate(data.getRequestDate()));
        holder.getInsurer().setText(data.getInsurer());
        holder.getPremium().setText(data.getPremium());
        holder.getMakeIcon().setImageResource(ApplicationController.makeLogoMap.get(Integer.valueOf(data.getMakeId())));
        holder.getDownloadPolicy().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(data);
            }
        });

        switch (data.getBookingStatus().toLowerCase()) {
            case "booked":
                holder.getStatus().setText("ISSUED");
                holder.getInspectedLayout().setVisibility(View.VISIBLE);
                holder.getBookingDate().setText(getFormattedDate(data.getBookingDate()));
//                holder.getStatus().setTextColor(holder.getContext().getResources().getColor(R.color.insurance_booked_status));
                holder.getStatus().setBackgroundResource(R.drawable.booked_status);
                break;
            case "cancelled":
                holder.getStatus().setText("CANCELLED");
                holder.getCancelledLayout().setVisibility(View.VISIBLE);
                holder.getCancelMessage().setText(data.getCancelReason());
//                holder.getStatus().setTextColor(holder.getContext().getResources().getColor(R.color.insurance_cancelled_status));
                holder.getStatus().setBackgroundResource(R.drawable.cancelled_status);
                break;
            default:
                holder.getStatus().setText("IN PROCESS");
//                holder.getStatus().setTextColor(holder.getContext().getResources().getColor(R.color.insurance_unbooked_status));
                holder.getStatus().setBackgroundResource(R.drawable.unbooked_status);
                holder.getBottomDivider().setVisibility(View.GONE);
                break;
        }

        switch (data.getCarType().toLowerCase()) {
            case "inspected":
                holder.getInspected().setTextColor(context.getResources().getColor(R.color.insurance_orange));
                holder.getInspected().setBackgroundResource(R.drawable.inspected_bg);
                break;
            default:
                holder.getInspected().setTextColor(context.getResources().getColor(R.color.insurance_middle_gray));
                holder.getInspected().setBackgroundResource(R.drawable.renewal_bg);
                break;
        }
    }

    private String getFormattedDate(String requestDate) {
        String[] date = requestDate.split("\\-");
        return (date[2] + " " + ApplicationController.monthShortMap.get(date[1]) + " " + date[0]);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void resetData(ArrayList<InsuranceInspectedCarData> list) {
        dataList.clear();
        dataList = list;
        notifyDataSetChanged();
    }

    public void downloadFile(InsuranceInspectedCarData data) {
        File directory = new File(Environment.getExternalStorageDirectory(), "Gaadi Gcloud/Insurance Docs");
        if (!directory.exists())
            if (!directory.mkdirs()) {
                GCLog.e("directory can't be created");
                return;
            }

        String fileName = data.getRequestId() + "_" +
                data.getMake() + "_" + data.getModel() + "_" + data.getCarVersion() + "_" +
                data.getInsurer() + data.getPolicyDocUrl().substring(data.getPolicyDocUrl().lastIndexOf("."));
        fileName = fileName.replaceAll(" ", "_");

        //GCLog.e("filePath: " + directory.getAbsolutePath() + "/" + fileName);

        File file = new File(directory, fileName);
        if (!file.exists()) {
            new FileDownLoadAsyncTask(context, file).execute(data.getPolicyDocUrl());
        } else {
            openFile(file);
        }
    }

    private void openFile(File file) {
        Intent fileOpenIntent = new Intent(Intent.ACTION_VIEW);
        fileOpenIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
        fileOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            context.startActivity(Intent.createChooser(fileOpenIntent, "Open File"));
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
            dialog.show();
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
            if (context == null || ((Activity) context).isFinishing()) {    // Fabric #840 by gaurav.kumar@gaadi.com
                return;
            }
            dialog.dismiss();
            if (file != null) {
                openFile(file);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }
    }
}
