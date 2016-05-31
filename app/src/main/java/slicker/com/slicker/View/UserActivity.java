package slicker.com.slicker.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import slicker.com.slicker.Adapters.ProfilePhotoAdapter;
import slicker.com.slicker.Controller.API.Api;
import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.MyItemDecoration;
import slicker.com.slicker.R;

public class UserActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MyInterfaces.RecyclerViewClickListener, Button.OnClickListener{

    private ProfilePhotoAdapter mAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private CircleImageView mBuddyIcon;
    private CollapsingToolbarLayout mCollapsingToolbar;

    private static int mNumOfPages = 100;
    private static int mCurrentPage = 0;
    private String mUserID = "";
    private String mIconFarm = "";
    private String mIconServer = "";
    private String mProfileurl = "";
    private String mPhotourl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBuddyIcon = (CircleImageView) findViewById(R.id.buddyIconUser);
        final TextView tvUserName = (TextView) findViewById(R.id.tvNameUser);
        final TextView tvDescription = (TextView) findViewById(R.id.tvDescription);
        final TextView tvLocation = (TextView) findViewById(R.id.tvLocation);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarUser);
        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainerUser);
        mSwipeContainer.setOnRefreshListener(this);


        //Setup Recyclerview
        MyItemDecoration decoration = new MyItemDecoration(8);
        mAdapter = new ProfilePhotoAdapter(this,this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvUser);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setAdapter(mAdapter);

        Button btFollow = (Button) findViewById(R.id.btFollow);
        btFollow.setOnClickListener(this);
        Button btShare = (Button) findViewById(R.id.btShare);
        btShare.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        mUserID = bundle.getString("user_id", "");
        String userUrl = String.format(MyConstants.BASE_FLICKR_URL,MyConstants.FLICKR_METHOD_PEOPLE_GETINFO,MyConstants.API_KEY) + "&user_id=" + mUserID;

        //Get User Info
        Api.get(this).getJSON(userUrl, new Api.JSONCallback() {
            @Override
            public void onGetJSONComplete(JSONObject json) {
                try {
                    JSONObject person = json.getJSONObject("person");
                    mIconFarm = person.getString("iconfarm");
                    mIconServer = person.getString("iconserver");
                    if (person.has("username")){
                        JSONObject username = person.getJSONObject("username");
                        tvUserName.setText(username.getString("_content"));
                    }
                    if (person.has("description")){
                        JSONObject description = person.getJSONObject("description");
                        String descriptionText = description.getString("_content");
                        descriptionText = descriptionText.replaceAll("<(.*?)\\>"," ");//Removes all items in brackets
                        descriptionText = descriptionText.replaceAll("<(.*?)\\\n"," ");//Must be undeneath
                        descriptionText = descriptionText.replaceFirst("(.*?)\\>", " ");//Removes any connected item to the last bracket
                        descriptionText = descriptionText.replaceAll("&nbsp;"," ");
                        descriptionText = descriptionText.replaceAll("&amp;"," ");
                        //descriptionText = descriptionText.length() > 250 ? descriptionText.substring(0, 250) + "...." : descriptionText;
                        tvDescription.setText(descriptionText);
                    }
                    if (person.has("location")){
                        JSONObject location = person.getJSONObject("location");
                        tvLocation.setText(location.getString("_content"));
                    }
                    if (person.has("profileurl")){
                        JSONObject profileurl = person.getJSONObject("profileurl");
                        mProfileurl = profileurl.getString("_content");
                    } else if (person.has("photosurl")){
                        JSONObject photourl = person.getJSONObject("photosurl");
                        mPhotourl = photourl.getString("_content");
                    }
                    getBuddyIcon();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.basic_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onGetJSONComplete(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.basic_error, Toast.LENGTH_LONG).show();
            }
        });

        getPhotos();
    }

    private void getPhotos() {
        String picturesURL = String.format(MyConstants.BASE_FLICKR_URL,MyConstants.FLICKR_METHOD_PEOPLE_PHOTOS,MyConstants.API_KEY) + "&user_id=" + mUserID;
        Api.get(this).getJSON(picturesURL, new Api.JSONCallback() {
            @Override
            public void onGetJSONComplete(JSONObject json) {
                try {
                    JSONObject photos = json.getJSONObject("photos");
                    JSONArray photo = photos.getJSONArray("photo");
                    mNumOfPages = photos.getInt("pages");
                    mCurrentPage = photos.getInt("page");
                    String photoUrl = String.format(MyConstants.IMAGE_URL, photo.getJSONObject(0).getString("farm"), photo.getJSONObject(0).getString("server"), photo.getJSONObject(0).getString("id"), photo.getJSONObject(0).getString("secret"), "z");
                    Glide.with(getApplicationContext()).load(photoUrl).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            resource.setAlpha(50);
                            mCollapsingToolbar.setBackground(resource);
                        }
                    });
                    for (int i = 0; i < photo.length(); i++) {
                        Photo newPhoto = new Photo(photo.getJSONObject(i));
                        newPhoto.setWidth(150);
                        newPhoto.setHeight((int) (50 + Math.random() * 150));
                        mAdapter.add(newPhoto);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.basic_error, Toast.LENGTH_LONG).show();
                    if (mSwipeContainer != null && mSwipeContainer.isRefreshing()) {
                        mSwipeContainer.setRefreshing(false);
                    }
                }
                if (mSwipeContainer != null && mSwipeContainer.isRefreshing()) {
                    mSwipeContainer.setRefreshing(false);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetJSONComplete(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.basic_error, Toast.LENGTH_LONG).show();
                if (mSwipeContainer != null && mSwipeContainer.isRefreshing()) {
                    mSwipeContainer.setRefreshing(false);
                }
            }
        });
    }

    private void getBuddyIcon(){
        String buddyIconUrl = String.format(MyConstants.BUDDY_ICON_URL, mIconFarm, mIconServer, mUserID);
        Glide.with(this).load(buddyIconUrl).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(mBuddyIcon);
    }


    @Override
    public void recyclerViewMainImageClicked(Photo photo, View v) {
        Intent intent = new Intent(this,FullScreenActivity.class);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btShare:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                if (mProfileurl.isEmpty()){
                    sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this Flickr profile!");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this Flickr profile! " + mPhotourl);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, mPhotourl);
                } else {
                    sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this Flickr profile!");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this Flickr profile! " + mProfileurl);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, mProfileurl);
                }
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share via"));
                break;
            case R.id.btFollow:
                toggleFollow();
                break;
        }
    }

    private void toggleFollow(){

    }
}
