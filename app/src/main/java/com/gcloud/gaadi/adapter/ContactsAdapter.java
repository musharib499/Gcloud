package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.model.ContactsViewHolder;

/**
 * Created by ankit on 9/1/15.
 */
public class ContactsAdapter extends CursorAdapter {

    private Context mContext;
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private ContactsViewHolder mHolder;

    public ContactsAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        mContext = context;
        mCursor = cursor;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.contact_list_item, parent, false);
        mHolder = new ContactsViewHolder();
        mHolder.contactName = (TextView) view.findViewById(R.id.contactName);
        mHolder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
        view.setTag(mHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mHolder = (ContactsViewHolder) view.getTag();

        String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        mHolder.contactNumber.setText(contactNumber);
        mHolder.contactName.setText(contactName);

    }
}
