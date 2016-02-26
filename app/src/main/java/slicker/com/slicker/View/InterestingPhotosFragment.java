package slicker.com.slicker.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

import slicker.com.slicker.Adapters.GridAdapter;
import slicker.com.slicker.Controller.Api;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class InterestingPhotosFragment extends android.support.v4.app.Fragment {
    private static OnFragmentInteractionListener mListener;

    private GridAdapter adapter;
    private RecyclerView gridView;

    public InterestingPhotosFragment() {
    }


    public static InterestingPhotosFragment newInstance(OnFragmentInteractionListener listener) {
        InterestingPhotosFragment fragment = new InterestingPhotosFragment();
        mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interesting_photos, container, false);
        adapter = new GridAdapter(getActivity());
        gridView = (RecyclerView) rootView.findViewById(R.id.interestingRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        gridView.setLayoutManager(llm);
        gridView.setAdapter(adapter);

        //Get list of photos
        Api.get(getActivity()).getJSON("https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&format=json&nojsoncallback=1&api_key=1fc23d5d959ae5c917c963ceed83e493&per_page=100&page=1", new Api.JSONCallback() {
            @Override
            public void onGetJSONComplete(JSONObject json) {
                try {
                    JSONObject photos = json.getJSONObject("photos");
                    JSONArray items = photos.getJSONArray("photo");
                    for (int i = 0; i < items.length(); i++){
                        Photo photo = new Photo(items.getJSONObject(i));
                        adapter.add(photo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onGetJSONComplete(VolleyError error) {
                Toast.makeText(getActivity(),R.string.basic_error,Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
