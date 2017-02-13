package zjut.salu.share.model.local;

import java.io.Serializable;

/**景点图片
 * Created by Salu on 2017/2/13.
 */

public class TourismCovers implements Serializable {
    private String tourismcoverid;
    private String tourismurl;

    private TourismAttraction tourismAttraction;

    public String getTourismcoverid() {
        return tourismcoverid;
    }

    public void setTourismcoverid(String tourismcoverid) {
        this.tourismcoverid = tourismcoverid;
    }

    public String getTourismurl() {
        return tourismurl;
    }

    public void setTourismurl(String tourismurl) {
        this.tourismurl = tourismurl;
    }

    public TourismAttraction getTourismAttraction() {
        return tourismAttraction;
    }

    public void setTourismAttraction(TourismAttraction tourismAttraction) {
        this.tourismAttraction = tourismAttraction;
    }
}
