package zjut.salu.share.activity.lightstrategy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.adapter.userinfo.UserInfoMainPageAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.NineImage;
import zjut.salu.share.model.SpaceBean;
import zjut.salu.share.model.lightstrategy.DiaryLightStrategy;
import zjut.salu.share.model.lightstrategy.DiaryLightStrategyImage;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.LogUtil;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.NineGridLayout;

/**
 * 轻游记日记详情控制层
 * @author Alan-Mac
 */
public class DiaryLightStrategyDetail extends RxBaseActivity {
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.iv_avatar_main_page)ImageView avatarIV;
    @Bind(R.id.tv_username_main_page)TextView userNameTV;
    @Bind(R.id.tv_time_main_page)TextView timeTV;
    @Bind(R.id.tv_content_main_page)TextView contentTV;
    @Bind(R.id.nl_image_main_page)NineGridLayout nineGridLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_diary_light_strategy_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        WeakReference<Activity> mReference = new WeakReference<>(this);
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        Intent intent=getIntent();
        DiaryLightStrategy diaryLightStrategy= (DiaryLightStrategy) intent.getSerializableExtra("diary_light_strategy");
        ImageLoaderUtils.loadAvatarWithURL(mReference.get(), RequestURLs.MAIN_URL+diaryLightStrategy.getUser().getHeaderimage()
        , DiskCacheStrategy.NONE,avatarIV);
        timeTV.setText(StringUtils.getTimeDiff(diaryLightStrategy.getDiarytime().getTime()));
        contentTV.setText(diaryLightStrategy.getDiarycontent());
        userNameTV.setText(diaryLightStrategy.getUser().getUsername());
        List<NineImage> imgs=new ArrayList<>();
        for(int i=0;i<diaryLightStrategy.getImages().size();i++){
            NineImage nineImage=new NineImage();
            String strategyImage=diaryLightStrategy.getImages().get(i);
            nineImage.setUrl(RequestURLs.MAIN_URL+strategyImage);
            nineImage.setPosition(i+"");
            imgs.add(nineImage);
        }
        nineGridLayout.setImagesData(imgs,mReference.get());
        Map<String,Object> params=new HashMap<>();
        params.put("diaryid",diaryLightStrategy.getDiaryid());
        Observable<String> observable= okHttpUtils.asyncGetRequest(RequestURLs.INCREASE_DIARY_LIGHT_STRATEGY,params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(String result) {LogUtil.d("success");}
                });

    }

    @Override
    public void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setTitle(R.string.diary_detail_text);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
