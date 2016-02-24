package slicker.com.slicker.Model;

import java.io.Serializable;

/**
 * Created by squiggie on 2/24/16.
 */
public class OAuth implements Serializable {
    private static final long serialVersionUID = 1L;
    private OAuthToken token;
    private User user;

    public OAuth() {
    }

    public OAuthToken getToken() {
        return this.token;
    }

    public void setToken(OAuthToken token) {
        this.token = token;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toString() {
        return "OAuth [token=" + this.token + ", user=" + this.user + "]";
    }
}

