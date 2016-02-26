package slicker.com.slicker.Controller;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by squiggie on 2/26/16.
 */
public abstract class RecyclerOnScrollListener  extends RecyclerView.OnScrollListener{

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 25;
    private int visibleItemCount, totalItemCount, firstVisibleItem;
    private LinearLayoutManager mLinearLayoutManager;

    public RecyclerOnScrollListener(LinearLayoutManager lm) {
        this.mLinearLayoutManager = lm;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            onLoadMore();
            loading = true;
        }
    }

    public abstract void onLoadMore();
}
