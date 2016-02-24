package slicker.com.slicker.Helper;

import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by squiggie on 2/23/16.
 */
public final class FlickrHelper {

    private static FlickrHelper instance = null;
    private static final String API_KEY = "1fc23d5d959ae5c917c963ceed83e493";
    public static final String API_SEC = "038c3d980b655413";

    private FlickrHelper() {
    }

    public static FlickrHelper getInstance() {
        if (instance == null) {
            instance = new FlickrHelper();
        }
        return instance;
    }

    public Flickr getFlickr() {
        try {
            Flickr f = new Flickr(API_KEY, API_SEC, new REST());
            return f;
        } catch (ParserConfigurationException e) {
            return null;
        }
    }

    public OAuth10aService getService(){
        OAuth10aService service = new ServiceBuilder()
                .apiKey(API_KEY)
                .apiSecret(API_SEC)
                .build(FlickrApi.instance());
        return service;
    }


}

