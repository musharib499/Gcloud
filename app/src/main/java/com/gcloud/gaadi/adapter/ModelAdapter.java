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
import com.gcloud.gaadi.model.ModelVersionViewHolder;

/**
 * Created by ankit on 19/1/15.
 */
public class ModelAdapter extends CursorAdapter {

    private Context mContext;
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private ModelVersionViewHolder mHolder;

    public ModelAdapter(Context context, Cursor cursor) {
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

        String version = cursor.getString(cursor.getColumnIndex(MakeModelVersionDB.MODELNAME));

        mHolder.version.setText(version);
    }
}
