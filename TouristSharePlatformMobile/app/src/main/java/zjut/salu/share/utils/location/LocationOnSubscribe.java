package zjut.salu.share.utils.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import rx.Observable;
import rx.Subscriber;

/**立即定位
 * Created by Salu on 2017/2/14.
 */

public class LocationOnSubscribe implements Observable.OnSubscribe<BDLocation>  {
    private final Context context;

    public LocationOnSubscribe(Context context) {
        this.context = context;
    }

    @Override
    public void call(final Subscriber<? super BDLocation> subscriber) {
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
