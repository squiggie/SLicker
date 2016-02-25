package slicker.com.slicker.View;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import slicker.com.slicker.Controller.Api;
import slicker.com.slicker.Model.Photo;
import slicker.com.slicker.Model.User;
import slicker.com.slicker.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sp;
    public static final String KEY_USER_ID = "slicker-userId";
    public static final String KEY_OAUTH_TOKEN = "oauthToken";
    public static final String KEY_TOKEN_SECRET = "tokenSecret";
    public static final String SP_KEY = "slicker_sp";
    private Realm mRealm;
    private User mUser;

    private TextView tvRealName;
    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRealm = Realm.getInstance(this);
        sp = getSharedPreferences(SP_KEY,MODE_PRIVATE);

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

        getUserDetails();
    }

    private void getUserDetails() {
        String userID = sp.getString(KEY_USER_ID, null);
        RealmQuery<User> query = mRealm.where(User.class);
        query.equalTo("id",userID);
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
                        if (result.contains("realname")){
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
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void displayUserInfo() {
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

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void saveNewUser(){
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(mUser);
        mRealm.commitTransaction();
    }
}
