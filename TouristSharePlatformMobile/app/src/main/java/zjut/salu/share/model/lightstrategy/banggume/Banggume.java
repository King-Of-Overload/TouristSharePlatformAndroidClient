package zjut.salu.share.model.lightstrategy.banggume;

import java.io.Serializable;

import zjut.salu.share.model.TripUser;

/**小视频实体类
 * Created by Alan-Mac on 2017/02/07.
 */

public class Banggume implements Serializable {
    private String bangumeid;
    private String bangumename;
    private String bangumecontent;
    private String bangumeurl;
    private int clicknum;
    private String bangumetags;
    private String bangumecover;
    private TripUser user;
    private int isesence;

    public String getBangumeid() {
        return bangumeid;
    }

    public void setBangumeid(String bangumeid) {
        this.bangumeid = bangumeid;
    }

    public String getBangumename() {
        return bangumename;
    }

    public void setBangumename(String bangumename) {
        this.bangumename = bangumename;
    }

    public String getBangumecontent() {
        return bangumecontent;
    }

    public void setBangumecontent(String bangumecontent) {
        this.bangumecontent = bangumecontent;
    }

    public String getBangumeurl() {
        return bangumeurl;
    }

    public void setBangumeurl(String bangumeurl) {
        this.bangumeurl = bangumeurl;
    }

    public int getClicknum() {
        return clicknum;
    }

    public void setClicknum(int clicknum) {
        this.clicknum = clicknum;
    }

    public String getBangumetags() {
        return bangumetags;
    }

    public void setBangumetags(String bangumetags) {
        this.bangumetags = bangumetags;
    }

    public String getBangumecover() {
        return bangumecover;
    }

    public void setBangumecover(String bangumecover) {
        this.bangumecover = bangumecover;
    }

    public TripUser getUser() {
        return user;
    }

    public void setUser(TripUser user) {
        this.user = user;
    }

    public int getIsesence() {
        return isesence;
    }

    public void setIsesence(int isesence) {
        this.isesence = isesence;
    }
}
