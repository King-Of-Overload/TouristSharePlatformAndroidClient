package zjut.salu.share.activity.lightstrategy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.UserInfoActivity;
import zjut.salu.share.adapter.userinfo.UserInfoMainPageAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.SpaceBean;
import zjut.salu.share.model.lightstrategy.DiaryLightStrategy;
import zjut.salu.share.model.lightstrategy.DiaryLightStrategyImage;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;

/**
 * 旅行日记列表显示
 * @author Alan-Mac
 */
public class DiaryLightStrategyActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_text_light_strategy)ListView listView;

    private WeakReference<Activity> mReference;

    private List<DiaryLightStrategy> diaryLightStrategies;
    private UserInfoMainPageAdapter adapter;
    @Override
    public int getLayoutId() {
        return R.layout.activity_diary_light_strategy;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        progressView.spin();
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        mReference=new WeakReference<>(this);
        Observable<String> observable= okHttpUtils.asyncGetRequest(RequestURLs.GET_ALL_DIARY_LIGHT_STRATEGY,null);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        runOnUiThread(()->progressView.stopSpinning());
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()->ToastUtils.ShortToast(R.string.server_down_text));
                    }

                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        diaryLightStrategies=gson.fromJson(result,new TypeToken<List<DiaryLightStrategy>>(){}.getType());
                        List<SpaceBean> beanList=new ArrayList<>();
                        for (DiaryLightStrategy s:diaryLightStrategies){
                            SpaceBean bean=new SpaceBean();
                            bean.setClickedNum(s.getClicknum());
                            bean.setContent(s.getDiarycontent());
                            bean.setId(s.getDiaryid());
                            bean.setIsessence(s.getIsesence());
                            bean.setTime(s.getDiarytime());
                            bean.setUser(s.getUser());
                            List<String> imgList=new ArrayList<>();
                            for(DiaryLightStrategyImage img:s.getImages()){
                                imgList.add(RequestURLs.MAIN_URL+img.getDiaryimgurl());
                            }
                            bean.setImageList(imgList);
                        }
                        adapter=new UserInfoMainPageAdapter(beanList,mReference.get());
                        listView.setAdapter(adapter);
                        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),true,true));
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            DiaryLightStrategy lightStrategy=diaryLightStrategies.get(position);
                            Intent intent=new Intent(mReference.get(),DiaryLightStrategyDetail.class);
                            intent.putExtra("diary_light_strategy",lightStrategy);
                            startActivity(intent);
                        });
                    }
                });
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.trip_note_text);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
