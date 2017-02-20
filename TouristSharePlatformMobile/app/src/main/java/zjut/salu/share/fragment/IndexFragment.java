package zjut.salu.share.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.andview.refreshview.XRefreshView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.AlbumActivity;
import zjut.salu.share.activity.AlbumDetailActivity;
import zjut.salu.share.activity.DestinationActivity;
import zjut.salu.share.activity.LoginActivity;
import zjut.salu.share.activity.LoveCardActivity;
import zjut.salu.share.activity.ProductActivity;
import zjut.salu.share.activity.SkillAcademyActivity;
import zjut.salu.share.activity.SkillAcademyDetailActivity;
import zjut.salu.share.activity.UserInfoActivity;
import zjut.salu.share.activity.UserStrategyActivity;
import zjut.salu.share.activity.UserStrategyDetailActivity;
import zjut.salu.share.activity.index.LightStrategyActivity;
import zjut.salu.share.adapter.index.IndexStrategyRecycleAdapter;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.greendao.IndexBannerBeanDao;
import zjut.salu.share.model.IndexBannerBean;
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.model.skillacademy.SkillAcademy;
import zjut.salu.share.utils.GlideImageLoader;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.utils.greendao.GreenDaoDBHelper;
import zjut.salu.share.widget.CityPicker;
import zjut.salu.share.widget.layout.FullyGridLayoutManager;

/**首页fragment
 * Created by Alan on 2016/10/16.
 */

public class IndexFragment extends RxLazyFragment {
    public static final String ALBUM = "album";
    public static final String SKILLACADEMY = "skillacademy";
    @Bind(R.id.index_header_banner) Banner banner;//滚动轮播图对象
    @Bind(R.id.index_grid_view)GridView indexGridView;//gridView对象
    @Bind(R.id.xref_index)XRefreshView refreshView;//刷新组件
    private Context context;
    private String[] images=null;
    private List<String> titles=null;
    private String[] bannerId=null;
    private String[] bannerType=null;
    private int[] gridIcons=null;//grid_view图标
    private String[] gridTitles=null;//grid_view文字
    private OkHttpUtils okHttpUtils=null;

    //DAO持久对象
    private IndexBannerBeanDao bannerBeanDao;
    private List<IndexBannerBean> beans=null;
    private int isRefresh=1;//是否为刷新
    private ImageLoader imageLoader;

    public static final String STRATEGY="strategy";

    @Bind(R.id.recycle_strategy)RecyclerView strategyRecycle;

    private List<UserStrategy> strategyList;

    private IndexStrategyRecycleAdapter strategyRecycleAdapter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_index;
    }


    @Override
    public void finishCreateView(Bundle state) {
        imageLoader=ImageLoader.getInstance();
        bannerBeanDao = GreenDaoDBHelper.getDaoSession().getIndexBannerBeanDao();
        PullRefreshUtils.bindPullRefreshView(refreshView,true,false,new IndexRefreshViewListener());
        indexGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        okHttpUtils=new OkHttpUtils();
        context=getActivity();
        beans=bannerBeanDao.loadAll();
        if(null!=beans&&beans.size()>0){
            images=new String[6];
            titles=new LinkedList<>();
            bannerId=new String[6];
            bannerType=new String[6];
            int length=0;
            for(IndexBannerBean bean:beans){
                images[length]=bean.getCoverImage();
                titles.add(bean.getName());
                bannerId[length]=bean.getId();
                bannerType[length]=bean.getType();
                length++;
            }
            startBanner();
        }else{//去网络下载
            isRefresh=1;
            initHeaderBanner();
        }
        initIndexGridView();
        finishTask();
    }

    @Override
    protected void finishTask() {//初始化主数据
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.LOAD_INDEX_DATA,null);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        ((Activity)context).runOnUiThread(()->{
                            ToastUtils.ShortToast(R.string.server_down_text);
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        try {
                            JSONObject object=new JSONObject(result);
                            strategyList=gson.fromJson(object.getString("strategy"),new TypeToken<List<UserStrategy>>(){}.getType());
                            FullyGridLayoutManager gridLayoutManager=new FullyGridLayoutManager(context,2);
                            strategyRecycle.setHasFixedSize(true);
                            strategyRecycle.setNestedScrollingEnabled(true);
                            strategyRecycle.setLayoutManager(gridLayoutManager);
                            strategyRecycleAdapter=new IndexStrategyRecycleAdapter(strategyRecycle,strategyList,imageLoader);
                            strategyRecycle.setAdapter(strategyRecycleAdapter);
                            strategyRecycle.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 初始化头部滚动轮播图
     */
    private void initHeaderBanner(){
        okHttpUtils.startAsyncGetRequest(RequestURLs.GET_BANNER_BEAN_DATA_URL,new GetBannerDataResponseCallback());
    }



    /**
     * 获得滚动图数据
     */
    private class GetBannerDataResponseCallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            ((Activity)context).runOnUiThread(() -> ToastUtils.ShortToast(R.string.no_network_connection));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                final String responseResult=response.body().string();
                Gson gson=new Gson();
                beans=gson.fromJson(responseResult, new TypeToken<List<IndexBannerBean>>(){}.getType());
                int length=0;
                images=null;titles=null;bannerId=null;bannerType=null;
                images=new String[6];
                titles=new LinkedList<>();
                bannerId=new String[6];
                bannerType=new String[6];
                if(isRefresh==0){//不是刷新
                    bannerBeanDao.deleteAll();//删除数据库
                }
                for (IndexBannerBean bean:beans){
                    images[length]=RequestURLs.MAIN_URL+bean.getCoverImage();
                    bean.setCoverImage(RequestURLs.MAIN_URL+bean.getCoverImage());
                    titles.add(bean.getName());
                    bannerId[length]=bean.getId();
                    bannerType[length]=bean.getType();
                    //将数据插入到数据库当中
                    bannerBeanDao.insert(bean);
                    length++;
                }
                ((Activity)context).runOnUiThread(IndexFragment.this::startBanner);
            }else{
                ((Activity)context).runOnUiThread(() -> ToastUtils.ShortToast(R.string.server_down_text));
            }
        }
    }

    /**
     * 启动Banner动画
     */
    private void startBanner(){
        banner.setImageLoader(new GlideImageLoader());
        banner.setBackgroundColor(Color.WHITE);
        banner.setImages(Arrays.asList(images));
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
        banner.setBannerTitles(titles);
        banner.setDelayTime(3500);
        banner.isAutoPlay(true);
        banner.setBannerAnimation(com.youth.banner.Transformer.RotateDown);
        banner.setOnBannerClickListener(new IndexBannerClickListener());//轮播图点击事件
        banner.start();
        banner.startAutoPlay();
    }

    /**
     * 初始化grid_view
     */
    private void initIndexGridView(){
        gridIcons=new int[]{R.mipmap.home_user_strategy,R.mipmap.home_write_strategy,R.mipmap.home_official_strategy,
        R.mipmap.home_album,R.mipmap.home_product,R.mipmap.home_skill_academy,R.mipmap.home_love_postcard,R.mipmap.home_user_space};
        String userStrategy=getString(R.string.index_user_strategy_text);
        String writeStrategy=getString(R.string.index_write_strategy_text);
        String officialStrategy=getString(R.string.index_destination_text);
        String niceShot=getString(R.string.index_nice_shot_text);
        String tripTools=getString(R.string.index_trip_equipment_text);
        String skills=getString(R.string.index_skills_text);
        String loveCard=getString(R.string.index_love_card_text);
        String mySpace=getString(R.string.index_my_space_text);
        gridTitles=new String[]{userStrategy,writeStrategy,officialStrategy,niceShot,tripTools,skills,loveCard,mySpace};
        SimpleAdapter gridAdapter = new SimpleAdapter(context, getGridData(), R.layout.index_grid_view_item, new String[]{"grid_image", "grid_title"}, new int[]{R.id.iv_grid_item, R.id.tv_grid_item});
        indexGridView.setAdapter(gridAdapter);
        indexGridView.setOnItemClickListener(new IndexGridOnItemClickListener());//监听事件
    }

    /**
     * 为grid_view填充数据
     * @return 集合
     */
    private List<Map<String,Object>> getGridData(){
        List<Map<String, Object>> gridList = new LinkedList<>();
        Map<String,Object> map;
        for(int i=0;i<gridTitles.length;i++){
            map=new HashMap<>();
            map.put("grid_image",gridIcons[i]);
            map.put("grid_title",gridTitles[i]);
            gridList.add(map);
        }
        return gridList;
    }

    /**
     * gridView的监听事件
     */
    private class IndexGridOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:{//游记攻略
                    Intent intent=new Intent(context, UserStrategyActivity.class);
                    startActivity(intent);
                    break;
                }
                case 1:{//轻游记
                    Intent intent=new Intent(context,LightStrategyActivity.class);
                    startActivity(intent);
                    break;
                }
                case 2:{//目的地
                    Intent intent=new Intent(context, DestinationActivity.class);
                    startActivity(intent);
                    break;
                }
                case 3:{//美丽瞬间
                    Intent intent=new Intent(context, AlbumActivity.class);
                    startActivity(intent);
                    break;
                }
                case 4:{//旅行装备
                    Intent intent=new Intent(context, ProductActivity.class);
                    startActivity(intent);
                    break;
                }
                case 5:{//行摄技法
                   Intent intent=new Intent(context, SkillAcademyActivity.class);
                    startActivity(intent);
                    break;
                }
                case 6:{//爱の明信片
                    Intent intent=new Intent(context, LoveCardActivity.class);
                    startActivity(intent);
                    break;
                }
                case 7:{//用户中心,我的空间
                    //判断用户是否登录
                    Boolean loginStatus=PreferenceUtils.getBoolean("loginStatus",false);
                    if(loginStatus){
                        Intent intent=new Intent(context, UserInfoActivity.class);
                        intent.putExtra("isCurrentUser",true);
                        startActivity(intent);
                    }else{
                        ToastUtils.ShortToast(R.string.please_login_first_text);
                        Intent intent=new Intent(context,LoginActivity.class);
                        intent.putExtra("activity_name",((Activity)context).getClass().getName());
                        startActivity(intent);
                    }
                    break;
                }
            }
        }
    }


    /**
     * 轮播图の点击事件
     * 注意position从0取起
     */
    private class IndexBannerClickListener implements OnBannerClickListener{
        @Override
        public void OnBannerClick(int position) {
            String type=bannerType[position-1];
            String id=bannerId[position-1];
            String title=titles.get(position-1);
            switch (type){
                case STRATEGY:{//攻略
                    getBannerSingleData(STRATEGY,id);
                    break;
                }
                case ALBUM:{//相册
                    Intent intent=new Intent(context, AlbumDetailActivity.class);
                    intent.putExtra("albumid",id);
                    intent.putExtra("title",title);
                    startActivity(intent);
                    break;
                }
                case SKILLACADEMY:{//技法
                    getBannerSingleData(SKILLACADEMY,id);
                    break;
                }
            }
        }
    }

    /**
     * 查找单个轮播图数据
     * @param type 类型
     * @param id id
     * @return
     */
    private void getBannerSingleData(String type,String id){
        String url="";
        switch (type){
            case STRATEGY:{//攻略
                url=RequestURLs.GET_USER_STRATEGY_URL;
                break;
            }
            case SKILLACADEMY:{//技法
                url=RequestURLs.GET_SKILL_ACADEMY;
                break;
            }
        }
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("id",id);
        params.add(map);
        Observable<String> observable=okHttpUtils.asyncPostRequest(params,url);
        String finalUrl = url;
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        if(finalUrl.equals(RequestURLs.GET_USER_STRATEGY_URL)){
                            UserStrategy strategy=gson.fromJson(result,UserStrategy.class);
                            Intent intent=new Intent(context, UserStrategyDetailActivity.class);
                            intent.putExtra("user_strategy",strategy);
                            startActivity(intent);
                        }else{
                            SkillAcademy academy=gson.fromJson(result,SkillAcademy.class);
                            Intent intent=new Intent(context,SkillAcademyDetailActivity.class);
                            intent.putExtra("skill_academy",academy);
                            startActivity(intent);
                        }
                    }
                });
    }



    @Override
    public void onStart() {
        super.onStart();
        banner.isAutoPlay(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        banner.isAutoPlay(false);
        banner.stopAutoPlay();
    }

    /**
     * 刷新时回调
     */
    private class IndexRefreshViewListener implements XRefreshView.XRefreshViewListener {
        @Override
        public void onRefresh() {
            Observable.timer(2, TimeUnit.SECONDS)
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onCompleted() {}
                        @Override
                        public void onError(Throwable e) {}

                        @Override
                        public void onNext(Long aLong) {
                            isRefresh=0;
                            initHeaderBanner();//刷新banner数据
                        }
                    });
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
}
