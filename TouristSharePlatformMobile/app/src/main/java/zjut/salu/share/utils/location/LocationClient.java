package zjut.salu.share.utils.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;

/**定位client
 * Created by Salu on 2017/2/14.
 */

public class LocationClient {
    private com.baidu.location.LocationClient realClient;

    private static volatile LocationClient proxyClient;

    private LocationClient(Context context) {
        realClient = new com.baidu.location.LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        //设置百度定位参数
        realClient.setLocOption(option);
    }

    public static LocationClient get(Context context) {
        if (proxyClient == null) {
            synchronized (LocationClient.class) {
                if (proxyClient == null) {
                    proxyClient = new LocationClient(context.getApplicationContext());
                }
            }
        }
        return proxyClient;
    }

    public void locate(final BDLocationListener bdLocationListener) {
        final BDLocationListener realListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                bdLocationListener.onReceiveLocation(bdLocation);
                //防止内存溢出
                realClient.unRegisterLocationListener(this);
                stop();
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        realClient.registerLocationListener(realListener);
        if (!realClient.isStarted()) {
            realClient.start();
        }
    }

    public BDLocation getLateKnownLocation() {
        return realClient.getLastKnownLocation();
    }

    public void stop() {
        realClient.stop();
    }
}
