package slicker.com.slicker.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by squiggie on 2/24/16.
 */
public class Photo {
    public final String id;
    public final String owner;
    public final String title;
    public final String server;
    public final String farm;
    public final String secret;
    public Photo(JSONObject jsonPhoto) throws JSONException {
        this.id = jsonPhoto.getString("id");
        this.owner = jsonPhoto.getString("owner");
        this.title = jsonPhoto.optString("title", "");
        this.server = jsonPhoto.getString("server");
        this.farm = jsonPhoto.getString("farm");
        this.secret = jsonPhoto.getString("secret");
    }
}
