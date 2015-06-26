package co.sihe.apptemplete.views.endlessrecycler;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by hwangjr on 6/26/15.
 */
public abstract class EndlessRecyclerScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessRecyclerScrollListener.class.getSimpleName();

    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 5;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    // The current offset index of data you have loaded
    private int currentPage = 0;

    private LinearLayoutManager linearLayoutManager;

    public EndlessRecyclerScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    public EndlessRecyclerScrollListener(LinearLayoutManager linearLayoutManager, int visibleThreshold) {
        this.linearLayoutManager = linearLayoutManager;
        this.visibleThreshold = visibleThreshold;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        onFindFirstCompletelyVisibleItemPosition(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = linearLayoutManager.getItemCount();
        firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotalItemCount) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            currentPage++;
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);

    public void onFindFirstCompletelyVisibleItemPosition(boolean hasFound) {
    }

    @Override
    public void onScrollStateChanged(RecyclerView view, int scrollState) {
    }
}
