package zjut.salu.share.utils.location;

import com.baidu.location.BDLocation;

/**
 * Created by Salu on 2017/2/14.
 */

public class LocationUtil {
    public static boolean isLocationResultEffective(BDLocation bdLocation) {
        return bdLocation != null
                && (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation);
    }
}
