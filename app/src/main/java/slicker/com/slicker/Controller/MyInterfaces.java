package slicker.com.slicker.Controller;


import android.view.View;

import com.github.scribejava.core.model.Token;

import slicker.com.slicker.Model.Photo;

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

    public interface OnUpdateFavorite{
        void onUpdateFavorite(String response);
    }

    public interface RecyclerViewClickListener {
        void recyclerViewMainImageClicked(Photo photo, View v);
        void recyclerViewShareClicked();
        void recyclerViewFavoriteClicked(Photo photo);
        void recyclerViewFavoriteUserClicked();
        void recyclerViewProfileClicked(String userID);
    }

    public interface OnGetMyPhotos{
        void onGetMyPhotos(String response);
    }
}
