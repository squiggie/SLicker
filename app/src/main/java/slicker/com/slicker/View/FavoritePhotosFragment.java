package slicker.com.slicker.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import slicker.com.slicker.Adapters.PhotoAdapter;
import slicker.com.slicker.Controller.API.FavoritePhotosAsyncTask;
import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Controller.RecyclerOnScrollListener;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class FavoritePhotosFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener, MyInterfaces.OnGetFavoritePhotos, MyInterfaces.RecyclerViewClickListener{

        private PhotoAdapter mAdapter;
        private RecyclerView mRecyclerView;
        private SwipeRefreshLayout mSwipeContainer;
        private int mNumOfPages = 100;
        private int mCurrentPage = 0;
        private String mUserID;
        private String mToken;
        private String mSecret;

        public FavoritePhotosFragment() {
        }

    public static FavoritePhotosFragment newInstance() {
        FavoritePhotosFragment fragment = new FavoritePhotosFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_favorite_photos, container, false);
        mSwipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeFavoriteContainer);
        mSwipeContainer.setOnRefreshListener(this);
        mAdapter = new PhotoAdapter(getActivity(),this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvFavorite);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setAdapter(mAdapter);


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
            mSwipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeContainer.setRefreshing(true);
                }
            });
            getSharedPrefsForAsyncTask();
            String page = String.valueOf(mCurrentPage + 1);
            FavoritePhotosAsyncTask favoritePhotosAsyncTask = new FavoritePhotosAsyncTask(getActivity(),this ,mSwipeContainer);
            favoritePhotosAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mToken,mSecret,mUserID,page);
        }
    }

    private void getSharedPrefsForAsyncTask() {
        SharedPreferences sp = getActivity().getSharedPreferences(MyConstants.SP_KEY,Context.MODE_PRIVATE);
        mUserID = sp.getString(MyConstants.KEY_USER_ID,null);
        mToken = sp.getString(MyConstants.KEY_OAUTH_TOKEN,null);
        mSecret = sp.getString(MyConstants.KEY_TOKEN_SECRET,null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeContainer != null && mSwipeContainer.isRefreshing()){
            mSwipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 0;
        mNumOfPages = 100;
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        mSwipeContainer.setRefreshing(true);
        getPhotos();
    }

    @Override
    public void onGetFavoritePhotos(String response) {
        ArrayList<Photo> allPhotos = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(response);
            JSONObject photos = json.getJSONObject("photos");
            mNumOfPages = photos.getInt("pages");
            mCurrentPage = photos.getInt("page");
            JSONArray items = photos.getJSONArray("photo");
            for (int i = 0; i < items.length(); i++){
                Photo photo = new Photo(items.getJSONObject(i));
                photo.setIsFavorite(true);
                allPhotos.add(photo);
                mAdapter.add(photo);
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),R.string.basic_error, Toast.LENGTH_LONG).show();
            if (mSwipeContainer != null && mSwipeContainer.isRefreshing()){
                mSwipeContainer.setRefreshing(false);
            }
        }
        saveFavoritesToRealm(allPhotos);
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
    public void recyclerViewBuddyImageClicked(Photo photo, View v) {

    }

    @Override
    public void recyclerViewFavoriteImageClicked(Photo photo, View v, int position) {

    }

    private void saveFavoritesToRealm(ArrayList<Photo> photos){
        if (getActivity() != null){
            Realm realm = Realm.getInstance(getActivity());
            realm.beginTransaction();
            List<Photo> realmPhotos  = realm.copyToRealmOrUpdate(photos);
            realm.commitTransaction();
        }
    }
}
