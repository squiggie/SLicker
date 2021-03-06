package slicker.com.slicker.Controller.API;

import android.content.Context;
import android.os.AsyncTask;

import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import slicker.com.slicker.Controller.MyInterfaces;
import slicker.com.slicker.Model.MyConstants;

public class UpdateFavoritesAsyncTask extends AsyncTask<String, String, String> {

    private Context mContext;
    private MyInterfaces.OnUpdateFavorite mListener;

    public UpdateFavoritesAsyncTask(Context context, MyInterfaces.OnUpdateFavorite listener) {
        mContext = context;
        mListener = listener;
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
            if (params[2].contains("add")){
                request.addQuerystringParameter("method",MyConstants.FLICKR_METHOD_ADDFAVORITE);
            } else if (params[2].contains("remove")){
                request.addQuerystringParameter("method",MyConstants.FLICKR_METHOD_REMOVEFAVORITE);
            }
            request.addQuerystringParameter("format","json");
            request.addQuerystringParameter("api_key",MyConstants.API_KEY);
            request.addQuerystringParameter("nojsoncallback","1");
            request.addQuerystringParameter("photo_id",params[3]);
            service.signRequest(accessToken, request);
            Response response = request.send();
            return response.getBody();
        } catch (Exception e){
            return "Error";
        }
    }

    @Override
    protected void onPostExecute(String response){
        mListener.onUpdateFavorite(response);
    }
}
