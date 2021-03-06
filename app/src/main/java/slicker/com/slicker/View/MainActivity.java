package slicker.com.slicker.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import slicker.com.slicker.Controller.API.Api;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.Model.User;
import slicker.com.slicker.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences sp;
    private Realm mRealm;
    private User mUser;
    private Toolbar mToolbar;

    private TextView tvRealName;
    private TextView tvUserName;
    private ImageView ivBuddyIcon;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mRealm = Realm.getInstance(this);
        sp = getSharedPreferences(MyConstants.SP_KEY, MODE_PRIVATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        tvRealName = (TextView) header.findViewById(R.id.tvRealName);
        tvUserName = (TextView) header.findViewById(R.id.tvUsername);
        ivBuddyIcon = (ImageView) header.findViewById(R.id.buddyIcon);

        getUserDetails();

        if(null == savedInstanceState){
            Fragment fragment = FavoritePhotosFragment.newInstance();
            replaceFragment(fragment);
        }
    }

    private void getUserDetails() {
        String userID = sp.getString(MyConstants.KEY_USER_ID, null);
        RealmQuery<User> query = mRealm.where(User.class);
        query.equalTo("id", userID);
        RealmResults<User> results  = query.findAll();
        if (results.size() > 0) {
            //existing user object
            mUser = results.first();
            displayUserInfo();

        } else {

            //No existing user so get from API
            Api.get(this).getUserInfo(userID, new Api.UserInfoCallback() {
                @Override
                public void onUserInfoDownloadComplete(String result) {
                    try {
                        mUser = new User();
                        JSONObject json = new JSONObject(result);
                        JSONObject person = json.getJSONObject("person");
                        if (result.contains("realname")) {
                            JSONObject realname = person.getJSONObject("realname");
                            mUser.setRealName(realname.getString("_content"));
                        }
                        mUser.setId(person.getString("id"));
                        JSONObject usernameJsonObject = person.getJSONObject("username");
                        mUser.setUsername(usernameJsonObject.getString("_content"));
                        mUser.setIconFarm(person.getInt("iconfarm"));
                        mUser.setIconServer(Integer.valueOf(person.getString("iconserver")));
                        saveNewUser();
                        displayUserInfo();
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

    private void displayUserInfo() {
        tvRealName.setText(mUser.getRealName());
        tvUserName.setText(mUser.getUsername());
        String buddyIconPath = String.format(MyConstants.BUDDY_ICON_URL, mUser.getIconFarm(), mUser.getIconServer(), mUser.getId());
        Glide.with(this).load(buddyIconPath).asBitmap().into(ivBuddyIcon);
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
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout){
            logout();
        } else if (id == R.id.nav_favorites){
            Fragment fragment = FavoritePhotosFragment.newInstance();
            replaceFragment(fragment);
        } else if (id == R.id.nav_intereesting){
            Fragment fragment = InterestingPhotoFragment.newInstance();
            replaceFragment(fragment);
        } else if (id == R.id.nav_myphotos){
            Fragment fragment = MyPhotosFragment.newInstance();
            replaceFragment(fragment);
        } else {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.llContent,fragment).commit();
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

        //send back to Login
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void setActionBarTitle(String title){
        mToolbar.setTitle(title);
    }
}

