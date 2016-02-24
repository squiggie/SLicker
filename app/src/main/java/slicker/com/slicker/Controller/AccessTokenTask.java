package slicker.com.slicker.Controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;

import slicker.com.slicker.Helper.FlickrHelper;

/**
 * Created by squiggie on 2/23/16.
 */
public class AccessTokenTask extends AsyncTask<String, Integer, OAuth> {

    private Context mContext;
    private MyInterfaces.OnAccessTokenTaskCompleted mListener;

    public AccessTokenTask(Context context, MyInterfaces.OnAccessTokenTaskCompleted listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected OAuth doInBackground(String... params) {
        String oauthToken = params[0];
        String oauthTokenSecret = params[1];
        String verifier = params[2];

        Flickr f = FlickrHelper.getInstance().getFlickr();
        OAuthInterface oauthApi = f.getOAuthInterface();
        try {
            return oauthApi.getAccessToken(oauthToken, oauthTokenSecret, verifier);
        } catch (Exception e) {
            Log.d("Error",e.getLocalizedMessage());
            return null;
        }

    }

    @Override
    protected void onPostExecute(OAuth result) {
        mListener.onAccessTokenTaskCompleted(result);
    }
}

