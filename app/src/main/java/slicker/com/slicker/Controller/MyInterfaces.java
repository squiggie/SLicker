package slicker.com.slicker.Controller;


import com.github.scribejava.core.model.Token;


/**
 * Created by squiggie on 2/23/16.
 */
public class MyInterfaces {

    public interface OnAccessTokenTaskCompleted{
        void onAccessTokenTaskCompleted(Token accessToken);
    }

    public interface OnRequestTokenTaskCompleted{
        void onRequestTokenTaskCompleted(String url);
    }

    public interface OnSaveOAuthRequestToken {
        void onSaveOauthRequestToken(Token requestToken);
    }

    public interface OnGetUserInfo{
        void onGetUserInfo(String response);
    }

    public interface OnGetFavoritePhotos{
        void onGetFavoritePhotos(String response);
    }
}
