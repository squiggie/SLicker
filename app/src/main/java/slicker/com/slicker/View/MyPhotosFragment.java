package slicker.com.slicker.View;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import slicker.com.slicker.Adapters.PhotoAdapter;
import slicker.com.slicker.Controller.API.MyPhotosAsyncTask;
import slicker.com.slicker.Controller.EndlessRecyclerViewScrollListener;
import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class MyPhotosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyInterfaces.OnGetMyPhotos, MyInterfaces.RecyclerViewClickListener {

    private PhotoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    private int mNumOfPages = 100;
    private int mCurrentPage = 0;
    private String mUserID;
    private String mToken;
    private String mSecret;

    public MyPhotosFragment() {
    }

    public static MyPhotosFragment newInstance() {
        MyPhotosFragment fragment = new MyPhotosFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_my_photos, container, false);
        mSwipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeMyPhotosContainer);
        mSwipeContainer.setOnRefreshListener(this);
        mAdapter = new PhotoAdapter(getActivity(),this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvMyPhotos);
        getActivity().setTitle("My Photos");

        StaggeredGridLayoutManager sglm = null;
        LinearLayoutManager llm = null;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            sglm = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(sglm);
        } else {
            llm = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
            mRecyclerView.setLayoutManager(llm);
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }
        });

        getPhotos();

        return rootView;
    }

    private void getPhotos(){
        if (mCurrentPage <= mNumOfPages){
            mSwipeContainer.post(new Runnable() {
                @Override public void run() {
                    mSwipeContainer.setRefreshing(true);
                }
            });
            getSharedPrefsForAsyncTask();
            String page = String.valueOf(mCurrentPage + 1);
            MyPhotosAsyncTask myPhotosAsyncTask = new MyPhotosAsyncTask(getActivity(),this,mSwipeContainer);
            myPhotosAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mToken, mSecret, mUserID, page);
        }
    }

    private void getSharedPrefsForAsyncTask() {
        SharedPreferences sp = getActivity().getSharedPreferences(MyConstants.SP_KEY, Context.MODE_PRIVATE);
        mUserID = sp.getString(MyConstants.KEY_USER_ID,null);
        mToken = sp.getString(MyConstants.KEY_OAUTH_TOKEN,null);
        mSecret = sp.getString(MyConstants.KEY_TOKEN_SECRET,null);
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 0;
        mNumOfPages = 100;
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
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
    public void onGetMyPhotos(String response) {
        try {
            JSONObject json = new JSONObject(response);
            JSONObject photos = json.getJSONObject("photos");
            mNumOfPages = photos.getInt("pages");
            mCurrentPage = photos.getInt("page");
            JSONArray items = photos.getJSONArray("photo");
            for (int i = 0; i < items.length(); i++){
                Photo photo = new Photo(items.getJSONObject(i));
                mAdapter.add(photo);
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), R.string.basic_error, Toast.LENGTH_LONG).show();
            if (mSwipeContainer != null && mSwipeContainer.isRefreshing()){
                mSwipeContainer.setRefreshing(false);
            }
        }
        mAdapter.notifyDataSetChanged();
        mSwipeContainer.setRefreshing(false);
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
        startActivity(intent);
    }

    @Override
    public void recyclerViewShareClicked() {

    }

    @Override
    public void recyclerViewFavoriteClicked(Photo photo) {

    }

    @Override
    public void recyclerViewFavoriteUserClicked() {

    }

    @Override
    public void recyclerViewProfileClicked(String userID) {
        Intent intent = new Intent(getContext(),UserActivity.class);
        intent.putExtra("user_id",userID);
        startActivity(intent);
    }

}
