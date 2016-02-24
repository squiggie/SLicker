package slicker.com.slicker.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import slicker.com.slicker.Controller.AccessTokenTask;
import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Controller.RequestTokenTask;
import slicker.com.slicker.R;

public class LoginActivity extends AppCompatActivity implements MyInterfaces.OnAccessTokenTaskCompleted, MyInterfaces.OnSaveOAuthTokenSecret {

    private static final String CALLBACK_SCHEME = "slicker";
    @Bind(R.id.login) Button btnLogin;
    @Bind(R.id.slicker) TextView slicker;
    @Bind(R.id.reset) Button btnReset;

    private SharedPreferences sp;

    public static final String KEY_OAUTH_TOKEN = "oauthToken";
    public static final String KEY_TOKEN_SECRET = "tokenSecret";
    public static final String KEY_USER_NAME = "slicker-userName";
    public static final String KEY_USER_ID = "slicker-userId";
    public static final String SP_KEY = "slicker_sp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sp = getSharedPreferences(SP_KEY,MODE_PRIVATE);

        OAuth oauth = getOAuthToken();
        if (oauth != null) {
            if (oauth.getUser() != null){
                startSlicker();
            }
        }
    }

    @OnClick(R.id.login)
    public void login(){
        RequestTokenTask task = new RequestTokenTask(this,this);
        task.execute();
    }

    @OnClick(R.id.reset)
    public void resetTokens(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_TOKEN_SECRET,null);
        editor.putString(KEY_OAUTH_TOKEN,null);
        editor.putString(KEY_USER_ID, null);
        editor.putString(KEY_USER_NAME,null);
        editor.apply();
    }

    public OAuth getOAuthToken() {
        //Restore preferences
        String oauthTokenString = sp.getString(KEY_OAUTH_TOKEN, null);
        String tokenSecret = sp.getString(KEY_TOKEN_SECRET, null);
        if (oauthTokenString == null && tokenSecret == null) {
            return null;
        }
        OAuth oauth = new OAuth();
        String userName = sp.getString(KEY_USER_NAME, null);
        String userId = sp.getString(KEY_USER_ID, null);
        if (userId != null) {
            User user = new User();
            user.setUsername(userName);
            user.setId(userId);
            oauth.setUser(user);
        }
        OAuthToken oauthToken = new OAuthToken();
        oauth.setToken(oauthToken);
        oauthToken.setOauthToken(oauthTokenString);
        oauthToken.setOauthTokenSecret(tokenSecret);
        return oauth;
    }



    private void startSlicker(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onAccessTokenTaskCompleted(OAuth result) {
        if (result == null) {
            Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show();
        } else {
            User user = result.getUser();
            OAuthToken token = result.getToken();
            if (user == null || user.getId() == null || token == null || token.getOauthToken() == null || token.getOauthTokenSecret() == null) {
                Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show();
                return;
            }
            String message = String.format(Locale.US, "Authorization Succeed: user=%s, userId=%s, oauthToken=%s, tokenSecret=%s", user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            saveOAuthToken(user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
            startSlicker();
        }

    }

    @Override
    public void onSaveOAuthTokenSecret(String tokenSecret) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_TOKEN_SECRET, tokenSecret);
        editor.apply();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        OAuth savedToken = getOAuthToken();
        if (CALLBACK_SCHEME.equals(scheme) && (savedToken == null || savedToken.getUser() == null)) {
            Uri uri = intent.getData();
            String query = uri.getQuery();
            String[] data = query.split("&");

            if (data != null && data.length == 2) {
                String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
                String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1);
                saveOAuthToken(null,null,oauthToken,null);
                OAuth oauth = getOAuthToken();
                if (oauth != null && oauth.getToken() != null && oauth.getToken().getOauthTokenSecret() != null) {
                    AccessTokenTask task = new AccessTokenTask(this,this);
                    task.execute(oauthToken, oauth.getToken().getOauthTokenSecret(), oauthVerifier);
                }
            }
        }
    }

    public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
        SharedPreferences.Editor editor = sp.edit();
        if (token != null) { editor.putString(KEY_OAUTH_TOKEN, token); }
        if (tokenSecret != null){editor.putString(KEY_TOKEN_SECRET, tokenSecret);}
        if (userName != null) {editor.putString(KEY_USER_NAME, userName);}
        if (userId != null) {editor.putString(KEY_USER_ID, userId);}
        editor.commit();
    }


}
