package slicker.com.slicker.Model;

import java.io.Serializable;

/**
* Created by squiggie on 2/24/16.
*/
public class OAuthToken implements Serializable {
    private static final long serialVersionUID = 1L;
    private String oauthToken;
    private String oauthTokenSecret;

    public OAuthToken() {
    }

    public OAuthToken(String oauthToken, String oauthTokenSecret) {
        this.oauthToken = oauthToken;
        this.oauthTokenSecret = oauthTokenSecret;
    }

    public String getOauthToken() {
        return this.oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getOauthTokenSecret() {
        return this.oauthTokenSecret;
    }

    public void setOauthTokenSecret(String oauthTokenSecret) {
        this.oauthTokenSecret = oauthTokenSecret;
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.oauthToken == null?0:this.oauthToken.hashCode());
        result1 = 31 * result1 + (this.oauthTokenSecret == null?0:this.oauthTokenSecret.hashCode());
        return result1;
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj == null) {
            return false;
        } else if(!(obj instanceof OAuthToken)) {
            return false;
        } else {
            OAuthToken other = (OAuthToken)obj;
            if(this.oauthToken == null) {
                if(other.oauthToken != null) {
                    return false;
                }
            } else if(!this.oauthToken.equals(other.oauthToken)) {
                return false;
            }

            if(this.oauthTokenSecret == null) {
                if(other.oauthTokenSecret != null) {
                    return false;
                }
            } else if(!this.oauthTokenSecret.equals(other.oauthTokenSecret)) {
                return false;
            }

            return true;
        }
    }

    public String toString() {
        return "OAuthToken [oauthToken=" + this.oauthToken + ", oauthTokenSecret=" + this.oauthTokenSecret + "]";
    }
}
