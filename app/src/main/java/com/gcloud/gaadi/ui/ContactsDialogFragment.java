package com.gcloud.gaadi.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.ContactsAdapter;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.constants.ShareType;
import com.gcloud.gaadi.events.ContactItemSelectedEvent;
import com.gcloud.gaadi.interfaces.OnContactSelectedListener;
import com.gcloud.gaadi.model.ContactListItem;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by ankit on 8/1/15.
 */
public class ContactsDialogFragment extends DialogFragment
        implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static ContactsDialogFragment mInstance;
    private Activity activity;
    private ListView listView;
    private Cursor mCursor;
    private ContactsAdapter contactsAdapter;
    private String shareText;
    private ShareType shareType;
    private OnContactSelectedListener mListener;
    private String carId;
    private String imageUrl;

    private String[] mProjection = new String[]{
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
    };

    private FilterQueryProvider contactsFilterQueryProvider = new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getCursor(constraint);
        }
    };

    public ContactsDialogFragment() {
    }

    public static ContactsDialogFragment getInstance() {
        if (mInstance == null) {
            mInstance = new ContactsDialogFragment();
        }

        return mInstance;
    }

    private Cursor getCursor(CharSequence constraint) {
        return getFragmentActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                mProjection,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?",
                new String[]{constraint.toString()},
                null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    private Activity getFragmentActivity() {
        return activity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            getLoaderManager().initLoader(Constants.CONTACTS_LOADER, null, this);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_contacts, null, false);
        /*CustomAutoCompleteTextView enterContact = (CustomAutoCompleteTextView) view.findViewById(R.id.enterContact);
        enterContact.setThreshold(1);*/

        listView = (ListView) view.findViewById(R.id.contactsList);
        mCursor = getContactsCursor();
        contactsAdapter = new ContactsAdapter(getActivity(), mCursor);
        listView.setAdapter(contactsAdapter);
        listView.setOnItemClickListener(this);

        //contactsAdapter.setFilterQueryProvider(contactsFilterQueryProvider);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(Constants.SHARE_TEXT) && args.containsKey(Constants.SHARE_TYPE)) {
                shareText = args.getString(Constants.SHARE_TEXT);
                shareType = (ShareType) args.getSerializable(Constants.SHARE_TYPE);
                carId = args.getString(Constants.CAR_ID);
                imageUrl = args.getString(Constants.IMAGE_URL);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getFragmentActivity());
        builder.setView(view);
        builder.setTitle(R.string.select_contact_from);

        if (getFragmentActivity() instanceof OnContactSelectedListener) {
            mListener = (OnContactSelectedListener) getFragmentActivity();
        }

        return builder.create();
    }

    private Cursor getContactsCursor() {
        return getFragmentActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                mProjection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME
                        + " COLLATE LOCALIZED ASC");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
        String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        int type = cursor.getType(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));

        GCLog.e("type = " + type);
        if (contactNumber != null && !contactNumber.isEmpty()) {
            ContactListItem contactListItem = new ContactListItem(contactName, contactNumber);
            if (mListener != null) {
                mListener.onContactSelected(contactListItem, shareText, shareType, carId, imageUrl);

            }

            if (getFragmentActivity() instanceof DealerPlatformActivity) {
                ApplicationController.getEventBus().post(new ContactItemSelectedEvent(contactListItem, shareType, shareText, carId));
            }
            if (getFragmentActivity() instanceof StocksActivity) {
                ApplicationController.getEventBus().post(new ContactItemSelectedEvent(contactListItem, shareType, shareText, carId, imageUrl));
            }

        } else {
            CommonUtils.showToast(getFragmentActivity(), "This doesn't seems to be a valid contact.", Toast.LENGTH_SHORT);
        }
        this.dismiss();


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                mProjection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        contactsAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        contactsAdapter.swapCursor(null);
    }
}
