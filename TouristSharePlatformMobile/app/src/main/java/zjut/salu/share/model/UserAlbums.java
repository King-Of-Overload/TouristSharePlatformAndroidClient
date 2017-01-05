package zjut.salu.share.model;

import java.io.Serializable;
import java.util.Date;

/**相册pojo
 * Created by Salu on 2016/11/30.
 */

public class UserAlbums implements Serializable {
    private String albumid;//相册id
    private String albumname;//相册标题
    private String albumdescription;//相册描述
    private int clickednum;//点击量
    private int ishot;//是否热门
    private Date uploadtime;//上传时间
    private int isessence;//是否精华
    private TripUser tripUser;//一对多，user对象
    private  City city;//一对多，城市对象
    private String coverImage;

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getAlbumdescription() {
        return albumdescription;
    }

    public void setAlbumdescription(String albumdescription) {
        this.albumdescription = albumdescription;
    }

    public int getClickednum() {
        return clickednum;
    }

    public void setClickednum(int clickednum) {
        this.clickednum = clickednum;
    }

    public int getIshot() {
        return ishot;
    }

    public void setIshot(int ishot) {
        this.ishot = ishot;
    }

    public Date getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(Date uploadtime) {
        this.uploadtime = uploadtime;
    }

    public int getIsessence() {
        return isessence;
    }

    public void setIsessence(int isessence) {
        this.isessence = isessence;
    }

    public TripUser getTripUser() {
        return tripUser;
    }

    public void setTripUser(TripUser tripUser) {
        this.tripUser = tripUser;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
