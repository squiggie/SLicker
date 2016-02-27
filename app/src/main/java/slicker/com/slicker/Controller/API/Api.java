package slicker.com.slicker.Controller.API;

/**
 * Created by squiggie on 2/24/16.
 */
import android.content.Context;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import slicker.com.slicker.Model.MyConstants;
import slicker.com.slicker.R;

public class Api {

    private static Api API;
    private final String sizeKey;
    private static final Map<Integer, String> EDGE_TO_SIZE_KEY = new HashMap<Integer, String>() {{
        put(75, "s");
        put(100, "t");
        put(150, "q");
        put(240, "m");
        put(320, "n");
        put(640, "z");
        put(1024, "b");
    }};
    private final Downloader downloader;
    private static final List<Integer> SORTED_SIZE_KEYS = new ArrayList<Integer>(EDGE_TO_SIZE_KEY.size());

    static {
        SORTED_SIZE_KEYS.addAll(EDGE_TO_SIZE_KEY.keySet());
        Collections.sort(SORTED_SIZE_KEYS);
    }

    private static String getSizeKey(int width, int height) {
        final int largestEdge = Math.max(width, height);
        String result = EDGE_TO_SIZE_KEY.get(SORTED_SIZE_KEYS.get(SORTED_SIZE_KEYS.size() - 1));
        for (int edge : SORTED_SIZE_KEYS) {
            if (largestEdge <= edge) {
                result = EDGE_TO_SIZE_KEY.get(edge);
                break;
            }
        }
        return result;
    }

    public interface JSONCallback{
        public void onGetJSONComplete(JSONObject json);
        public void onGetJSONComplete(VolleyError error);
    }

    public interface UserInfoCallback{
        public void onUserInfoDownloadComplete(String result);
        public void onUserInfoDownloadComplete(VolleyError error);
    }

    public static Api get(Context applicationContext) {
        if (API == null) {
            API = new Api(applicationContext, applicationContext.getResources().getDimensionPixelSize(R.dimen.large_photo_size));
        }
        return API;
    }

    protected Api(Context applicationContext, int maxPhotoSize) {
        this.downloader = Downloader.get(applicationContext);
        this.sizeKey = getSizeKey(maxPhotoSize, maxPhotoSize);
    }

    private static String getUrlForMethod(String method) {
        return String.format(MyConstants.SIGNED_API_URL, method);
    }

    private static String getUserInfo (String userID){
        return getUrlForMethod("flickr.people.getinfo") + "&user_id=" + userID;
    }

    public void getJSON(String url, final JSONCallback cb){
        downloader.download(url, new Downloader.StringCallback() {
            @Override
            public void onDownloadReady(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    cb.onGetJSONComplete(json);
                } catch (JSONException e) {
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.onGetJSONComplete(error);
            }
        });
    }

    public void getUserInfo(String userID, final UserInfoCallback user){
        downloader.download(getUserInfo(userID), new Downloader.StringCallback() {
            @Override
            public void onDownloadReady(String result) {
                user.onUserInfoDownloadComplete(result);
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                user.onUserInfoDownloadComplete(error);
            }
        });
    }
}
