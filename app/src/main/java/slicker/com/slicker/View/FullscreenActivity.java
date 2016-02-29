package slicker.com.slicker.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.github.jorgecastilloprz.FABProgressCircle;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmQuery;
import slicker.com.slicker.Controller.API.Api;
import slicker.com.slicker.Controller.API.UpdateFavoritesAsyncTask;
import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.R;

public class FullScreenActivity extends AppCompatActivity implements FloatingActionButton.OnClickListener, MyInterfaces.OnUpdateFavorite{
    private Realm mRealm;
    private FloatingActionButton mFABFavorite;
    private FABProgressCircle mFABProgress;
    private CoordinatorLayout mCoordinatorLayout;

    private static Photo mPhoto;
    private String mUserID = "";
    private String mToken = "";
    private String mSecret = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        mRealm = Realm.getInstance(this);

        final ImageView fullScreenImage = (ImageView) findViewById(R.id.imageViewFull);
        final TextView tvUserName = (TextView) findViewById(R.id.tvUserNameCard);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        final CircleImageView buddyIcon = (CircleImageView) findViewById(R.id.buddyIconCard);
        mFABFavorite = (FloatingActionButton) findViewById(R.id.fabFavorite);
        mFABFavorite.setOnClickListener(this);
        mFABProgress = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutFull);

        Bundle bundle = getIntent().getExtras();
        mPhoto = new Photo();
        mPhoto.setId(bundle.getString("id", ""));
        mPhoto.setServer(bundle.getInt("server", 0));
        mPhoto.setFarm(bundle.getInt("farm", 0));
        mPhoto.setSecret(bundle.getString("secret", ""));
        mPhoto.setOwner(bundle.getString("owner", ""));
        mPhoto.setTitle(bundle.getString("title", ""));
        tvTitle.setText(mPhoto.getTitle());

        String url = String.format(MyConstants.IMAGE_URL, mPhoto.getFarm(), mPhoto.getServer(), mPhoto.getId(), mPhoto.getSecret(), "b");

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Downloading Photo");
        dialog.show();

        Glide
            .with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(new ColorDrawable(Color.GRAY))
            .crossFade()
            .into(new ViewTarget<ImageView, GlideDrawable>(fullScreenImage) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                    dialog.dismiss();
                    int width = resource.getIntrinsicWidth();
                    int height = resource.getIntrinsicHeight();
                    if (width >= height) {
                        //set screen rotation horizontal and lock
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    } else {
                        //set screen rotation vertical and lock
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                     }
                    fullScreenImage.setImageDrawable(resource);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    dialog.dismiss();
                }
            });

        //Get and set buddyIcon for each Photo
        String userUrl = String.format(MyConstants.BASE_FLICKR_URL, MyConstants.FLICKR_METHOD_PEOPLE_GETINFO, MyConstants.API_KEY + "&user_id=" + mPhoto.getOwner());
        Api.get(this).getJSON(userUrl, new Api.JSONCallback() {
            @Override
            public void onGetJSONComplete(JSONObject json) {
                try {
                    JSONObject person = json.getJSONObject("person");
                    JSONObject username = person.getJSONObject("username");
                    tvUserName.setText(username.getString("_content"));
                    String buddyIconUrl = String.format(MyConstants.BUDDY_ICON_URL, person.getString("iconfarm"), person.getString("iconserver"), mPhoto.getOwner());
                    Glide.with(getApplicationContext()).load(buddyIconUrl).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(buddyIcon);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.basic_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onGetJSONComplete(VolleyError error) {

            }
        });

        setFavorite();
    }

    private void setFavorite(){
        RealmQuery<Photo> query = mRealm.where(Photo.class);
        query.equalTo("id",mPhoto.getId());
        query.findAll();

        if (query.count() > 0){
            mFABFavorite.setImageResource(R.drawable.ic_star);
            mPhoto.setIsFavorite(true);
        } else {
            mFABFavorite.setImageResource(R.drawable.ic_star_outline);
            mPhoto.setIsFavorite(false);
        }
    }


    @Override
    public void onClick(View v) {
        mFABProgress.show();
        RealmQuery<Photo> query = mRealm.where(Photo.class);
        query.equalTo("id",mPhoto.getId());
        Photo favorite = query.findFirst();
        getSharedPrefsForAsyncTask();
        if (mPhoto.getIsFavorite()){
            //unfavorite
            mPhoto.setIsFavorite(false);
            //remove from realm
            if (query.count() > 0){
                mRealm.beginTransaction();
                favorite.removeFromRealm();
                mRealm.commitTransaction();
            }

            //update api
            UpdateFavoritesAsyncTask updateFavoriteAsyncTask = new UpdateFavoritesAsyncTask(this, this, mFABProgress);
            updateFavoriteAsyncTask.execute(mToken,mSecret,"remove",mPhoto.getId());
        } else {
            //favorite
            mPhoto.setIsFavorite(true);
            //add to realm
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(mPhoto);
            mRealm.commitTransaction();
            //update api
            UpdateFavoritesAsyncTask updateFavoriteAsyncTask = new UpdateFavoritesAsyncTask(this, this, mFABProgress);
            updateFavoriteAsyncTask.execute(mToken, mSecret, "add", mPhoto.getId());
        }
    }

    private void getSharedPrefsForAsyncTask() {
        SharedPreferences sp = getSharedPreferences(MyConstants.SP_KEY, Context.MODE_PRIVATE);
        mUserID = sp.getString(MyConstants.KEY_USER_ID,null);
        mToken = sp.getString(MyConstants.KEY_OAUTH_TOKEN,null);
        mSecret = sp.getString(MyConstants.KEY_TOKEN_SECRET,null);
    }

    @Override
    public void onUpdateFavorite(String response) {
        mFABProgress.hide();
        Snackbar.make(mCoordinatorLayout,"Favorite updated",Snackbar.LENGTH_SHORT).show();
        toggleFAB();
    }

    private void toggleFAB(){
        if (mPhoto.getIsFavorite()){
            mFABFavorite.setImageResource(R.drawable.ic_star);
        } else {
            mFABFavorite.setImageResource(R.drawable.ic_star_outline);
        }
        mFABProgress.hide();
    }
}
