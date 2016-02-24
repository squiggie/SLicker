package slicker.com.slicker.Controller;

import android.content.Context;
import android.os.AsyncTask;

import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth10aService;

/**
 * Created by squiggie on 2/23/16.
 */
public class AccessTokenTask extends AsyncTask<String, Integer, Token> {

    private static final String PROTECTED_RESOURCE_URL = "https://api.flickr.com/services/rest/";

    private Context mContext;
    private MyInterfaces.OnAccessTokenTaskCompleted mListener;

    public AccessTokenTask(Context context, MyInterfaces.OnAccessTokenTaskCompleted listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Token doInBackground(String... params) {
        OAuth10aService service = new ServiceBuilder()
                .apiKey(params[0])
                .apiSecret(params[1])
                .build(FlickrApi.instance());
        Token requestToken = new Token(params[2],params[3]);
        Verifier verifier = new Verifier(params[4]);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        return accessToken;
    }

    @Override
    protected void onPostExecute(Token accessToken) {
        mListener.onAccessTokenTaskCompleted(accessToken);
    }
}

