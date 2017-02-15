package zjut.salu.share.utils.location;

import android.content.Context;

import com.baidu.location.BDLocation;

import rx.Observable;

/**用RxJava做回调的转发
 * Created by Salu on 2017/2/14.
 */

public class RxLocation {
    private static RxLocation instance = new RxLocation();

    private RxLocation () {}

    public static RxLocation get() {
        return instance;
    }

    public Observable<BDLocation> locate(Context context) {
        return Observable.create(new LocationOnSubscribe(context));
    }

    public Observable<BDLocation> locateLastKnown(Context context) {
        return Observable.create(new LocationLateKnownOnSubscribe(context));
    }

}
