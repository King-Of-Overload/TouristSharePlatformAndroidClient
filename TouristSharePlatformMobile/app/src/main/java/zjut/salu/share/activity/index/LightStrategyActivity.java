package zjut.salu.share.activity.index;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.banggumi.BanggumeActivity;
import zjut.salu.share.activity.banggumi.BanggumiDetailActivity;
import zjut.salu.share.activity.lightstrategy.DiaryLightStrategyActivity;
import zjut.salu.share.adapter.lightstrategy.BanggumeIndexRecycleAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.utils.GlideImageLoader;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.widget.CircleProgressView;
import zjut.salu.share.widget.MyScrollView;
import zjut.salu.share.widget.layout.FullyGridLayoutManager;
import zjut.salu.share.widget.scrollview.RecycleScrollView;

/**
 * 轻游记模块
 */
public class LightStrategyActivity extends RxBaseActivity {
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.common_header_banner)Banner banner;
    @Bind(R.id.swipe_view_common)SwipeRefreshLayout refreshLayout;
    @Bind(R.id.tv_banggumi)TextView banggumiTV;
    @Bind(R.id.tv_text)TextView textTV;
    @Bind(R.id.circle_progress_common)CircleProgressView progressView;

    @Bind(R.id.recycle_view_recommend)RecyclerView recommendRecycleView;
    @Bind(R.id.recycle_view_new)RecyclerView newRecycleView;
    @Bind(R.id.iv_loading_failed_recommend)ImageView recommendLoadingFailedIV;
    @Bind(R.id.iv_loading_failed_new)ImageView newLoadingFailedIV;
    @Bind(R.id.scrollview)RecycleScrollView scrollView;


    private String[] images;
    private WeakReference<Activity> mReference;
    private OkHttpUtils okHttpUtils;
    private List<Banggume> recommendList;
    private List<Banggume> newestList;
    private BanggumeIndexRecycleAdapter recommendAdapter;
    private BanggumeIndexRecycleAdapter newAdapter;
    private ImageLoader imageLoader;

    @Override
    public int getLayoutId() {
        return R.layout.activity_light_strategy;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        imageLoader=ImageLoader.getInstance();
        okHttpUtils=new OkHttpUtils();
        progressView.spin();
        startBanner();//初始化轮播图
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(()->{
                if(refreshLayout.isRefreshing()){
                    loadListData();
                }
        });
        TextPaint paint=banggumiTV.getPaint();
        paint.setFakeBoldText(true);
        paint=textTV.getPaint();
        paint.setFakeBoldText(true);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                if (refreshLayout != null) {
                    refreshLayout.setEnabled(scrollView.getScrollY() == 0);
                }
            });
        loadListData();
    }

    private void loadListData(){
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.LOAD_LIGHT_STRATEGY_INDEX_DATA,null);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        runOnUiThread(()->{
                            refreshLayout.setRefreshing(false);
                            recommendLoadingFailedIV.setVisibility(View.INVISIBLE);
                            newLoadingFailedIV.setVisibility(View.INVISIBLE);
                            progressView.stopSpinning();
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()->{
                            refreshLayout.setRefreshing(false);
                            recommendLoadingFailedIV.setVisibility(View.VISIBLE);
                            newLoadingFailedIV.setVisibility(View.VISIBLE);
                            progressView.stopSpinning();
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            Gson gson=new Gson();
                            recommendList=gson.fromJson(jsonObject.getString("recommend"),new TypeToken<List<Banggume>>(){}.getType());
                            newestList=gson.fromJson(jsonObject.getString("newest"),new TypeToken<List<Banggume>>(){}.getType());
                            FullyGridLayoutManager gridLayoutManager=new FullyGridLayoutManager(mReference.get(),2);
                            recommendRecycleView.setHasFixedSize(true);
                            recommendRecycleView.setNestedScrollingEnabled(true);
                            recommendRecycleView.setLayoutManager(gridLayoutManager);
                            newRecycleView.setHasFixedSize(true);
                            newRecycleView.setNestedScrollingEnabled(true);
                            FullyGridLayoutManager gridLayoutManager2=new FullyGridLayoutManager(mReference.get(),2);
                            newRecycleView.setLayoutManager(gridLayoutManager2);
                            recommendAdapter=new BanggumeIndexRecycleAdapter(recommendRecycleView,recommendList,imageLoader);
                            recommendRecycleView.setAdapter(recommendAdapter);
                            newAdapter=new BanggumeIndexRecycleAdapter(newRecycleView,newestList,imageLoader);
                            newRecycleView.setAdapter(newAdapter);
                            recommendAdapter.setOnItemClickListener((position, holder) -> {
                                Banggume banggume=recommendList.get(position);
                                BanggumiDetailActivity.launch(mReference.get(),banggume);
                            });
                            newAdapter.setOnItemClickListener((position, holder) -> {
                                Banggume banggume=newestList.get(position);
                                BanggumiDetailActivity.launch(mReference.get(),banggume);
                            });
                            recommendRecycleView.addOnScrollListener(new MyRecycleViewScrollListener(refreshLayout,scrollView));
                            newRecycleView.addOnScrollListener(new MyRecycleViewScrollListener(refreshLayout,scrollView));
                            setRecycleNoScroll();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void setRecycleNoScroll()
    {
        recommendRecycleView.setOnTouchListener((v, event) -> false);
        newRecycleView.setOnTouchListener((v, event) -> false);
    }

    /**
     * 小视频按钮点击事件
     */
    @OnClick(R.id.linear_banngume_btn)
    public void banggumiClick(View v){
        BanggumeActivity.launch(mReference.get(),null,"","");
    }
    /**
     * 旅行笔记点击事件
     */
    @OnClick(R.id.linear_text_btn)
    public void textClick(View v){
        Intent intent=new Intent(mReference.get(),DiaryLightStrategyActivity.class);
        startActivity(intent);
    }

    /**
     * 所有视频(按照点击量排序)
     */
    @OnClick(R.id.item_type_rank_btn)
    public void allBanggumiClick(){
        BanggumeActivity.launch(mReference.get(),null,BanggumeActivity.BANGGUME_HOT,"");
    }

    /**
     * 所有视频(按照上传时间排序)
     */
    @OnClick(R.id.item_type_more)
    public void newBangguiClick(View v){
        BanggumeActivity.launch(mReference.get(),null,BanggumeActivity.BANGGUME_NEW,"");
    }

    /**
     * 启动Banner动画
     */
    private void startBanner(){
        banner.setImageLoader(new GlideImageLoader());
        banner.setBackgroundColor(Color.WHITE);
        images=new String[]{RequestURLs.MAIN_URL+"img/test1.png",RequestURLs.MAIN_URL+"img/test2.png",RequestURLs.MAIN_URL+"img/test3.png"};
        banner.setImages(Arrays.asList(images));
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setDelayTime(3500);
        banner.isAutoPlay(true);
        banner.setBannerAnimation(Transformer.Default);
        //banner.setOnBannerClickListener(new IndexBannerClickListener());//轮播图点击事件
        banner.start();
        banner.startAutoPlay();
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.kawai_no_strategy);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
