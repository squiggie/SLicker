package slicker.com.slicker.Controller.API;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.R;

public class MyPhotosAsyncTask extends AsyncTask<String, String, String> {

    private Context mContext;
    private MyInterfaces.OnGetMyPhotos mListener;
    private SwipeRefreshLayout mSwipeContainer;

    public MyPhotosAsyncTask(Context context, MyInterfaces.OnGetMyPhotos listener, SwipeRefreshLayout swipe) {
        mContext = context;
        mListener = listener;
        mSwipeContainer = swipe;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            OAuth10aService service = new ServiceBuilder()
                    .apiKey(MyConstants.API_KEY)
                    .apiSecret(MyConstants.API_SEC)
                    .build(FlickrApi.instance());
            Token accessToken = new Token(params[0],params[1]);
            OAuthRequest request = new OAuthRequest(Verb.GET, MyConstants.PROTECTED_RESOURCE_URL, service);
            request.addQuerystringParameter("method",MyConstants.FLICKR_METHOD_PEOPLE_PHOTOS);
            request.addQuerystringParameter("format","json");
            request.addQuerystringParameter("api_key",MyConstants.API_KEY);
            request.addQuerystringParameter("nojsoncallback","1");
            request.addQuerystringParameter("user_id",params[2]);
            request.addQuerystringParameter("page", params[3]);
            service.signRequest(accessToken, request);
            Response response = request.send();
            return response.getBody();
        } catch (Exception e){
            return "Error";
        }
    }

    @Override
    protected void onPostExecute(String response){
        if (response.contains("error") || response.contains("fail")){
            Toast.makeText(mContext, R.string.basic_error, Toast.LENGTH_LONG).show();
            if (mSwipeContainer != null && mSwipeContainer.isRefreshing()){
                mSwipeContainer.setRefreshing(false);
            }
        } else {
            mListener.onGetMyPhotos(response);
        }
    }
}
