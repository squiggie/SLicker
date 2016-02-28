package slicker.com.slicker.Model;

/**
 * Created by squiggie on 2/25/16.
 */
public class MyConstants {

    public static final String KEY_OAUTH_TOKEN = "oauthToken";
    public static final String KEY_TOKEN_SECRET = "tokenSecret";
    public static final String KEY_USER_NAME = "slicker-userName";
    public static final String KEY_USER_ID = "slicker-userId";
    public static final String SP_KEY = "slicker_sp";
    public static final String API_KEY = "1fc23d5d959ae5c917c963ceed83e493";
    public static final String API_SEC = "038c3d980b655413";
    public static final String CALLBACK_SCHEME = "slicker";
    public static final String BUDDY_ICON_URL = "http://farm%s.staticflickr.com/%s/buddyicons/%s.jpg";
    public static final String IMAGE_URL = "https://farm%s.staticflickr.com/%s/%s_%s_%s.jpg";
    public static final String BASE_FLICKR_URL = "https://api.flickr.com/services/rest/?method=%s&format=json&nojsoncallback=1&api_key=%s";
    public static final String FLICKR_METHOD_INTERESTING = "flickr.interestingness.getList";
    public static final String FLICKR_METHOD_FAVORITES = "flickr.favorites.getList";
    public static final String FLICKR_METHOD_MYPHOTOS = "flickr.people.getPhotos";
    public static final String FLICKR_METHOD_PEOPLE_GETINFO = "flickr.people.getInfo";
    public static final String PROTECTED_RESOURCE_URL = "https://api.flickr.com/services/rest/";
    public static final String SIGNED_API_URL = "https://api.flickr.com/services/rest/?method=%s&format=json&nojsoncallback=1&api_key=" + API_KEY;

}
