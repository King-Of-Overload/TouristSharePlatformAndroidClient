package zjut.salu.share.model;

import java.io.Serializable;
import java.util.Date;

import zjut.salu.share.model.city.City;

/**推荐路线实体
 * Created by Salu on 2017/1/16.
 */

public class Routes implements Serializable {
    private String routeid;
    private String routename;
    private String routecontent;
    private String routeplaintext;
    private String routecover;
    private Date routedate;
    private City city;

    public String getRouteid() {
        return routeid;
    }

    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    public String getRoutename() {
        return routename;
    }

    public void setRoutename(String routename) {
        this.routename = routename;
    }

    public String getRoutecontent() {
        return routecontent;
    }

    public void setRoutecontent(String routecontent) {
        this.routecontent = routecontent;
    }

    public String getRouteplaintext() {
        return routeplaintext;
    }

    public void setRouteplaintext(String routeplaintext) {
        this.routeplaintext = routeplaintext;
    }

    public String getRoutecover() {
        return routecover;
    }

    public void setRoutecover(String routecover) {
        this.routecover = routecover;
    }

    public Date getRoutedate() {
        return routedate;
    }

    public void setRoutedate(Date routedate) {
        this.routedate = routedate;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
