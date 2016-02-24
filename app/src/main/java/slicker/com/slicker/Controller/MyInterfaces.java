package slicker.com.slicker.Controller;

import com.github.scribejava.core.model.Token;
import com.googlecode.flickrjandroid.oauth.OAuth;

import java.net.URL;

/**
 * Created by squiggie on 2/23/16.
 */
public class MyInterfaces {

    public interface OnAccessTokenTaskCompleted{
        void onAccessTokenTaskCompleted(OAuth result);
    }

    public interface OnSaveOAuthTokenSecret{
        void onSaveOAuthTokenSecret(String tokenSecret);
    }

    public interface OnGetUserInfo{
        void onGetUserInfo(String response);
    }
}
