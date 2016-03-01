package slicker.com.slicker.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import slicker.com.slicker.Adapters.PhotoAdapter;
import slicker.com.slicker.Controller.API.Api;
import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Controller.RecyclerOnScrollListener;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class InterestingPhotoFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener,MyInterfaces.RecyclerViewClickListener{

    private PhotoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    private int mNumOfPages = 100;
    private int mCurrentPage = 0;
    //private SmoothProgressBar mSmoothProgress;

    public InterestingPhotoFragment() {
    }


    public static InterestingPhotoFragment newInstance() {
        InterestingPhotoFragment fragment = new InterestingPhotoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interesting_photos, container, false);
        mSwipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeInterestingContainer);
        mSwipeContainer.setOnRefreshListener(this);
        mAdapter = new PhotoAdapter(getActivity(),this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvInteresting);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setAdapter(mAdapter);
        //mSmoothProgress = new SmoothProgressBar(getActivity());
        //mSmoothProgress.setVisibility(View.VISIBLE);

        mRecyclerView.addOnScrollListener(new RecyclerOnScrollListener(lm) {
            @Override
            public void onLoadMore() {
                getPhotos();
            }
        });

        getPhotos();
        return rootView;
    }

    private void getPhotos() {
        if (mCurrentPage < mNumOfPages){
            mSwipeContainer.post(new Runnable() {
                @Override public void run() {
                    mSwipeContainer.setRefreshing(true);
                }
            });
            String url = assembleURL();
            Log.d("Loading","Loading page " + mCurrentPage + " of page " + mNumOfPages);
            Api.get(getActivity()).getJSON(url, new Api.JSONCallback() {
                @Override
                public void onGetJSONComplete(JSONObject json) {
                    try {
                        JSONObject photos = json.getJSONObject("photos");
                        mNumOfPages = photos.getInt("pages");
                        JSONArray items = photos.getJSONArray("photo");
                        for (int i = 0; i < items.length(); i++){
                            Photo photo = new Photo(items.getJSONObject(i));
                            mAdapter.add(photo);
                        }
                        mCurrentPage++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mSwipeContainer != null && mSwipeContainer.isRefreshing()){
                        mSwipeContainer.setRefreshing(false);
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onGetJSONComplete(VolleyError error) {
                    Toast.makeText(getActivity(),R.string.basic_error,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String assembleURL() {
       return String.format(MyConstants.BASE_FLICKR_URL, MyConstants.FLICKR_METHOD_INTERESTING, MyConstants.API_KEY) +  "&page=" + String.valueOf(mCurrentPage + 1);
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 0;
        mNumOfPages = 100;
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        mSwipeContainer.setRefreshing(false);
        getPhotos();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeContainer != null && mSwipeContainer.isRefreshing()){
            mSwipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void recyclerViewMainImageClicked(Photo photo, View v) {
        Intent intent = new Intent(getActivity(),FullScreenActivity.class);
        intent.putExtra("farm", photo.getFarm());
        intent.putExtra("server",photo.getServer());
        intent.putExtra("id", photo.getId());
        intent.putExtra("secret", photo.getSecret());
        intent.putExtra("owner", photo.getOwner());
        intent.putExtra("title",photo.getTitle());
        startActivity(intent);
    }

    @Override
    public void recyclerViewBuddyImageClicked(Photo photo, View v) {

    }

    @Override
    public void recyclerViewFavoriteImageClicked(Photo photo, View v, int position) {

    }
}
