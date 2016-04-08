package com.gcloud.gaadi.adapter;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcloud.gaadi.ApplicationController;
import com.gcloud.gaadi.R;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.MakeModelVersionViewHolder;

/**
 * Created by ankit on 26/12/14.
 */
public class MakeModelVersionAdapter extends CursorAdapter {

    private Cursor mCursor;
    private Context mContext;
    private LayoutInflater mInflater;
    private MakeModelVersionViewHolder mViewHolder;

    public MakeModelVersionAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        mContext = context;
        mCursor = cursor;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.autocomplete_list_item, parent, false);
        mViewHolder = new MakeModelVersionViewHolder();
        mViewHolder.make = (ImageView) view.findViewById(R.id.make);
        mViewHolder.makeModelVersion = (TextView) view.findViewById(R.id.autosuggest);

        view.setTag(mViewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mViewHolder = (MakeModelVersionViewHolder) view.getTag();

        String makeid = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MAKEID));
        String makeModel = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME));

        int make = Integer.parseInt(makeid);
        mViewHolder.makeModelVersion.setText(makeModel);
        mViewHolder.make.setImageResource(ApplicationController.makeLogoMap.get(make));

    }


}
