package slicker.com.slicker.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import slicker.com.slicker.Controller.Api;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.Model.User;
import slicker.com.slicker.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sp;
    private Realm mRealm;
    private User mUser;
    private Photo mBuddyIcon;
    private File mCacheDir;

    private TextView tvRealName;
    private TextView tvUserName;
    private ImageView imBuddyIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRealm = Realm.getInstance(this);
        sp = getSharedPreferences(MyConstants.SP_KEY,MODE_PRIVATE);
        mCacheDir = new File(getCacheDir(),MyConstants.CACHE_DIR);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        tvRealName = (TextView) header.findViewById(R.id.tvRealName);
        tvUserName = (TextView) header.findViewById(R.id.tvUsername);
        imBuddyIcon = (ImageView) header.findViewById(R.id.buddyIcon);

        getUserDetails();

    }

    private void getUserDetails() {
        String userID = sp.getString(MyConstants.KEY_USER_ID, null);
        RealmQuery<User> query = mRealm.where(User.class);
        query.equalTo("id",userID);
        RealmResults<User> results  = query.findAll();
        if (results.size() > 0) {
            //existing user object
            mUser = results.first();
            displayUserInfo(mUser.getBuddyIconPath());

        } else {

            //No existing user so get from API
            Api.get(this).getUserInfo(userID, new Api.UserInfoCallback() {
                @Override
                public void onUserInfoDownloadComplete(String result) {
                    try {
                        mUser = new User();
                        mBuddyIcon = new Photo();
                        JSONObject json = new JSONObject(result);
                        JSONObject person = json.getJSONObject("person");
                        if (result.contains("realname")) {
                            JSONObject realname = person.getJSONObject("realname");
                            mUser.setRealName(realname.getString("_content"));
                        }
                        mUser.setId(person.getString("id"));
                        mBuddyIcon.setId(person.getString("id"));
                        JSONObject usernameJsonObject = person.getJSONObject("username");
                        mUser.setUsername(usernameJsonObject.getString("_content"));
                        mUser.setIconFarm(person.getInt("iconfarm"));
                        mBuddyIcon.setFarm(person.getInt("iconfarm"));
                        mUser.setIconServer(Integer.valueOf(person.getString("iconserver")));
                        mBuddyIcon.setServer((Integer.valueOf(person.getString("iconserver"))));
                        Api.get(getApplicationContext()).downloadBuddyIcon(mBuddyIcon, mCacheDir, new Api.PhotoCallback() {
                            @Override
                            public void onDownloadComplete(String path) {
                                mUser.setBuddyIconPath(path);
                                saveNewUser();
                                displayUserInfo(path);
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), R.string.basic_error, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onUserInfoDownloadComplete(VolleyError error) {
                    Toast.makeText(getApplicationContext(),R.string.basic_error,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void displayUserInfo(String buddyIconPath) {
        File buddyIcon = new File(buddyIconPath);
        if (buddyIcon.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(buddyIcon.getAbsolutePath());
            imBuddyIcon.setImageBitmap(bitmap);
        }
        tvRealName.setText(mUser.getRealName());
        tvUserName.setText(mUser.getUsername());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_logout:
                logout();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void saveNewUser(){
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(mUser);
        mRealm.commitTransaction();
    }

    private void logout(){
        //delete all shared prefs
        sp.edit().clear().commit();

        //delete all realm users
        RealmResults<User> results = mRealm.where(User.class).findAll();
        mRealm.beginTransaction();
        results.clear();
        mRealm.commitTransaction();

        //delete all file cache

        //send back to Login
        //System.exit(0);
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
