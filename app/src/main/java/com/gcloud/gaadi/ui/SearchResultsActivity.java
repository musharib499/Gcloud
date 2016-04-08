package com.gcloud.gaadi.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.gcloud.gaadi.utils.GCLog;

/**
 * Created by ankit on 19/1/15.
 */
public class SearchResultsActivity extends ActionBarActivity {
    private ActionBar mActionBar;
//    private GAHelper mGAHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            GCLog.e("search query = " + query);
        }
    }

}
