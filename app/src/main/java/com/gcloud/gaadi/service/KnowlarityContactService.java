package com.gcloud.gaadi.service;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.crashlytics.android.Crashlytics;
import com.gcloud.gaadi.model.InsuranceAvailabilityModel;
import com.gcloud.gaadi.utils.CommonUtils;

import java.util.ArrayList;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class KnowlarityContactService extends IntentService {

    final private String CONTACT_ID = "contactId";

    public KnowlarityContactService() {
        super("KnowlarityContactService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //GCLog.e("KnowlarityContactService onHandleIntent");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            //GCLog.e("extras not null");
            ArrayList<InsuranceAvailabilityModel.KnowlarityNumberModel> contactList =
                    (ArrayList<InsuranceAvailabilityModel.KnowlarityNumberModel>) extras.get("contacts");
            if (contactList != null && !contactList.isEmpty()) {
                //GCLog.e("arraylist not empty size: "+contactList.size());
                if (CommonUtils.getLongSharedPreference(this, CONTACT_ID, 0) == 0) {
                    //GCLog.e("shared preference is null");
                    Cursor dataQuery = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " = ?",
                            new String[]{"CarDekho Leads"},
                            null);
                    if (dataQuery != null && dataQuery.moveToFirst()) {
                        //GCLog.e("contact present");
                        CommonUtils.setLongSharedPreference(this,
                                CONTACT_ID,
                                dataQuery.getLong(dataQuery.getColumnIndexOrThrow(ContactsContract.Data.RAW_CONTACT_ID)));
                    } else {
                        try {
                            createContact();
                        } catch (NullPointerException e) {
                            Crashlytics.logException(e.getCause());
                            return;
                        }
                    }
                    //GCLog.e("rawContactid");
                } else {
                    Cursor rawContactVerify = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.RAW_CONTACT_ID + " = ?",
                            new String[]{String.valueOf(CommonUtils.getLongSharedPreference(this, CONTACT_ID, 0))},
                            null);

                    if (rawContactVerify != null) {
                        if (!rawContactVerify.moveToFirst()) {
                            try {
                                createContact();
                            } catch (NullPointerException e) {
                                Crashlytics.logException(e.getCause());
                                return;
                            } catch (IllegalStateException ex) {
                                Crashlytics.logException(ex);
                                return;
                            }
                        }
                        rawContactVerify.close();
                    }
                }
                long rawContactId = CommonUtils.getLongSharedPreference(this, CONTACT_ID, 0);
                //GCLog.e("rawContactid: "+rawContactId);

                for (InsuranceAvailabilityModel.KnowlarityNumberModel model : contactList) {
                    Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " +
                                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                            new String[]{String.valueOf(rawContactId), model.getNewNumber()},
                            null);
                    if (cursor != null) {
                        if (cursor.getCount() > 0) {
                            //GCLog.e("contact exists");
                            cursor.close();
                            continue;
                        } else {
                            Cursor cursor1 = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                                    null,
                                    ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " +
                                            ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                                    new String[]{String.valueOf(rawContactId), model.getPastNumber()},
                                    null);
                            if (cursor1 != null) {
                                if (cursor1.getCount() > 0) {
                                    ContentValues values = new ContentValues();
                                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, model.getNewNumber());
                                    getContentResolver().update(ContactsContract.Data.CONTENT_URI,
                                            values,
                                            ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " +
                                                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                                            new String[]{String.valueOf(rawContactId), model.getPastNumber()});
                                } else {
                                    ContentValues values = new ContentValues();
                                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, model.getNewNumber());
                                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
                                    values.put(ContactsContract.CommonDataKinds.Phone.LABEL, getCount(rawContactId));
                                    getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                                }
                                cursor1.close();
                            }
                        }
                        cursor.close();
                    }
                }
            }
        }
    }

    private String getCount(long rawContactId) {
        String label = "Mobile ";
        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.RAW_CONTACT_ID + " = ?",
                new String[]{String.valueOf(rawContactId)},
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                label += "" + cursor.getCount();
                cursor.close();
            }
        }
        return label;
    }

    private void createContact() throws NullPointerException, IllegalStateException {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, "");
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, "");
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "CarDekho Leads");
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        //GCLog.e("contact created");
        CommonUtils.setLongSharedPreference(this, CONTACT_ID, rawContactId);
    }
}
