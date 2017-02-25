package zjut.salu.share.utils.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import rx.Observable;
import rx.Subscriber;

/**获取最近一次定位信息：
 * Created by Salu on 2017/2/14.
 */

public class LocationLateKnownOnSubscribe implements Observable.OnSubscribe<BDLocation> {
    private final Context context;

    public LocationLateKnownOnSubscribe(Context context) {
        this.context = context;
    }

    @Override
    public void call(final Subscriber<? super BDLocation> subscriber) {
        BDLocation lateKnownLocation = LocationClient.get(context).getLateKnownLocation();
        if (LocationUtil.isLocationResultEffective(lateKnownLocation)) {
            subscriber.onNext(lateKnownLocation);
            subscriber.onCompleted();
        } else {
            BDLocationListener bdLocationListener = new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    subscriber.onNext(bdLocation);
                    subscriber.onCompleted();
                }

            };
            LocationClient.get(context).locate(bdLocationListener);
        }
    }
}
