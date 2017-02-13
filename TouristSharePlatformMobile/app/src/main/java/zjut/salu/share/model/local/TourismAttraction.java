package zjut.salu.share.model.local;

import java.io.Serializable;
import java.util.List;

import zjut.salu.share.model.city.City;

/**地点pojo映射
 * Created by Salu on 2017/2/13.
 */

public class TourismAttraction implements Serializable{
    private static final long serialVersionUID = 1L;

    private String tourismid;
    private String locationid;
    private String tourismname;
    private TourismCategory tourismCategory;
    private String tourismforeignname;
    private String tourismdescription;
    private Float tourismprice;
    private String tourismopendesc;
    private String tourismaddress;
    private String tourismguide;
    private String tourismphone;
    private String tourismurl;
    private String tourismactivity;
    private String tourismtype;
    private String currencytype;
    private City city;

    private List<TourismCovers> tourismCoverses;

    private Location location;

    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<TourismCovers> getTourismCoverses() {
        return tourismCoverses;
    }

    public void setTourismCoverses(List<TourismCovers> tourismCoverses) {
        this.tourismCoverses = tourismCoverses;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTourismid() {
        return tourismid;
    }

    public void setTourismid(String tourismid) {
        this.tourismid = tourismid;
    }

    public String getLocationid() {
        return locationid;
    }

    public void setLocationid(String locationid) {
        this.locationid = locationid;
    }

    public String getTourismname() {
        return tourismname;
    }

    public void setTourismname(String tourismname) {
        this.tourismname = tourismname;
    }

    public TourismCategory getTourismCategory() {
        return tourismCategory;
    }

    public void setTourismCategory(TourismCategory tourismCategory) {
        this.tourismCategory = tourismCategory;
    }

    public String getTourismforeignname() {
        return tourismforeignname;
    }

    public void setTourismforeignname(String tourismforeignname) {
        this.tourismforeignname = tourismforeignname;
    }

    public String getTourismdescription() {
        return tourismdescription;
    }

    public void setTourismdescription(String tourismdescription) {
        this.tourismdescription = tourismdescription;
    }

    public Float getTourismprice() {
        return tourismprice;
    }

    public void setTourismprice(Float tourismprice) {
        this.tourismprice = tourismprice;
    }

    public String getTourismopendesc() {
        return tourismopendesc;
    }

    public void setTourismopendesc(String tourismopendesc) {
        this.tourismopendesc = tourismopendesc;
    }

    public String getTourismaddress() {
        return tourismaddress;
    }

    public void setTourismaddress(String tourismaddress) {
        this.tourismaddress = tourismaddress;
    }

    public String getTourismguide() {
        return tourismguide;
    }

    public void setTourismguide(String tourismguide) {
        this.tourismguide = tourismguide;
    }

    public String getTourismphone() {
        return tourismphone;
    }

    public void setTourismphone(String tourismphone) {
        this.tourismphone = tourismphone;
    }

    public String getTourismurl() {
        return tourismurl;
    }

    public void setTourismurl(String tourismurl) {
        this.tourismurl = tourismurl;
    }

    public String getTourismactivity() {
        return tourismactivity;
    }

    public void setTourismactivity(String tourismactivity) {
        this.tourismactivity = tourismactivity;
    }

    public String getTourismtype() {
        return tourismtype;
    }

    public void setTourismtype(String tourismtype) {
        this.tourismtype = tourismtype;
    }

    public String getCurrencytype() {
        return currencytype;
    }

    public void setCurrencytype(String currencytype) {
        this.currencytype = currencytype;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
