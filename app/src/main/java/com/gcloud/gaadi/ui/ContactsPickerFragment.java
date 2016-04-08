package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ContactType;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.ContactOptionSelectedEvent;
import com.gcloud.gaadi.model.ContactsTypeModel;
import com.gcloud.gaadi.utils.GCLog;

import java.util.ArrayList;

/**
 * Created by ankit on 8/1/15.
 */
public class ContactsPickerFragment extends DialogFragment {
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_ITEMS = "ARG_ITEMS";
    private static final String ARG_SELECTED_INDEX = "ARG_SELECTED_INDEX";
    private static final String CONTACTS_TYPE = "CONTACTS_TYPE";
    private static ArrayList<ContactsTypeModel> contactsType = new ArrayList<ContactsTypeModel>();
    private static ShareType shareType;
    private Activity activity;
    private String title, shareText = "";
    private int selectedIndex;
    private String carId;
    private String imageUrl;

    /**
     * Constructor
     */
    public ContactsPickerFragment() {
    }

    /**
     * Create a new instance of ItemPickerDialogFragment with specified arguments
     *
     * @param title         Dialog title text
     * @param selectedIndex initial selection index, or -1 if no item should be pre-selected
     * @return ItemPickerDialogFragment
     */
    public static ContactsPickerFragment newInstance(
            String title,
            int selectedIndex,
            String shareText,
            ShareType sharetype,
            String carId,
            String imageUrl) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        shareType = sharetype;
        args.putSerializable(ARG_ITEMS, getContactTypeOptions());
        args.putInt(ARG_SELECTED_INDEX, selectedIndex);
        args.putString(Constants.SHARE_TEXT, shareText);
        args.putString(Constants.CAR_ID, carId);
        args.putSerializable(Constants.SHARE_TYPE, sharetype);
        args.putSerializable(Constants.IMAGE_URL, imageUrl);

        ContactsPickerFragment fragment = new ContactsPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static ArrayList<ContactsTypeModel> getContactTypeOptions() {
        contactsType.clear();
        if (contactsType.size() == 0) {
            contactsType.add(new ContactsTypeModel(1, ContactType.CALL_LOGS));
            contactsType.add(new ContactsTypeModel(2, ContactType.CONTACTS));
            if (shareType.equals(ShareType.SMS)) {
                contactsType.add(new ContactsTypeModel(3, ContactType.SEND_TO_NUMBER));
            } else if (shareType.equals(ShareType.WHATSAPP)) {
                contactsType.add(new ContactsTypeModel(3, ContactType.NEW_CONTACT));
            }
        }

        return contactsType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        ApplicationController.getEventBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationController.getEventBus().unregister(this);
    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_SELECTED_INDEX, selectedIndex);
        outState.putSerializable(CONTACTS_TYPE, contactsType);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString(ARG_TITLE, "Dialog");
            contactsType = (ArrayList<ContactsTypeModel>) args.getSerializable(ARG_ITEMS);
            selectedIndex = args.getInt(ARG_SELECTED_INDEX, -1);
            shareText = args.getString(Constants.SHARE_TEXT);
            shareType = (ShareType) args.getSerializable(Constants.SHARE_TYPE);
            carId = args.getString(Constants.CAR_ID);
            imageUrl = args.getString(Constants.IMAGE_URL);
        }

        if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt(ARG_SELECTED_INDEX, selectedIndex);
        }

        String[] contactsTypeArray = getContactsTypeArray();

        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());
        //Dialog dialog = new Dialog(getActivity());
        builder.setTitle(title)
                .setCancelable(false)
                .setItems(contactsTypeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedIndex = which;
                        if (getFragmentActivity() instanceof OnContactsOptionSelectedListener) {
                            if (0 <= selectedIndex && selectedIndex < contactsType.size()) {
                                ContactsTypeModel contactType = contactsType.get(selectedIndex);
                                //GCLog.e(Constants.TAG, "contactType: " + contactType.toString());
                                OnContactsOptionSelectedListener listener = (OnContactsOptionSelectedListener) getFragmentActivity();
                                if (listener != null) {
                                    listener.onContactsOptionSelected(
                                            ContactsPickerFragment.this,
                                            contactType.getContactType(),
                                            selectedIndex,
                                            shareText,
                                            shareType,
                                            carId,
                                            imageUrl);
                                }
                            }
                        } else if (getFragmentActivity() instanceof DealerPlatformActivity) {
                            if (0 <= selectedIndex && selectedIndex < contactsType.size()) {
                                ContactsTypeModel contactType = contactsType.get(selectedIndex);
                                GCLog.e("contactType: " + contactType.toString());

                                ApplicationController.getEventBus().post(new ContactOptionSelectedEvent(contactType.getContactType(),
                                        selectedIndex,
                                        shareText,
                                        shareType,
                                        carId));
                            }
                        } else if (getFragmentActivity() instanceof StocksActivity) {
                            if (0 <= selectedIndex && selectedIndex < contactsType.size()) {
                                ContactsTypeModel contactType = contactsType.get(selectedIndex);
                                GCLog.e("contactType: " + contactType.toString());

                                ApplicationController.getEventBus().post(new ContactOptionSelectedEvent(contactType.getContactType(),
                                        selectedIndex,
                                        shareText,
                                        shareType,
                                        carId, imageUrl));
                            }
                        }
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private String[] getContactsTypeArray() {
        final int itemCount = contactsType.size();
        String[] contactsTypeArray = new String[itemCount];
        for (int i = 0; i < itemCount; ++i) {
            contactsTypeArray[i] = contactsType.get(i).getContactType().name().replace("_", " ");
        }
        return contactsTypeArray;
    }


    /**
     * Interface for notification of item selection
     * <p/>
     * If the owning Activity implements this interface, then the fragment will
     * invoke its onItemSelected() method when the user clicks the OK button.
     */
    public interface OnContactsOptionSelectedListener {
        void onContactsOptionSelected(
                ContactsPickerFragment fragment,
                ContactType contactType,
                int index,
                String shareText,
                ShareType shareType,
                String carId,
                String imageUrl);

    }


}
