package zjut.salu.share.model.lightstrategy.banggume;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import zjut.salu.share.model.TripUser;
import org.greenrobot.greendao.annotation.Generated;

/**小视频实体类
 * Created by Alan-Mac on 2017/02/07.
 */
@Entity(generateConstructors = false)
public class Banggume implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String bangumeid;
    private String bangumename;
    private String bangumecontent;
    private String bangumeurl;
    private int clicknum;
    private String bangumetags;
    private String bangumecover;
    @Transient private TripUser user;
    private int isesence;
    private Date bangumedate;
    private int banggumesharenum;
    private int banggumedownloadnum;
    private int banggumecollectnum;

    @Transient private List<BanggumeTag> banggimeTagList;

    private String phonestoragepath;

    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getPhonestoragepath() {
        return phonestoragepath;
    }

    public void setPhonestoragepath(String phonestoragepath) {
        this.phonestoragepath = phonestoragepath;
    }

    public Banggume() {
    }

    public int getBanggumesharenum() {
        return banggumesharenum;
    }

    public void setBanggumesharenum(int banggumesharenum) {
        this.banggumesharenum = banggumesharenum;
    }

    public int getBanggumedownloadnum() {
        return banggumedownloadnum;
    }

    public void setBanggumedownloadnum(int banggumedownloadnum) {
        this.banggumedownloadnum = banggumedownloadnum;
    }

    public int getBanggumecollectnum() {
        return banggumecollectnum;
    }

    public void setBanggumecollectnum(int banggumecollectnum) {
        this.banggumecollectnum = banggumecollectnum;
    }

    public List<BanggumeTag> getBanggimeTagList() {
        return banggimeTagList;
    }

    public void setBanggimeTagList(List<BanggumeTag> banggimeTagList) {
        this.banggimeTagList = banggimeTagList;
    }

    public Date getBangumedate() {
        return bangumedate;
    }

    public void setBangumedate(Date bangumedate) {
        this.bangumedate = bangumedate;
    }

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
