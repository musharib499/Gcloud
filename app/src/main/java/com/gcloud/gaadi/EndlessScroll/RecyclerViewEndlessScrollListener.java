package com.gcloud.gaadi.EndlessScroll;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ankitgarg on 21/05/15.
 */
public abstract class RecyclerViewEndlessScrollListener extends RecyclerView.OnScrollListener {

    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 7;
    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public RecyclerViewEndlessScrollListener(
            LinearLayoutManager linearLayoutManager,
            int totalItemCount) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.totalItemCount = totalItemCount;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading && totalItemCount > (previousTotal + 1)) {

            previousTotal = totalItemCount;
            loading = false;

        } else if (!loading
                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            loading = true;

            onLoadMore(current_page);
        }
    }

    public abstract void onLoadMore(int nextPageNo);

}
