package zjut.salu.share.model;

import java.io.Serializable;

/**城市主页推荐
 * Created by Salu on 2017/1/20.
 */

public class CityIndexRecommend implements Serializable{
    private String cityindexid;
    private String cityindexname;
    private int cityindexcover;
    private String cityindexdes;
    private String cityindexcontent;

    public String getCityindexid() {
        return cityindexid;
    }

    public void setCityindexid(String cityindexid) {
        this.cityindexid = cityindexid;
    }

    public String getCityindexname() {
        return cityindexname;
    }

    public void setCityindexname(String cityindexname) {
        this.cityindexname = cityindexname;
    }

    public int getCityindexcover() {
        return cityindexcover;
    }

    public void setCityindexcover(int cityindexcover) {
        this.cityindexcover = cityindexcover;
    }

    public String getCityindexdes() {
        return cityindexdes;
    }

    public void setCityindexdes(String cityindexdes) {
        this.cityindexdes = cityindexdes;
    }

    public String getCityindexcontent() {
        return cityindexcontent;
    }

    public void setCityindexcontent(String cityindexcontent) {
        this.cityindexcontent = cityindexcontent;
    }
}
