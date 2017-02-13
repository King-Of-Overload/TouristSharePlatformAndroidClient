package zjut.salu.share.model.local;

import java.io.Serializable;
import java.math.BigDecimal;

/**地理位置信息
 * Created by Salu on 2017/2/13.
 */

public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    private String locationid;
    private BigDecimal longitude;
    private BigDecimal latitude;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getLocationid() {
        return locationid;
    }

    public void setLocationid(String locationid) {
        this.locationid = locationid;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
}
