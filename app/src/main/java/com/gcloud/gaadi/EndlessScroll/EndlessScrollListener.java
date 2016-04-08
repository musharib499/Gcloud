package com.gcloud.gaadi.EndlessScroll;

import android.widget.AbsListView;

/**
 * Created by Lakshay on 11-01-2015.
 */

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 8;
    private int previousTotalCount = 0;
    private boolean loading = true;
    private int currentPage = 1;
    private int startingPageIndex = 1;
    protected int scrollStatechanged = 0;
//    public static boolean responsePrevious = false;

    public abstract void onEndlessScroll(int pageNo);

    public abstract void handleOpenedTuple();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        scrollStatechanged = scrollState;
        if (scrollState == SCROLL_STATE_IDLE) {
            handleOpenedTuple();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        /*GCLog.e("FirstVisibleItem ", firstVisibleItem+" ");
        GCLog.e("VisibleItemCount ", visibleItemCount+" ");
        GCLog.e("TotalItemCount ", totalItemCount+" ");*/


        if (totalItemCount < previousTotalCount) {
//            GCLog.d("Invalid List ","True");
            this.currentPage = this.startingPageIndex;
            this.previousTotalCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        /*GCLog.d("Loading ",loading+" ");
        GCLog.d("PreviousTotal", previousTotalCount+" ");
        GCLog.d("TotalItemCount",totalItemCount+" ");
        GCLog.d("Current Page", currentPage + " ");*/
        if (this.loading && totalItemCount > previousTotalCount + 1) {
            /*GCLog.d("Increment Current Page","True");*/
            this.currentPage++;
            previousTotalCount = totalItemCount;
            this.loading = false;
//            Log.e("EndlessScroll", "listOpenedItem : " + Constants.listOpenedItem);
        }

//        Log.e("EndlessScroll" , "TotalItemCount " + totalItemCount);
//        Log.e("EndlessScroll", "Visible items "+ visibleItemCount);
//        Log.e("EndlessScroll" ,"Firstvisible "+ firstVisibleItem);
        int a = totalItemCount - visibleItemCount;
        int b = firstVisibleItem + visibleThreshold;
        int c = a - b;
//        Log.e("EndlessScroll" ,""+ c );
        if (!this.loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            this.loading = true;
            /*GCLog.d("Load More Data","True");*/
            onEndlessScroll(currentPage);
        }
    }
}