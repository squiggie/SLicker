package slicker.com.slicker.Controller;

import android.os.AsyncTask;

import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

/**
 * Created by squiggie on 2/24/16.
 */
public class UserInfoTask extends AsyncTask<String, Integer, String> {
    private static final String PROTECTED_RESOURCE_URL = "https://api.flickr.com/services/rest/";
    private MyInterfaces.OnGetUserInfo mListener;

    public UserInfoTask(MyInterfaces.OnGetUserInfo listener){
        mListener = listener;
    }
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        OAuth10aService service = new ServiceBuilder()
                .apiKey(params[0])
                .apiSecret(params[1])
                .build(FlickrApi.instance());
        OAuthRequest request = new OAuthRequest(Verb.GET,PROTECTED_RESOURCE_URL,service);
        Token accessToken = new Token(params[2],params[3]);
        request.addQuerystringParameter("method","flickr.test.login");
        request.addQuerystringParameter("format","json");
        service.signRequest(accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    @Override
    protected void onPostExecute(String response) {
        mListener.onGetUserInfo(response);
    }
}
