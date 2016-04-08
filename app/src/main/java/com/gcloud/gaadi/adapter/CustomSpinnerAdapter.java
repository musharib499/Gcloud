package com.gcloud.gaadi.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

import com.gcloud.gaadi.db.FinanceDB;
import com.gcloud.gaadi.db.FinanceDBHelper;

/**
 * Created by lakshaygirdhar on 15/10/15.
 */
public class CustomSpinnerAdapter extends SimpleCursorAdapter {

    private AutoCompleteTextView mAutoCompleteTextView;

    public CustomSpinnerAdapter(String column, Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    public CustomSpinnerAdapter(final String column, final AutoCompleteTextView textView, Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mAutoCompleteTextView = mAutoCompleteTextView;

        setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                switch (column) {
                    case FinanceDB.BANK_NAME:
                        return FinanceDBHelper.getBanks(textView.getText().toString());
                    case FinanceDB.COMPANY_NAME:
                        return FinanceDBHelper.getCompanies(textView.getText().toString());
                }
                return null;
            }
        });

        setCursorToStringConverter(new CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndexOrThrow(column));
            }
        });

    }

}
