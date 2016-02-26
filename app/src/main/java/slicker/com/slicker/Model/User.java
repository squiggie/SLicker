package slicker.com.slicker.Model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by squiggie on 2/24/16.
 */
@RealmClass
public class User extends RealmObject{

    @PrimaryKey
    private String id;

    private String username;
    private int iconFarm;
    private int iconServer;
    private String realName;
    private String location;
    private String pathAlias;
    private Date photosFirstDate;
    private Date photosFirstDateTaken;
    private Date faveDate;
    private int photosCount;
    private long filesizeMax;
    private String photosurl;
    private String profileurl;
    private String mobileurl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIconFarm() {
        return iconFarm;
    }

    public void setIconFarm(int iconFarm) {
        this.iconFarm = iconFarm;
    }

    public int getIconServer() {
        return iconServer;
    }

    public void setIconServer(int iconServer) {
        this.iconServer = iconServer;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPathAlias() {
        return pathAlias;
    }

    public void setPathAlias(String pathAlias) {
        this.pathAlias = pathAlias;
    }

    public Date getPhotosFirstDate() {
        return photosFirstDate;
    }

    public void setPhotosFirstDate(Date photosFirstDate) {
        this.photosFirstDate = photosFirstDate;
    }

    public Date getPhotosFirstDateTaken() {
        return photosFirstDateTaken;
    }

    public void setPhotosFirstDateTaken(Date photosFirstDateTaken) {
        this.photosFirstDateTaken = photosFirstDateTaken;
    }

    public Date getFaveDate() {
        return faveDate;
    }

    public void setFaveDate(Date faveDate) {
        this.faveDate = faveDate;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
    }

    public long getFilesizeMax() {
        return filesizeMax;
    }

    public void setFilesizeMax(long filesizeMax) {
        this.filesizeMax = filesizeMax;
    }

    public String getPhotosurl() {
        return photosurl;
    }

    public void setPhotosurl(String photosurl) {
        this.photosurl = photosurl;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }

    public String getMobileurl() {
        return mobileurl;
    }

    public void setMobileurl(String mobileurl) {
        this.mobileurl = mobileurl;
    }

}
