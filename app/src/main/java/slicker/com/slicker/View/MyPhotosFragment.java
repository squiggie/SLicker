package slicker.com.slicker.View;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import slicker.com.slicker.Adapters.PhotoAdapter;
import slicker.com.slicker.Controller.API.MyPhotosAsyncTask;
import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Controller.RecyclerOnScrollListener;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class MyPhotosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyInterfaces.OnGetMyPhotos, MyInterfaces.OnRecyclerViewClickListener {

    private PhotoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    private int mNumOfPages = 100;
    private int mCurrentPage = 0;
    private String mUserID;
    private String mToken;
    private String mSecret;
    private ProgressDialog mProgressDialog;

    public MyPhotosFragment() {
    }

    public static MyPhotosFragment newInstance() {
        return new MyPhotosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_my_photos, container, false);
        mSwipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeMyPhotosContainer);
        mSwipeContainer.setOnRefreshListener(this);
        mAdapter = new PhotoAdapter(getActivity(),this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvMyPhotos);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setAdapter(mAdapter);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Getting My Photos");
        mProgressDialog.dismiss();

        mRecyclerView.addOnScrollListener(new RecyclerOnScrollListener(lm) {
            @Override
            public void onLoadMore() {
                getPhotos();
            }
        });

        getPhotos();

        return rootView;
    }

    private void getPhotos(){
        if (mCurrentPage <= mNumOfPages){
            mProgressDialog.show();
            getSharedPrefsForAsyncTask();
            String page = String.valueOf(mCurrentPage + 1);
            MyPhotosAsyncTask myPhotosAsyncTask = new MyPhotosAsyncTask(getActivity(),this, mProgressDialog);
            myPhotosAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mToken,mSecret,mUserID,page);
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
            mProgressDialog.dismiss();
        }
        mAdapter.notifyDataSetChanged();
        mSwipeContainer.setRefreshing(false);
        if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onCircleImageClick(Photo photo) {

    }

    @Override
    public void onMainImageClick(Photo photo) {
        Intent intent = new Intent(getActivity(),FullScreenActivity.class);
        intent.putExtra("farm", photo.getFarm());
        intent.putExtra("server",photo.getServer());
        intent.putExtra("id", photo.getId());
        intent.putExtra("secret", photo.getSecret());
        intent.putExtra("owner", photo.getOwner());
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Photo photo) {
        toggleFavorite(photo);
    }

    private void toggleFavorite(Photo photo){

    }
}
