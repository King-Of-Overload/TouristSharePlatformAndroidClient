package zjut.salu.share.activity.index;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.OnClick;
import zjut.salu.share.R;
import zjut.salu.share.activity.banggumi.BanggumiDetailActivity;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.GlideImageLoader;
import zjut.salu.share.utils.RequestURLs;

/**
 * 轻游记模块
 */
public class LightStrategyActivity extends RxBaseActivity {
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.common_header_banner)Banner banner;
    @Bind(R.id.swipe_view_common)SwipeRefreshLayout refreshLayout;
    @Bind(R.id.tv_banggumi)TextView banggumiTV;
    @Bind(R.id.tv_text)TextView textTV;


    private String[] images;
    private WeakReference<Activity> mReference;

    @Override
    public int getLayoutId() {
        return R.layout.activity_light_strategy;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        startBanner();//初始化轮播图
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(()->{
            if(refreshLayout.isRefreshing()){
               //TODO:刷新时回调
            }
        });
        TextPaint paint=banggumiTV.getPaint();
        paint.setFakeBoldText(true);
        paint=textTV.getPaint();
        paint.setFakeBoldText(true);

    }

    /**
     * 小视频按钮点击事件
     */
    @OnClick(R.id.linear_banngume_btn)
    public void banggumiClick(View v){
        //TODO:
        Intent intent=new Intent(mReference.get(), BanggumiDetailActivity.class);
        startActivity(intent);
    }

    /**
     * 旅行笔记点击事件
     */
    @OnClick(R.id.linear_text_btn)
    public void textClick(View v){
        //TODO:
    }

    /**
     * 所有视频(按照点击量排序)
     */
    @OnClick(R.id.item_type_rank_btn)
    public void allBanggumiClick(){
        //TODO:
    }

    /**
     * 所有视频(按照上传时间排序)
     */
    @OnClick(R.id.item_type_more)
    public void newBangguiClick(View v){
        //TODO:
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
