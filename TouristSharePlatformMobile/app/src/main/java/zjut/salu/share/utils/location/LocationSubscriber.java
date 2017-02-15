package zjut.salu.share.utils.location;

import android.support.annotation.NonNull;

import com.baidu.location.BDLocation;

import rx.Subscriber;

/**
 * Created by Salu on 2017/2/14.
 */

public abstract class LocationSubscriber extends Subscriber<BDLocation> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable throwable) {
        onLocatedFail(null);
    }

    @Override
    public void onNext(BDLocation bdLocation) {
        if (LocationUtil.isLocationResultEffective(bdLocation)) {
            onLocatedSuccess(bdLocation);
        } else {
            onLocatedFail(bdLocation);
        }
    }

    public abstract void onLocatedSuccess(@NonNull BDLocation bdLocation);
    public abstract void onLocatedFail(BDLocation bdLocation);
}
