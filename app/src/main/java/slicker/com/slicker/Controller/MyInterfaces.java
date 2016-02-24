package slicker.com.slicker.Controller;


import com.github.scribejava.core.model.Token;

import slicker.com.slicker.Model.OAuth;

/**
 * Created by squiggie on 2/23/16.
 */
public class MyInterfaces {

    public interface OnAccessTokenTaskCompleted{
        void onAccessTokenTaskCompleted(Token accessToken);
    }

    public interface OnSaveOAuthRequestToken {
        void onSaveOauthRequestToken(Token requestToken);
    }

    public interface OnGetUserInfo{
        void onGetUserInfo(String response);
    }
}
