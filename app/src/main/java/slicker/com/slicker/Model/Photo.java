package slicker.com.slicker.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by squiggie on 2/24/16.
 */
public class Photo {

    private String id;
    private String owner;
    private String title;
    private int server;
    private int farm;
    private String secret;

    public Photo(){

    }

    public Photo(JSONObject jsonPhoto) throws JSONException {
        this.id = jsonPhoto.getString("id");
        this.owner = jsonPhoto.getString("owner");
        this.title = jsonPhoto.optString("title", "");
        this.server = jsonPhoto.getInt("server");
        this.farm = jsonPhoto.getInt("farm");
        this.secret = jsonPhoto.getString("secret");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public int getFarm() {
        return farm;
    }

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
