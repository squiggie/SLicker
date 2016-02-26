package slicker.com.slicker.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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
import slicker.com.slicker.Controller.RecyclerOnScrollListener;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class InterestingPhotoFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private PhotoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    private int mNumOfPages = 100;
    private int mCurrentPage = 0;
    private ProgressDialog mProgressDialog;
    public InterestingPhotoFragment() {
    }


    public static InterestingPhotoFragment newInstance() {
        InterestingPhotoFragment fragment = new InterestingPhotoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interesting_photos, container, false);
        mSwipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeInterestingContainer);
        mSwipeContainer.setOnRefreshListener(this);
        mSwipeContainer.setRefreshing(true);
        mAdapter = new PhotoAdapter(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvInteresting);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setAdapter(mAdapter);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Getting Interesting Photos");
        mProgressDialog.dismiss();

        mRecyclerView.addOnScrollListener(new RecyclerOnScrollListener(lm) {
            @Override
            public void onLoadMore() {
                getPhotos();
            }
        });

        mProgressDialog.show();
        getPhotos();
        return rootView;
    }

    private void getPhotos() {
        if (mCurrentPage < mNumOfPages){
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
                    if (mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                    mAdapter.notifyDataSetChanged();
                    mSwipeContainer.setRefreshing(false);
                }

                @Override
                public void onGetJSONComplete(VolleyError error) {
                    Toast.makeText(getActivity(),R.string.basic_error,Toast.LENGTH_LONG).show();
                    mSwipeContainer.setRefreshing(false);
                }
            });
        }
    }

    private String assembleURL() {
       return String.format(MyConstants.BASE_FLICKR_URL, MyConstants.FLICKR_METHOD_INTERESTING, MyConstants.API_KEY) +  "&page=" + String.valueOf(mCurrentPage + 1);
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
    public void onRefresh() {
        mCurrentPage = 0;
        mNumOfPages = 100;
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        mSwipeContainer.setRefreshing(true);
        getPhotos();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
