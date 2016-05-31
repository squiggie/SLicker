package slicker.com.slicker.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmQuery;
import slicker.com.slicker.Adapters.PhotoAdapter;
import slicker.com.slicker.Controller.API.Api;
import slicker.com.slicker.Controller.API.UpdateFavoritesAsyncTask;
import slicker.com.slicker.Controller.EndlessRecyclerViewScrollListener;
import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class InterestingPhotoFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener,MyInterfaces.RecyclerViewClickListener, MyInterfaces.OnUpdateFavorite{

    private PhotoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    private int mNumOfPages = 100;
    private int mCurrentPage = 0;
    private Realm mRealm;
    private Photo mFavorite;

    public InterestingPhotoFragment() {
    }


    public static InterestingPhotoFragment newInstance() {
        InterestingPhotoFragment fragment = new InterestingPhotoFragment();
        return fragment;
    }

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_interesting_photos, container, false);
         mRealm = Realm.getInstance(getContext());
         mSwipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeInterestingContainer);
         mSwipeContainer.setOnRefreshListener(this);
         mAdapter = new PhotoAdapter(getActivity(),this);
         mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvInteresting);

         getActivity().setTitle("Flickr Interesting Photos");
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
                    if (mSwipeContainer != null && mSwipeContainer.isRefreshing()){
                        mSwipeContainer.setRefreshing(false);
                    }
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
        /*Intent intent = new Intent(getActivity(),FullScreenActivity.class);
        intent.putExtra("farm", photo.getFarm());
        intent.putExtra("server",photo.getServer());
        intent.putExtra("id", photo.getId());
        intent.putExtra("secret", photo.getSecret());
        intent.putExtra("owner", photo.getOwner());
        intent.putExtra("title",photo.getTitle());
        startActivity(intent);*/
    }

    @Override
    public void recyclerViewShareClicked() {

    }

    @Override
    public void recyclerViewFavoriteClicked(Photo photo) {
        mFavorite = photo;
        SharedPreferences sp = getActivity().getSharedPreferences(MyConstants.SP_KEY, Context.MODE_PRIVATE);
        String token = sp.getString(MyConstants.KEY_OAUTH_TOKEN,null);
        String secret = sp.getString(MyConstants.KEY_TOKEN_SECRET,null);

        if (photo.getIsFavorite()){
            UpdateFavoritesAsyncTask updateFavoriteAsyncTask = new UpdateFavoritesAsyncTask(getContext(), this);
            updateFavoriteAsyncTask.execute(token,secret,"remove",photo.getId());
        } else {
            UpdateFavoritesAsyncTask updateFavoriteAsyncTask = new UpdateFavoritesAsyncTask(getContext(), this);
            updateFavoriteAsyncTask.execute(token, secret, "add", photo.getId());
        }
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

    @Override
    public void onUpdateFavorite(String response) {
        if (response.contains("Photo is owned by you")){
            Toast.makeText(getContext(), "Can't favorite you're own photo.", Toast.LENGTH_LONG).show();
        } else if (response.contains("error") || response.contains("fail")){
            Toast.makeText(getContext(), R.string.basic_error, Toast.LENGTH_LONG).show();
        } else {
            toggleFavorite();
        }
    }

    private void toggleFavorite(){
        RealmQuery<Photo> query = mRealm.where(Photo.class);
        query.equalTo("id",mFavorite.getId());
        Photo favorite = query.findFirst();

        if (mFavorite.getIsFavorite()){
            //unfavorite
            mFavorite.setIsFavorite(false);
            //remove from realm
            if (query.count() > 0){
                mRealm.beginTransaction();
                favorite.removeFromRealm();
                mRealm.commitTransaction();
            }
        } else {
            //favorite
            mFavorite.setIsFavorite(true);
            //add to realm
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(mFavorite);
            mRealm.commitTransaction();
        }
    }
}
