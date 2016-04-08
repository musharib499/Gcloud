package com.gcloud.gaadi.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gcloud.gaadi.R.id;
import com.gcloud.gaadi.R.layout;
import com.gcloud.gaadi.R.string;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ContactType;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.ShareTypeEvent;
import com.gcloud.gaadi.interfaces.CallLogsItemClickInterface;
import com.gcloud.gaadi.interfaces.OnContactSelectedListener;
import com.gcloud.gaadi.model.CallLogItem;
import com.gcloud.gaadi.model.ContactListItem;
import com.gcloud.gaadi.model.GeneralResponse;
import com.gcloud.gaadi.model.StockDetailModel;
import com.gcloud.gaadi.retrofit.RetrofitRequest;
import com.gcloud.gaadi.ui.CallLogsDialogFragment;
import com.gcloud.gaadi.ui.ContactsDialogFragment;
import com.gcloud.gaadi.ui.ContactsPickerFragment;
import com.gcloud.gaadi.ui.ContactsPickerFragment.OnContactsOptionSelectedListener;
import com.gcloud.gaadi.utils.GAHelper.TrackerName;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Arun Singh Chauhan on 6/10/2015.
 */
public class GShareToUtil implements OnContactsOptionSelectedListener,
        CallLogsItemClickInterface, OnContactSelectedListener {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    public GAHelper mGAHelper;
    public ShareTypeEvent mShareTypeEvent;
    ContactsPickerFragment contactsPickerFragment;
    String shareText;
    private CallLogsDialogFragment callLogsDialogFragment;
    private String mobileNumber = "";
    private int selectedIndex;
    private Bundle args;
    private String url;
    private StockDetailModel stockDetailModel;
    private String dealerUsername, ucdid, carId = "";
    private FrameLayout layoutContainer, alternativeLayout;
    private TextView errorMessage;
    private Button retry;
    private Intent whatsappIntent;

    public GShareToUtil(Context mContext) {
        this.mContext = mContext;
        this.mGAHelper = new GAHelper(mContext);
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    FragmentManager fetchFragmentManager(Activity context) {
        try {
            return ((FragmentActivity) context).getSupportFragmentManager();
        } catch (ClassCastException e) {
            GCLog.e("GShareToUtil", "Can't get fragment manager");
        }
        return null;
    }

    public void sendSMS(String title, int selectedIndex, ShareTypeEvent event) {
        ContactsPickerFragment contactsPickerFragment;
        contactsPickerFragment = ContactsPickerFragment.newInstance(
                this.mContext.getString(string.select_contact_from),
                selectedIndex,
                event.getShareText(),
                event.getShareType(),
                event.getCarId(),
                event.getImageURL()
        );
        contactsPickerFragment.show(this.fetchFragmentManager((Activity) this.mContext), "contacts-picker-fragment");
    }

    public void sendWhatsapp(String title, int selectedIndex, ShareTypeEvent event) {
        ContactsPickerFragment contactsPickerFragment;
        contactsPickerFragment = ContactsPickerFragment.newInstance(
                //this.mContext.getString(string.select_contact_from),
                title,
                selectedIndex,
                event.getShareText(),
                event.getShareType(),
                event.getCarId(),
                event.getImageURL()
        );
        contactsPickerFragment.show(this.fetchFragmentManager((Activity) this.mContext), "contacts-picker-fragment");
    }


    public void showCallLogsDialog(String shareText, ShareType shareType, String carId, String imageUrl) {
        this.callLogsDialogFragment = CallLogsDialogFragment.getInstance();
        Bundle args = new Bundle();
        args.putString(Constants.SHARE_TEXT, shareText);
        args.putSerializable(Constants.SHARE_TYPE, shareType);
        args.putString(Constants.CAR_ID, carId);
        this.callLogsDialogFragment.setArguments(args);
        this.callLogsDialogFragment.show(this.fetchFragmentManager((Activity) mContext), "call-logs-dialog");

    }

    @Override
    public void onContactSelected(ContactListItem contactListItem, String shareText, ShareType shareType, String carId, String imageUrl) {
        this.mobileNumber = contactListItem.getGaadiFormatNumber();
        if (shareType == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(contactListItem.getContactName()) || contactListItem.getContactName() == null) {
                this.showAddNameToContactDialog(
                        contactListItem.getContactNumber(),
                        shareText,
                        carId);
            } else {
                this.sendMessageInWhatsApp(shareText, carId, imageUrl);
            }
        }
    }

    @Override
    public void onCallLogSelected(CallLogItem callLogItem, ShareType shareType, String shareText, String carId, String imageUrl) {
        this.mobileNumber = callLogItem.getGaadiFormatNumber();
        if (shareType == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(callLogItem.getName()) || callLogItem.getName() == null) {
                this.showAddNameToContactDialog(callLogItem.getNumber(), shareText, carId);

            } else {
                this.sendMessageInWhatsApp(shareText, carId);
            }

        } else if (shareType == ShareType.SMS) {
            try {
                this.sendSMSHelp(callLogItem.getNumber(), shareText, carId);
            } catch (Exception e) {
                CommonUtils.showToast(this.mContext, "Could not send SMS. Invalid number", Toast.LENGTH_SHORT);
            }
        }
    }

    public void showAddNewNumToSendSMSDialog(final String shareText, final String carId) {
        Builder builder = new Builder(this.mContext);

        View view = this.mLayoutInflater.inflate(layout.add_new_number_dialog, null, false);

        final EditText number = (EditText) view.findViewById(id.contactNumber);
        number.requestFocus();
        number.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        GShareToUtil.this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(number, 0);
            }
        }, 200);
        builder.setView(view);

        AlertDialog dialog = builder.setTitle(string.add_number)
                .setPositiveButton(string.done_label, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (number.getText().toString().isEmpty()) {
                            CommonUtils.showToast(GShareToUtil.this.mContext, "Please enter number", Toast.LENGTH_SHORT);
                        } else if (number.getText().toString().length() < 10
                                || !hasValidStarters(number.getText().toString())) {
                            CommonUtils.showToast(GShareToUtil.this.mContext, "Please enter valid number", Toast.LENGTH_SHORT);
                        } else {
                            try {
                                GShareToUtil.this.sendSMSHelp(number.getText().toString(), shareText, carId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton(string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        if (!((Activity) this.mContext).isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }

    }

    private boolean hasValidStarters(String s) {
        ArrayList<String> validStarters = new ArrayList<>(Arrays.asList(new String[]{"7", "8", "9"}));
        return validStarters.contains(String.valueOf(s.charAt(0)));
    }


    private boolean checkValidEmails(String[] allEmails) {
        boolean isValid = false;
        for (int i = 0; i < allEmails.length; i++) {
            isValid = CommonUtils.isEmailValid(allEmails[i]);
            if (!isValid) {
                break;
            }
        }
        return isValid;
    }

    public void showSendEmailDialog(final ShareTypeEvent shareTypeEvent) {
        Builder builder = new Builder(this.mContext);
        View view = this.mLayoutInflater.inflate(layout.layout_add_contact_dialog, null, false);
        final EditText emails = (EditText) view.findViewById(id.contactName);
        emails.setHint("Enter comma separated emails");
        builder.setView(view);
        AlertDialog dialog = builder.setTitle(string.enter_email_ids)
                .setPositiveButton(string.done_label, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (emails.getText().toString().isEmpty()) {
                            CommonUtils.showToast(GShareToUtil.this.mContext, "Please enter email ids", Toast.LENGTH_SHORT);
                        } else {
                            boolean isAllEmailsValid = false;
                            if (emails.getText().toString().contains(",")) {
                                String[] emailIDs = emails.getText().toString().split(",");
                                isAllEmailsValid = GShareToUtil.this.checkValidEmails(emailIDs);
                            } else {
                                isAllEmailsValid = CommonUtils.isEmailValid(emails.getText().toString());
                            }
                            if (!isAllEmailsValid) {
                                CommonUtils.showToast(GShareToUtil.this.mContext, "Please enter valid email ids", Toast.LENGTH_SHORT);

                            } else {
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put(Constants.CAR_ID, shareTypeEvent.getExtraText());
                                params.put(Constants.EMAIL_IDS, emails.getText() + ",");
                                params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
                                params.put(Constants.METHOD_LABEL, Constants.SEND_EMAIL_METHOD);
                                params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));

                                RetrofitRequest.emailRequest(mContext, params, new Callback<GeneralResponse>() {
                                    @Override
                                    public void success(GeneralResponse generalResponse, Response response) {
                                        if ("T".equalsIgnoreCase(generalResponse.getStatus())) {
                                            CommonUtils.showToast(GShareToUtil.this.mContext, "Email sent successfully.", Toast.LENGTH_SHORT);
                                        } else {
                                            CommonUtils.showToast(GShareToUtil.this.mContext, "Sending email failed.", Toast.LENGTH_SHORT);
                                        }
//                                    GCGCLog.e(Constants.TAG, "Response email " + response);

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        if (error.getCause() instanceof UnknownHostException) {
                                            CommonUtils.showToast(GShareToUtil.this.mContext, GShareToUtil.this.mContext.getString(string.network_connection_error), Toast.LENGTH_SHORT);
                                        } else {
                                            CommonUtils.showToast(GShareToUtil.this.mContext, GShareToUtil.this.mContext.getString(string.server_error_message), Toast.LENGTH_SHORT);
                                        }

                                    }
                                });

                             /*   SendEmailRequest emailRequest = new SendEmailRequest(
                                        GShareToUtil.this.mContext,
                                        Method.POST,
                                        Constants.getWebServiceURL(GShareToUtil.this.mContext),
                                        params,
                                        new Listener<GeneralResponse>() {
                                            @Override
                                            public void onResponse(GeneralResponse response) {
                                                if ("T".equalsIgnoreCase(response.getStatus())) {
                                                    CommonUtils.showToast(GShareToUtil.this.mContext, "Email sent successfully.", Toast.LENGTH_SHORT);
                                                } else {
                                                    CommonUtils.showToast(GShareToUtil.this.mContext, "Sending email failed.", Toast.LENGTH_SHORT);
                                                }
//                                    GCGCLog.e(Constants.TAG, "Response email " + response);
                                            }
                                        },
                                        new ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                if (error.getCause() instanceof UnknownHostException) {
                                                    CommonUtils.showToast(GShareToUtil.this.mContext, GShareToUtil.this.mContext.getString(string.network_connection_error), Toast.LENGTH_SHORT);
                                                } else {
                                                    CommonUtils.showToast(GShareToUtil.this.mContext, GShareToUtil.this.mContext.getString(string.server_error_message), Toast.LENGTH_SHORT);
                                                }
                                            }
                                        }
                                );

                                ApplicationController.getInstance().addToRequestQueue(emailRequest, Constants.TAG_EMAIL_REQUEST, false, (OnNoInternetConnectionListener) GShareToUtil.this.mContext);
              */              }
                        }
                    }
                })
                .setNegativeButton(string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();

        if (!((Activity) this.mContext).isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }

    }

    public void showContactsDialog(String shareText, ShareType shareType, String carId, String imageUrl) {
        if (shareType == ShareType.WHATSAPP) {
            this.sendMessageInWhatsApp(shareText, carId, imageUrl);

        } else {

            this.mShareTypeEvent = new ShareTypeEvent(shareType, shareText, carId, imageUrl);
            this.shareText = shareText;
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(Contacts.CONTENT_TYPE);
            GCLog.d("OutSideif");
            if (intent.resolveActivity(this.mContext.getPackageManager()) != null) {
                GCLog.d("InSideif");
                this.args = new Bundle();
                this.args.putString(Constants.SHARE_TEXT, shareText);
                this.args.putSerializable(Constants.SHARE_TYPE, shareType);
                this.args.putString(Constants.CAR_ID, carId);
                GCLog.d("Activity Name = " + mContext);
                ((Activity) this.mContext).startActivityForResult(intent, Constants.STOCKS_CONTACT_LIST);
            }
        }
    }

    public void showCallLogsDialog(String shareText, ShareType shareType, String carId) {

        //if (callLogsDialogFragment == null) { // need to recreate callLogsDialogFragment instance because of SMS/Whatsapp shareType

        this.callLogsDialogFragment = CallLogsDialogFragment.getInstance();
        Bundle args = new Bundle();
        args.putString(Constants.SHARE_TEXT, shareText);
        args.putSerializable(Constants.SHARE_TYPE, shareType);
        args.putString(Constants.CAR_ID, carId);
        this.callLogsDialogFragment.setArguments(args);

        //}

        this.callLogsDialogFragment.show(this.fetchFragmentManager((Activity) mContext), "call-logs-dialog");
        GCLog.e("Share text = " + shareText);
    }

    public void showContactsDialog(String shareText, ShareType shareType, String carId) {
        ContactsDialogFragment contactsDialogFragment = ContactsDialogFragment.getInstance();
        Bundle args = new Bundle();
        args.putString(Constants.SHARE_TEXT, shareText);
        args.putSerializable(Constants.SHARE_TYPE, shareType);
        args.putString(Constants.CAR_ID, carId);
        contactsDialogFragment.setArguments(args);
        contactsDialogFragment.show(this.fetchFragmentManager((Activity) mContext), "contacts-dialog");
    }

    public void sendMessageInWhatsApp(String shareText, String carId) {
        if (this.isWhatsappInstalled("com.whatsapp")) {
            PackageManager pm = this.mContext.getPackageManager();
            try {

                this.mGAHelper.sendEvent(TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_WHATSAPP_SENT,
                        0);
                /*Uri mUri = Uri.parse("smsto:" + number);*/
                final Intent mIntent = new Intent(Intent.ACTION_SEND/*TO, mUri*/);

                mIntent.setPackage("com.whatsapp");
                mIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                mIntent.setType("text/plain");

                if (this.url != null && !this.url.trim().isEmpty()) {
                    GCLog.e("url " + this.url);
                    Glide.with(this.mContext)
                            .load(this.url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new SimpleTarget<GlideDrawable>() {

                                ProgressDialog progressDialog;

                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                    this.progressDialog = new ProgressDialog(GShareToUtil.this.mContext);
                                    this.progressDialog.setIndeterminate(true);
                                    this.progressDialog.setCancelable(true);
                                    this.progressDialog.setTitle("Please Wait");
                                    this.progressDialog.setMessage("Image Download in Progress");
                                    if (!((Activity) GShareToUtil.this.mContext).isFinishing() && !this.progressDialog.isShowing()) {
                                        this.progressDialog.show();
                                    }
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    if (this.progressDialog != null) {
                                        this.progressDialog.dismiss();
                                    }
                                    GShareToUtil.this.makeServerCallForSharedItem(ShareType.WHATSAPP);
                                    GShareToUtil.this.mContext.startActivity(Intent.createChooser(mIntent, "Share with"));
                                    GCLog.e("onloadfailed");

                                }

                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                    GCLog.e("onresourceready");
                                    if (CommonUtils.writeToStorage(GShareToUtil.this.mContext, resource, "share_big_" + GShareToUtil.this.stockDetailModel.getStockId())) {
                                        GCLog.e("write to storage worked");
                                        if (this.progressDialog != null) {
                                            this.progressDialog.dismiss();
                                        }
                                        mIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                                                + "/Gaadi Gcloud", "share_big_" + GShareToUtil.this.stockDetailModel.getStockId() + ".png")));
                                        mIntent.setType("image/png");
                                        GShareToUtil.this.makeServerCallForSharedItem(ShareType.WHATSAPP);
                                        GShareToUtil.this.mContext.startActivity(Intent.createChooser(mIntent, "Share with"));
                                    }

                                }
                            });
                } else {
                    this.makeServerCallForSharedItem(ShareType.WHATSAPP);
                    this.mContext.startActivity(Intent.createChooser(mIntent, "Share with"));
                }


            } catch (Exception e) {
                CommonUtils.showToast(this.mContext, "While sending as WhatsApp message, some unknown error occurred.", Toast.LENGTH_LONG);
            }
        } else {
            CommonUtils.showToast(this.mContext, "WhatsApp not installed.", Toast.LENGTH_SHORT);

        }
    }

    public boolean isWhatsappInstalled(String packageName) {
        PackageManager pm = this.mContext.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            StackTraceElement[] trace = e.getStackTrace();
        }
        return false;
    }

    public void showAddNameToContactDialog(final String contactNumber, final String shareText, final String carId, final String imageUrl) {
        Builder builder = new Builder(this.mContext);
        View view = this.mLayoutInflater.inflate(layout.layout_add_contact_dialog, null, false);
        final EditText name = (EditText) view.findViewById(id.contactName);
        builder.setView(view);

        AlertDialog dialog = builder.setTitle(string.add_contact)
                .setPositiveButton(string.done_label, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (name.getText().toString().isEmpty()) {
                            CommonUtils.showToast(GShareToUtil.this.mContext, "Please enter name", Toast.LENGTH_SHORT);
                        } else {
                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                            ops.add(ContentProviderOperation.newInsert(
                                    RawContacts.CONTENT_URI)
                                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(RawContacts.ACCOUNT_NAME, null)
                                    .build());

                            //------------------------------------------------------ Names
                            if (!name.getText().toString().isEmpty()) {
                                ops.add(ContentProviderOperation.newInsert(
                                        Data.CONTENT_URI)
                                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                                        .withValue(Data.MIMETYPE,
                                                StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(
                                                StructuredName.DISPLAY_NAME,
                                                name.getText().toString()).build());
                            }

                            //------------------------------------------------------ Mobile Number
                            if (contactNumber != null) {
                                ops.add(ContentProviderOperation.
                                        newInsert(Data.CONTENT_URI)
                                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                                        .withValue(Data.MIMETYPE,
                                                Phone.CONTENT_ITEM_TYPE)
                                        .withValue(Phone.NUMBER, contactNumber)
                                        .withValue(Phone.TYPE,
                                                Phone.TYPE_MOBILE)
                                        .build());
                            }

                            try {

                                GShareToUtil.this.mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                CommonUtils.showToast(GShareToUtil.this.mContext, "Contact added to phonebook", Toast.LENGTH_SHORT);
                                GShareToUtil.this.sendMessageInWhatsApp(shareText, carId, imageUrl);

                            } catch (Exception e) {

                                CommonUtils.showToast(GShareToUtil.this.mContext, "Add Contact Failed. Try Again", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                })
                .setNegativeButton(string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        if (!((Activity) this.mContext).isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }

    }

    public void showAddNameToContactDialog(final String shareText, final String carId) {
        Builder builder = new Builder(this.mContext);
        View view = this.mLayoutInflater.inflate(layout.add_new_contact_dialog, null, false);
        final EditText name = (EditText) view.findViewById(id.contactName);
        final EditText number = (EditText) view.findViewById(id.contactNumber);
        name.requestFocus();
        name.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        GShareToUtil.this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(name, 0);
            }
        }, 200);
        builder.setView(view);

        AlertDialog dialog = builder.setTitle(string.add_contact)
                .setPositiveButton(string.done_label, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (name.getText().toString().isEmpty() || number.getText().toString().isEmpty()) {
                            CommonUtils.showToast(GShareToUtil.this.mContext, "Both fields are mandatory", Toast.LENGTH_SHORT);
                        } else {
                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                            ops.add(ContentProviderOperation.newInsert(
                                    RawContacts.CONTENT_URI)
                                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(RawContacts.ACCOUNT_NAME, null)
                                    .build());

                            //------------------------------------------------------ Names
                            if (!name.getText().toString().isEmpty()) {
                                ops.add(ContentProviderOperation.newInsert(
                                        Data.CONTENT_URI)
                                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                                        .withValue(Data.MIMETYPE,
                                                StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(
                                                StructuredName.DISPLAY_NAME,
                                                name.getText().toString()).build());
                            }

                            //------------------------------------------------------ Mobile Number
                            if (!number.getText().toString().isEmpty()) {
                                ops.add(ContentProviderOperation.
                                        newInsert(Data.CONTENT_URI)
                                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                                        .withValue(Data.MIMETYPE,
                                                Phone.CONTENT_ITEM_TYPE)
                                        .withValue(Phone.NUMBER, number.getText().toString())
                                        .withValue(Phone.TYPE,
                                                Phone.TYPE_MOBILE)
                                        .build());
                            }

                            try {

                                GShareToUtil.this.mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                CommonUtils.showToast(GShareToUtil.this.mContext, "Contact added to phonebook", Toast.LENGTH_SHORT);
                                GShareToUtil.this.sendMessageInWhatsApp(shareText, carId);

                            } catch (Exception e) {

                                CommonUtils.showToast(GShareToUtil.this.mContext, "Add Contact Failed. Try Again", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                })
                .setNegativeButton(string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        if (!((Activity) this.mContext).isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }

    }

    public void sendMessageInWhatsApp(String shareText, final String carId, String url) {
        if (this.isWhatsappInstalled("com.whatsapp")) {
            PackageManager pm = this.mContext.getPackageManager();
            try {

                this.mGAHelper.sendEvent(TrackerName.APP_TRACKER,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.CATEGORY_VIEW_STOCK,
                        Constants.ACTION_TAP,
                        Constants.LABEL_WHATSAPP_SENT,
                        0);
                /*Uri mUri = Uri.parse("smsto:" + number);*/
                this.whatsappIntent = new Intent(Intent.ACTION_SEND/*TO, mUri*/);
                this.whatsappIntent.setPackage("com.whatsapp");
                this.whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                this.whatsappIntent.setType("text/plain");
                if (url != null && !url.trim().isEmpty()) {
                    //GCGCLog.e(TAG, "url " + url);
                    File file = new File(Environment.getExternalStorageDirectory(), "/Gaadi Gcloud/" + "share_small_" + carId + ".png");
                    if (file.exists()) {
                        GShareToUtil.this.whatsappIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Gaadi Gcloud", "share_small_" + carId + ".png")));
                        GShareToUtil.this.whatsappIntent.setType("image/png");
                        GShareToUtil.this.makeServerCallForSharedItem(ShareType.WHATSAPP);
                        GShareToUtil.this.mContext.startActivity(Intent.createChooser(GShareToUtil.this.whatsappIntent, "Share with"));
                        return;
                    }
                    Glide.with(this.mContext)
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new SimpleTarget<GlideDrawable>() {

                                ProgressDialog progressDialog;

                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                    this.progressDialog = new ProgressDialog(GShareToUtil.this.mContext);
                                    this.progressDialog.setIndeterminate(true);
                                    this.progressDialog.setCancelable(true);
                                    this.progressDialog.setTitle("Please Wait");
                                    this.progressDialog.setMessage("Image Download in Progress");
                                    if (!((Activity) GShareToUtil.this.mContext).isFinishing() && !this.progressDialog.isShowing()) {
                                        this.progressDialog.show();
                                    }
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    //GCGCLog.e(TAG, "onloadfailed");
                                    if (this.progressDialog != null) {
                                        this.progressDialog.dismiss();
                                    }
                                    GShareToUtil.this.makeServerCallForSharedItem(ShareType.WHATSAPP);
                                    GShareToUtil.this.mContext.startActivity(Intent.createChooser(GShareToUtil.this.whatsappIntent, "Share with"));

                                }

                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                    //GCGCLog.e(TAG, "onresourceready");
                                    if (CommonUtils.writeToStorage(GShareToUtil.this.mContext, resource, "share_small_" + carId)) {
                                        //GCGCLog.e(TAG, "write to storage worked");
                                        if (this.progressDialog != null) {
                                            this.progressDialog.dismiss();
                                        }
                                        GShareToUtil.this.whatsappIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Gaadi Gcloud", "share_small_" + carId + ".png")));
                                        GShareToUtil.this.whatsappIntent.setType("image/png");
                                    }
                                    GShareToUtil.this.makeServerCallForSharedItem(ShareType.WHATSAPP);
                                    GShareToUtil.this.mContext.startActivity(Intent.createChooser(GShareToUtil.this.whatsappIntent, "Share with"));

                                }
                            });
                } else {
                    this.makeServerCallForSharedItem(ShareType.WHATSAPP);
                    this.mContext.startActivity(Intent.createChooser(this.whatsappIntent, "Share with"));

                }


            } catch (Exception e) {
                CommonUtils.showToast(this.mContext, "While sending as WhatsApp message, some unknown error occurred.", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        } else {
            CommonUtils.showToast(this.mContext, "WhatsApp not installed.", Toast.LENGTH_SHORT);

        }
    }

    public void showAddNameToContactDialog(final String contactNumber, final String shareText, final String carId) {
        Builder builder = new Builder(this.mContext);
        View view = this.mLayoutInflater.inflate(layout.layout_add_contact_dialog, null, false);
        final EditText name = (EditText) view.findViewById(id.contactName);
        builder.setView(view);

        AlertDialog dialog = builder.setTitle(string.add_contact)
                .setPositiveButton(string.done_label, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (name.getText().toString().isEmpty()) {
                            CommonUtils.showToast(GShareToUtil.this.mContext, "Please enter name", Toast.LENGTH_SHORT);
                        } else {
                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                            ops.add(ContentProviderOperation.newInsert(
                                    RawContacts.CONTENT_URI)
                                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(RawContacts.ACCOUNT_NAME, null)
                                    .build());

                            //------------------------------------------------------ Names
                            if (!name.getText().toString().isEmpty()) {
                                ops.add(ContentProviderOperation.newInsert(
                                        Data.CONTENT_URI)
                                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                                        .withValue(Data.MIMETYPE,
                                                StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(
                                                StructuredName.DISPLAY_NAME,
                                                name.getText().toString()).build());
                            }

                            //------------------------------------------------------ Mobile Number
                            if (contactNumber != null) {
                                ops.add(ContentProviderOperation.
                                        newInsert(Data.CONTENT_URI)
                                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                                        .withValue(Data.MIMETYPE,
                                                Phone.CONTENT_ITEM_TYPE)
                                        .withValue(Phone.NUMBER, contactNumber)
                                        .withValue(Phone.TYPE,
                                                Phone.TYPE_MOBILE)
                                        .build());
                            }

                            try {

                                GShareToUtil.this.mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                CommonUtils.showToast(GShareToUtil.this.mContext, "Contact added to phonebook", Toast.LENGTH_SHORT);
                                GShareToUtil.this.sendMessageInWhatsApp(shareText, carId);

                            } catch (Exception e) {

                                CommonUtils.showToast(GShareToUtil.this.mContext, "Add Contact Failed. Try Again", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                })
                .setNegativeButton(string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        if (!((Activity) this.mContext).isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }

    }


    public void sendSMSHelp(String number, String shareText, String carId) throws Exception {
        this.carId = carId;
        this.makeServerCallForSharedItem(ShareType.SMS);
        Intent sendIntent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("vnd.android-dir/mms-sms");
        } else {
            sendIntent = new Intent(Intent.ACTION_SENDTO);
        }
        sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendIntent.setData(Uri.parse("smsto:" + number));
        sendIntent.putExtra("address", number);
        sendIntent.putExtra("sms_body", shareText);
        this.mContext.startActivity(sendIntent);
    }

    public void sendSMSHelp(String number) throws Exception {
        carId = this.carId;
        this.makeServerCallForSharedItem(ShareType.SMS);
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendIntent.setData(Uri.parse("smsto:" + number));
        sendIntent.putExtra("address", number);
        sendIntent.putExtra("sms_body", this.shareText);
        sendIntent.setType("vnd.android-dir/mms-sms");
        this.mContext.startActivity(sendIntent);
    }

    private void makeServerCallForSharedItem(ShareType shareType) {
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, this.carId);
        params.put(Constants.MOBILE_NUM, this.mobileNumber);
        params.put(Constants.SHARE_TYPE, shareType.name());
        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_VIEW_STOCK);
        params.put(Constants.API_KEY_LABEL, Constants.API_KEY);
        params.put(Constants.METHOD_LABEL, Constants.SENT_CARS_METHOD);
        params.put(Constants.DEALER_USERNAME, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_USERNAME, ""));
        params.put(Constants.DEALER_PASSWORD, CommonUtils.getStringSharedPreference(mContext, Constants.UC_DEALER_PASSWORD, ""));


        RetrofitRequest.shareCarsRequestwhatsup(mContext, params, new Callback<GeneralResponse>() {
            @Override
            public void success(GeneralResponse generalResponse, Response response) {
                GCLog.e("Dipanshu",params.toString());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

       /* ShareCarsRequest shareCarsRequest = new ShareCarsRequest(this.mContext,
                Method.POST,
                Constants.getWebServiceURL(this.mContext),
                params,
                new Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {

                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);*/
    }

    @Override
    public void onContactsOptionSelected(
            ContactsPickerFragment fragment,
            ContactType contactType,
            int index,
            String shareText,
            ShareType shareType,
            String carId,
            String imageUrl) {
        this.selectedIndex = index;

        switch (contactType) {
            case CALL_LOGS:
                this.mGAHelper.sendEvent(TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CALL_LOGS,
                        0);
                this.showCallLogsDialog(shareText, shareType, carId, imageUrl);
                break;

            case CONTACTS:
                this.mGAHelper.sendEvent(TrackerName.APP_TRACKER,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.CATEGORY_CAR_DETAILS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CONTACTS,
                        0);
                this.showContactsDialog(shareText, shareType, carId, imageUrl);
                break;

            case NEW_CONTACT:
                this.showAddNameToContactDialog(shareText, carId);
                break;

            case SEND_TO_NUMBER:
                this.showAddNewNumToSendSMSDialog(shareText, carId);
                break;
        }
    }
}