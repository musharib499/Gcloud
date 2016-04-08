package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.ModelVersionViewHolder;

/**
 * Created by ankit on 29/12/14.
 */
public class ModelVersionAdapter extends CursorAdapter {

    private Context mContext;
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private ModelVersionViewHolder mHolder;

    private SparseBooleanArray enabledArray = new SparseBooleanArray();

    public ModelVersionAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        mContext = context;
        mCursor = cursor;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.model_version_autocomplete_list_item, parent, false);
        mHolder = new ModelVersionViewHolder();
        mHolder.version = (TextView) view.findViewById(R.id.version);
        view.setTag(mHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mHolder = (ModelVersionViewHolder) view.getTag();


        String version = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.VERSIONNAME));
        if (version.equalsIgnoreCase("Petrol") || version.equalsIgnoreCase("Diesel") || version.equalsIgnoreCase("CNG") || version.equalsIgnoreCase("LPG") || version.equalsIgnoreCase("Electric") || version.equalsIgnoreCase("Hybrid")) {
            mHolder.version.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            enabledArray.put(cursor.getPosition(), false);
            //clickedView ( here textview )intercepts the click, therefore the list element will not respond to any click because clickedView intercepts them.
            //mHolder.version.setClickable(true);
            //mHolder.version.setEnabled(true);
        } else {
            mHolder.version.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
            enabledArray.put(cursor.getPosition(), true);
           /* if (mHolder.version.isEnabled())
            {
                mHolder.version.setEnabled(false);
            }
            if (mHolder.version.isClickable())
            {
                mHolder.version.setClickable(false);

            }*/

        }
        mHolder.version.setText(version);
    }

    @Override
    public boolean isEnabled(int position) {
        return enabledArray.get(position);
    }
}
