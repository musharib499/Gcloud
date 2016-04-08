package com.gcloud.gaadi.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.LeadsPageAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ContactType;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.ShareTypeEvent;
import com.gcloud.gaadi.interfaces.CallLogsItemClickInterface;
import com.gcloud.gaadi.interfaces.OnContactSelectedListener;
import com.gcloud.gaadi.model.CallLogItem;
import com.gcloud.gaadi.model.ContactListItem;
import com.gcloud.gaadi.ui.viewpagerindicator.BaseActivityCollapsingToolbar;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.gcloud.gaadi.utils.GShareToUtil;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

/**
 * Created by Seema Pandit on 14-01-2015.
 */
public class LeadsActivity extends BaseActivityCollapsingToolbar implements
        ContactsPickerFragment.OnContactsOptionSelectedListener, CallLogsItemClickInterface, OnContactSelectedListener, View.OnClickListener {
    int selectedIndex = 0;
    //private ActionBar mActionBar;
    private Bundle args;
    private ShareTypeEvent shareTypeEvent;
    //    private GAHelper mGAHelper;
    private String mobileNumber = "";
    private boolean smsReceiverRegistered = false;
    private String carId = "";
    private RelativeLayout similarTab, sentTab, allTab;
    private TextView similar, sent, all;
    private GShareToUtil mGShareToUtil;
    private HashMap<Integer, String> adapterTypeMap = new HashMap<Integer, String>() {
        {
            put(Constants.OPENLIST_SIMILAR, "Similar Cars");
            put(Constants.OPENLIST_SENTCARS, "Sent Cars");
            put(Constants.OPENLIST_ALLCARS, "All Cars");
        }
    };

    @Override
    public void onBackPressed() {

        /*if (smsReceiverRegistered) {
            unregisterReceiver(sendSMSReceiver);
        }*/
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.STOCKS_CONTACT_LIST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();

                Cursor cursor = getContentResolver().query(contactUri,
                        new String[]{ContactsContract.Contacts._ID},
                        null, null, null);
                cursor.moveToFirst();

                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                //phones.moveToFirst();

                if (phones.getCount() >= 1) {
                    while (phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //GCLog.e(Constants.TAG, "number: " + number);
                        try {
                            mGShareToUtil.sendSMSHelp(number, shareTypeEvent.getShareText(), shareTypeEvent.getCarId());
                        } catch (Exception e) {
                            CommonUtils.showToast(this, "Could not send SMS. Invalid phone number.", Toast.LENGTH_SHORT);
                        }
                        int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                // do something with the Home number here...
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                // do something with the Mobile number here...
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                // do something with the Work number here...
                                break;
                        }
                    }
                } else {
                    CommonUtils.showToast(this, "Cannot send SMS. No contact number present", Toast.LENGTH_SHORT);

                }
                phones.close();

                //GCLog.e(Constants.TAG, "cursor details: " + cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                cursor.close();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGShareToUtil = new GShareToUtil(this);
      /*  getLayoutInflater().inflate(R.layout.activity_leads,frameLayout);
        mGShareToUtil = new GShareToUtil(this);

        //mActionBar = getSupportActionBar();
//        mGAHelper = new GAHelper(this);
        //mActionBar.setDisplayHomeAsUpEnabled(true);

        similarTab = (RelativeLayout) findViewById(R.id.similarTab);
        similarTab.setOnClickListener(this);
        similar = (TextView) findViewById(R.id.similar);

        sentTab = (RelativeLayout) findViewById(R.id.sentTab);
        sentTab.setOnClickListener(this);
        sent = (TextView) findViewById(R.id.sent);

        allTab = (RelativeLayout) findViewById(R.id.allTab);
        allTab.setOnClickListener(this);
        all = (TextView) findViewById(R.id.all);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new LeadsPageAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(this);
        setCurrentTab(0);*/
        setTabLayout();

    }

    public void setTabLayout() {
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText("Suggested"));
        tabLayout.addTab(tabLayout.newTab().setText("Sent"));
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        // adapter = new DealerPlatformAdapter(this.getSupportFragmentManager());
        // stocksPagerAdapter = new StocksPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(new LeadsPageAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        fab.setVisibility(View.GONE);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        ApplicationController.getInstance().getGAHelper().sendScreenName(GAHelper.TrackerName.APP_TRACKER, Constants.CATEGORY_SIMILAR_CARS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe
    public void onShareTypeSelected(final ShareTypeEvent event) {

        ContactsPickerFragment contactsPickerFragment;
        Bundle args = new Bundle();
        this.carId = event.getCarId();
        switch (event.getShareType()) {
            case SMS:

                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.ACTION_TAP,
                        adapterTypeMap.get(event.getFragmentType()) + "-" + Constants.LABEL_ALL_CARS_SMS,
                        0);

                GCLog.e("Share type sms");

                if ((event.getMobileNumber() != null) && !event.getMobileNumber().isEmpty()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    //final AlertDialog alertDialog = builder.create();

                    builder.setTitle(event.getLeadName() + "-" + event.getMobileNumber());
                    builder.setMessage(R.string.send_sms);
                    builder.setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    try {
                                        mGShareToUtil.sendSMSHelp(event.getMobileNumber(), event.getShareText(), event.getCarId());
                                    } catch (Exception e) {
                                        CommonUtils.showToast(LeadsActivity.this, "Could not send SMS. Invalid number", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                    builder.setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {


                                }
                            });

                    AlertDialog alertDialog = builder.create();

                    if (!alertDialog.isShowing() && !(LeadsActivity.this.isFinishing())) {
                        alertDialog.show();
                    }

                } else {

                    contactsPickerFragment = ContactsPickerFragment.newInstance(
                            getString(R.string.select_contact_from),
                            selectedIndex,
                            event.getShareText(),
                            event.getShareType(),
                            event.getCarId(),
                            event.getImageURL()
                    );

                    contactsPickerFragment.show(getSupportFragmentManager(), "contacts-picker-fragment");
                }

                break;

            case WHATSAPP:

                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.ACTION_TAP,
                        adapterTypeMap.get(event.getFragmentType()) + "-" + Constants.LABEL_ALL_CARS_WHATSAPP,
                        0);

                GCLog.e("Share type whatsapp");
                /*contactsPickerFragment = ContactsPickerFragment.newInstance(
                        getString(R.string.select_contact_from),
                        selectedIndex,
                        event.getShareText(),
                        event.getShareType(),
                    event.getCarId()
                );

                contactsPickerFragment.show(getSupportFragmentManager(), "contacts-picker-fragment");*/
                shareTypeEvent = event;
                mGShareToUtil.showContactsDialog(shareTypeEvent.getShareText(), shareTypeEvent.getShareType(), shareTypeEvent.getCarId(), shareTypeEvent.getImageURL());
                //sendMessageInWhatsApp(event.getShareText(), event.getCarId());
                break;

        }
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
        selectedIndex = index;
        this.carId = carId;

        switch (contactType) {
            case CALL_LOGS:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CALL_LOGS,
                        0);
                mGShareToUtil.showCallLogsDialog(shareText, shareType, carId, imageUrl);
                break;

            case CONTACTS:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_CONTACTS,
                        0);
                mGShareToUtil.showContactsDialog(shareText, shareType, carId, imageUrl);
                break;
        }
    }

    private boolean checkContactPresence(String mobileNumber, String leadName) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(mobileNumber));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        ApplicationController.getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getEventBus().unregister(this);
    }

    @Override
    public void onCallLogSelected(CallLogItem callLogItem, ShareType shareType, String shareText, String carId, String imageUrl) {
        GCLog.e("call log item: " + callLogItem.toString() + ", sharetype = " + shareType.name() + ", sharetext = " + shareText);
        mobileNumber = callLogItem.getGaadiFormatNumber();
        if (shareType == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(callLogItem.getName()) || (callLogItem.getName() == null)) {
                //showAddNameToContactDialog(callLogItem.getNumber(), shareText, carId);

            } else {
                mGShareToUtil.sendMessageInWhatsApp(shareText, carId);
            }

        } else if (shareType == ShareType.SMS) {

            try {
                mGShareToUtil.sendSMSHelp(callLogItem.getNumber(), shareText, carId);
            } catch (Exception e) {
                CommonUtils.showToast(this, "Could not send SMS. Invalid number", Toast.LENGTH_SHORT);
            }
        }
    }

    /*private BroadcastReceiver sendSMSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    mGAHelper.sendEvent(GAHelper.TrackerName.APP_TRACKER,
                            Constants.CATEGORY_SIMILAR_CARS,
                            Constants.CATEGORY_SIMILAR_CARS,
                            Constants.LABEL_SMS_SENT,
                            String.valueOf(CommonUtils.getIntSharedPreference(LeadsActivity.this, Constants.DEALER_ID, -1)),
                            0);

                    makeServerCallForSharedItem(ShareType.SMS);

                    CommonUtils.showToast(getBaseContext(), "SMS sent",
                            Toast.LENGTH_SHORT);
                    break;

                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    CommonUtils.showToast(getBaseContext(), "Could not send SMS.",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    CommonUtils.showToast(getBaseContext(), "No service",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    CommonUtils.showToast(getBaseContext(), "PDU is empty",
                            Toast.LENGTH_SHORT);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    CommonUtils.showToast(getBaseContext(), "Radio off",
                            Toast.LENGTH_SHORT);
                    break;
            }
        }
    };*/

   /* private void makeServerCallForSharedItem(ShareType shareType) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.CAR_ID, carId);
        params.put(Constants.MOBILE_NUM, mobileNumber);
        params.put(Constants.SHARE_TYPE, shareType.name());
        params.put(Constants.SHARE_SOURCE, Constants.CATEGORY_SIMILAR_CARS);

        ShareCarsRequest shareCarsRequest = new ShareCarsRequest(LeadsActivity.this,
                Request.Method.POST,
                Constants.getWebServiceURL(this),
                params,
                new Response.Listener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        ApplicationController.getInstance().addToRequestQueue(shareCarsRequest);
    }*/

    @Override
    public void onContactSelected(ContactListItem contactListItem, String shareText, ShareType shareType, String carId, String imageUrl) {
        mobileNumber = contactListItem.getGaadiFormatNumber();
        if (shareType == ShareType.WHATSAPP) {
            if ("null".equalsIgnoreCase(contactListItem.getContactName()) || (contactListItem.getContactName() == null)) {
        /*showAddNameToContactDialog(
            contactListItem.getContactNumber(),
            shareText,
            carId);*/
            } else {
                mGShareToUtil.sendMessageInWhatsApp(shareText, carId);
            }


        }/* else if (shareType == ShareType.SMS) {
      sendSMS(contactListItem.getContactNumber(), shareText, carId);
    }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.similarTab:
                setCurrentTab(0);
                viewPager.setCurrentItem(0);
                break;

            case R.id.sentTab:
                setCurrentTab(1);
                viewPager.setCurrentItem(1);
                break;

            case R.id.allTab:
                setCurrentTab(2);
                viewPager.setCurrentItem(2);
                break;
        }
    }

    private void setCurrentTab(int i) {
        switch (i) {
            case 0:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SIMILAR_CARS,
                        0);
                similarTab.setSelected(true);
                similar.setTextAppearance(this, R.style.text_bold);

                sentTab.setSelected(false);
                sent.setTextAppearance(this, R.style.text_normal);

                allTab.setSelected(false);
                all.setTextAppearance(this, R.style.text_normal);
                break;

            case 1:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_SENT_CARS,
                        0);
                similarTab.setSelected(false);
                similar.setTextAppearance(this, R.style.text_normal);

                sentTab.setSelected(true);
                sent.setTextAppearance(this, R.style.text_bold);

                allTab.setSelected(false);
                all.setTextAppearance(this, R.style.text_normal);
                break;

            case 2:
                ApplicationController.getInstance().getGAHelper().sendEvent(GAHelper.TrackerName.APP_TRACKER,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.CATEGORY_SIMILAR_CARS,
                        Constants.ACTION_TAP,
                        Constants.LABEL_ALL_CARS,
                        0);
                similarTab.setSelected(false);
                similar.setTextAppearance(this, R.style.text_normal);

                sentTab.setSelected(false);
                sent.setTextAppearance(this, R.style.text_normal);

                allTab.setSelected(true);
                all.setTextAppearance(this, R.style.text_bold);
                break;
        }
    }


}
