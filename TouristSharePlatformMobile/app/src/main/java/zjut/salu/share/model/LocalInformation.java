package zjut.salu.share.model;

import java.io.Serializable;
import java.util.Date;

/**定位当地pojo映射对象
 * Created by Salu on 2016/12/25.
 */

public class LocalInformation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String localid;

    private int cityid;

    private String localbref;

    private String localcannotmisscontent;

    private String localcannotmissplaintext;

    private String localarrive;

    private String localtraffic;

    private String localguide;

    private String localtips;

    private Date localdate;

    private String localcover;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getLocalid() {
        return localid;
    }

    public void setLocalid(String localid) {
        this.localid = localid;
    }

    public int getCityid() {
        return cityid;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    public String getLocalbref() {
        return localbref;
    }

    public void setLocalbref(String localbref) {
        this.localbref = localbref;
    }

    public String getLocalcannotmisscontent() {
        return localcannotmisscontent;
    }

    public void setLocalcannotmisscontent(String localcannotmisscontent) {
        this.localcannotmisscontent = localcannotmisscontent;
    }

    public String getLocalcannotmissplaintext() {
        return localcannotmissplaintext;
    }

    public void setLocalcannotmissplaintext(String localcannotmissplaintext) {
        this.localcannotmissplaintext = localcannotmissplaintext;
    }

    public String getLocalarrive() {
        return localarrive;
    }

    public void setLocalarrive(String localarrive) {
        this.localarrive = localarrive;
    }

    public String getLocaltraffic() {
        return localtraffic;
    }

    public void setLocaltraffic(String localtraffic) {
        this.localtraffic = localtraffic;
    }

    public String getLocalguide() {
        return localguide;
    }

    public void setLocalguide(String localguide) {
        this.localguide = localguide;
    }

    public String getLocaltips() {
        return localtips;
    }

    public void setLocaltips(String localtips) {
        this.localtips = localtips;
    }

    public Date getLocaldate() {
        return localdate;
    }

    public void setLocaldate(Date localdate) {
        this.localdate = localdate;
    }

    public String getLocalcover() {
        return localcover;
    }

    public void setLocalcover(String localcover) {
        this.localcover = localcover;
    }
}
