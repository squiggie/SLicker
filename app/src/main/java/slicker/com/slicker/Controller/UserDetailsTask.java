package slicker.com.slicker.Controller;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.googlecode.flickrjandroid.people.User;

import slicker.com.slicker.Helper.FlickrHelper;
import slicker.com.slicker.View.MainActivity;

/**
 * Created by squiggie on 2/24/16.
 */
public class UserDetailsTask  extends AsyncTask<String, Void, String>{

    private static final String PROTECTED_RESOURCE_URL = "http://api.flickr.com/services/rest/";
    private static final String FLICKR_PEOPLE_METHOD = "flickr.people.getinfo";
    private Context mContext;
    private MyInterfaces.OnGetUserInfo mListener;

    public UserDetailsTask(Context context, MyInterfaces.OnGetUserInfo listener){
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            FlickrHelper f = FlickrHelper.getInstance();
            OAuth10aService service = f.getService();
            Token token = new Token(params[1],params[2]);
            OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
            request.addQuerystringParameter("method",FLICKR_PEOPLE_METHOD);
            request.addQuerystringParameter("user_id", params[0]);
            service.signRequest(token, request);
            Response response = request.send();
            return response.getBody();
        } catch (Exception e){
            return "error";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.contains("error")){
            Toast.makeText(mContext,"Oops something went wrong getting user information. Try again!",Toast.LENGTH_LONG).show();
        } else {
            mListener.onGetUserInfo(s);
        }
    }
}
