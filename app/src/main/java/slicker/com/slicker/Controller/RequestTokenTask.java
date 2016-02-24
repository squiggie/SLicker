package slicker.com.slicker.Controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import java.net.URL;

import slicker.com.slicker.Helper.FlickrHelper;

/**
 * Created by squiggie on 2/23/16.
 */
public class RequestTokenTask extends AsyncTask<Void, Integer, String> {

    private static final String OAUTH_CALLBACK_URI = "slicker://oauth";
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private MyInterfaces.OnSaveOAuthTokenSecret mListener;

    public RequestTokenTask(Context context, MyInterfaces.OnSaveOAuthTokenSecret listener) {
        super();
        this.mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContext,"", "Generating the authorization request...");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                RequestTokenTask.this.cancel(true);
            }
        });
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Flickr f = FlickrHelper.getInstance().getFlickr();
            OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(OAUTH_CALLBACK_URI.toString());
            URL requestURL = f.getOAuthInterface().buildAuthenticationUrl(Permission.READ, oauthToken);
            saveTokenSecrent(oauthToken.getOauthTokenSecret());
            return requestURL.toString();
        } catch (Exception e) {
            Log.d("Error to oauth", e.getMessage());
            return "error:" + e.getMessage();
        }
    }

    private void saveTokenSecrent(String tokenSecret) {
        mListener.onSaveOAuthTokenSecret(tokenSecret);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if ((result != null) && !result.startsWith("error")) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result)));
        } else {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }
}
