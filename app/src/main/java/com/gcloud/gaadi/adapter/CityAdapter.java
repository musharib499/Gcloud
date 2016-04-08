package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.model.CityData;
import com.gcloud.gaadi.model.ModelVersionViewHolder;

import java.util.ArrayList;

/**
 * Created by ankit on 8/1/15.
 */
public class CityAdapter extends CursorAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<CityData> mItems;
    private Cursor mCursor;
    private ModelVersionViewHolder mHolder;

    public CityAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCursor = cursor;
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

        String version = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.CITY_NAME));

        mHolder.version.setText(version);
    }

}
