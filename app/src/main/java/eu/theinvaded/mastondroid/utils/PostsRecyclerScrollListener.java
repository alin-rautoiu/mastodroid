package eu.theinvaded.mastondroid.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by alin on 11.01.2017.
 */

public abstract class PostsRecyclerScrollListener extends RecyclerView.OnScrollListener {
    private int visibleItemCount;
    private int totalItemCount;
    private int firstVisibleItemIndex;
    private boolean loading;
    private int previousTotal;
    LinearLayoutManager layoutManager;

    public PostsRecyclerScrollListener(LinearLayoutManager layoutManager) {
        loading = false;
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItemIndex = layoutManager.findFirstVisibleItemPosition();

        //synchronize loading state when item count changes
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= firstVisibleItemIndex && totalItemCount != visibleItemCount) {
            // Loading NOT in progress and end of list has been reached
            loading = true;
            previousTotal = totalItemCount;
            loadData();
        }
    }

    protected abstract void loadData();
}
