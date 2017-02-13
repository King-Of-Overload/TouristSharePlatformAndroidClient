package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.Bind;
import mehdi.sakout.dynamicbox.DynamicBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.adapter.skill_academy.SkillAcademyListAdapter;
import zjut.salu.share.base.AbsBaseActivity;
import zjut.salu.share.base.AbsBaseActivityWithBar;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.exception.MyException;
import zjut.salu.share.fragment.IndexFragment;
import zjut.salu.share.fragment.user_order_list_fragment.UserPayedOrderFragment;
import zjut.salu.share.model.SkillBannerBean;
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.model.skillacademy.SkillAcademy;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.GlideImageLoader;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import android.support.v4.view.ViewPager.PageTransformer;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * 行摄技法学院主页面
 */
public class SkillAcademyActivity extends RxBaseActivity {
    @Bind(R.id.toolbar_skill_academy)Toolbar mToolBar;
    @Bind(R.id.header_banner_skill_academy)Banner banner;
    @Bind(R.id.lv_skill_academy)ListView listView;
    @Bind(R.id.xref_skill_academy)XRefreshView refreshView;

    private String[] bannerImageList=new String[6];
    private String[] bannerId=new String[6];
    private String[] bannerTitleList=new String[6];
    private OkHttpUtils okHttpUtils=null;
    private WeakReference<Activity> mReference=null;
    private WeakReference<ListView> listViewWeakReference=null;
    public static final String ACADEMY_SKILL="ACADEMY_SKILL";
    public static final String ACADEMY_AFTER="ACADEMY_AFTER";
    public static final String ACADEMY_BASIC="ACADEMY_BASIC";
    public static final String ACADEMY_EQUIP="ACADEMY_EQUIP";
    public static final String ACADEMY_MASTER="ACADEMY_MASTER";
    public static final String ACADEMY_STRATEGY="ACADEMY_STRATEGY";
    private String currentType="";
    private List<SkillAcademy> academyList=null;
    private Boolean isNetworkAlive=false;
    private SkillAcademyListAdapter adapter=null;
    private DynamicBox dynamicBox=null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_skill_academy;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        okHttpUtils=new OkHttpUtils();
        mReference=new WeakReference<>(this);
        listViewWeakReference=new WeakReference<>(listView);
        PullRefreshUtils.bindPullRefreshView(refreshView,true,false,new SkillAcademyListRefreshViewListener());
        dynamicBox=new DynamicBox(mReference.get(),listView);
        DynamicBoxUtils.bindDynamicBox(dynamicBox,getString(R.string.hint_text),getString(R.string.server_exception_text),
                getString(R.string.loading_now_text),new GetDataFailedClickListener());
        initBannerView();
        isNetworkAlive= CommonUtils.isNetworkAvailable(mReference.get());
        if(isNetworkAlive){
            dynamicBox.showLoadingLayout();
            currentType=ACADEMY_SKILL;
            getSkillAcademyData(ACADEMY_SKILL);
        }else{
            dynamicBox.showExceptionLayout();
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }

    /**
     * 下拉刷新监听事件
     */
 private class SkillAcademyListRefreshViewListener implements XRefreshView.XRefreshViewListener{

     @Override
     public void onRefresh() {
         new Handler().postDelayed(() -> {
             isNetworkAlive=CommonUtils.isNetworkAvailable(mReference.get());
             if(isNetworkAlive==true){
                 getSkillAcademyData(currentType);
                 refreshView.stopRefresh();
                 ToastUtils.ShortToast(R.string.refresh_text);
             }else{
                 refreshView.stopRefresh();
                 ToastUtils.ShortToast(R.string.no_network_connection);
             }
         }, 2000);
     }

     @Override
     public void onLoadMore(boolean isSilence) {

     }

     @Override
     public void onRelease(float direction) {

     }

     @Override
     public void onHeaderMove(double headerMovePercent, int offsetY) {

     }
 }
    /**
     * 失败界面重试按钮
     */
    private class GetDataFailedClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            getSkillAcademyData(currentType);
        }
    }

    /**
     * 获取技法学院数据，默认为摄影技巧
     * @param requestType 请求参数
     */
    private void getSkillAcademyData(String requestType){
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("requestType",requestType);
        params.add(map);
        try {
            okHttpUtils.startPostRequestWithParams(params,RequestURLs.GET_SKILL_ACADEMY_DATA_URL,new GetSkillAcademyDataResponseCallback());
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    private class GetSkillAcademyDataResponseCallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(() -> {
                dynamicBox.showExceptionLayout();
                ToastUtils.ShortToast(R.string.no_network_connection);
            });
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                final String responseString=response.body().string();
                Gson gson=new Gson();
                academyList=gson.fromJson(responseString,new TypeToken<List<SkillAcademy>>(){}.getType());
                runOnUiThread(() -> {
                    dynamicBox.hideAll();
                    adapter=new SkillAcademyListAdapter(mReference.get(),academyList,listViewWeakReference.get());
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new MyListViewItemClickListener());
                });
            }else{
                runOnUiThread(() -> {
                    dynamicBox.showExceptionLayout();
                    ToastUtils.ShortToast(R.string.server_down_text);
                });
            }
        }
    }

    /**
     * list view点击事件
     */
    private class MyListViewItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent(mReference.get(),SkillAcademyDetailActivity.class);
            intent.putExtra("skill_academy",academyList.get(position));
            startActivity(intent);
        }
    }

    /**
     * 初始化banner轮播图
     */
    private void initBannerView() {
        okHttpUtils.startAsyncGetRequest(RequestURLs.GET_SKILL_ACADEMY_BANNER_DATA_URL,new GetBannerListResponseCallback());
    }

    /**
     * 轮播图获取回调
     */
    private class GetBannerListResponseCallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(() -> ToastUtils.ShortToast(R.string.no_network_connection));
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                final String responseResult=response.body().string();
                Gson gson=new Gson();
                List<SkillBannerBean> beans=gson.fromJson(responseResult,new TypeToken<List<SkillBannerBean>>(){}.getType());
                int length=0;
                for(SkillBannerBean bean:beans){
                    bannerId[length]=bean.getSkillId();
                    bannerTitleList[length]=bean.getBannerTitle();
                    bannerImageList[length]=RequestURLs.MAIN_URL+bean.getBannerImage();
                    length++;
                }
                runOnUiThread(() -> {
                    banner.setImageLoader(new GlideImageLoader());
                    banner.setBackgroundColor(Color.WHITE);
                    banner.setImages(Arrays.asList(bannerImageList));
                    banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
                    banner.setBannerTitles(Arrays.asList(bannerTitleList));
                    banner.setDelayTime(3500);
                    banner.isAutoPlay(true);
                    banner.setBannerAnimation(Transformer.FlipHorizontal);
                    banner.setOnBannerClickListener(new SkillAcademyBannerClickListener());//轮播图点击事件
                    banner.start();
                    banner.startAutoPlay();
                });
            }else{
                runOnUiThread(() -> ToastUtils.ShortToast(R.string.server_down_text));
            }
        }

    }

    /**
     * 轮播图点击事件
     */
    private class SkillAcademyBannerClickListener implements OnBannerClickListener {

        @Override
        public void OnBannerClick(int position) {

        }
    }

    @Override
    public void initToolBar() {
        mToolBar.setTitle(getText(R.string.index_skills_text));
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolBar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_skill_academy,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_skill_skill_academy:{
                currentType=ACADEMY_SKILL;
                getSkillAcademyData(ACADEMY_SKILL);
                break;
            }
            case R.id.menu_houqimiji_skill_academy:{
                currentType=ACADEMY_AFTER;
                getSkillAcademyData(ACADEMY_AFTER);
                break;
            }
            case R.id.menu_basic_skill_academy:{
                currentType=ACADEMY_BASIC;
                getSkillAcademyData(ACADEMY_BASIC);
                break;
            }
            case R.id.menu_equip_skill_academy:{
                currentType=ACADEMY_EQUIP;
                getSkillAcademyData(ACADEMY_EQUIP);
                break;
            }
            case R.id.menu_master_skill_academy:{
                currentType=ACADEMY_MASTER;
                getSkillAcademyData(ACADEMY_MASTER);
                break;
            }
            case R.id.menu_strategy_skill_academy:{
                currentType=ACADEMY_STRATEGY;
                getSkillAcademyData(ACADEMY_STRATEGY);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
