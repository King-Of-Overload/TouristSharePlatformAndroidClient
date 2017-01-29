package zjut.salu.share.activity.destination;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.LocaltionWebViewActivity;
import zjut.salu.share.activity.local.RecommendRouteActivity;
import zjut.salu.share.activity.local.RecommendRouteDetailActivity;
import zjut.salu.share.adapter.destination.CityIndexRecommendAdapter;
import zjut.salu.share.adapter.destination.CityIndexRecommendRouteAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.config.CuteTouristShareConfig;
import zjut.salu.share.fragment.UserStrategyFragment;
import zjut.salu.share.model.CityIndexRecommend;
import zjut.salu.share.model.LocalInformation;
import zjut.salu.share.model.Routes;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;
import zjut.salu.share.widget.HorizontalListView;
import zjut.salu.share.widget.NoScrollGridView;

/**
 * 城市首页controller
 */
public class CityIndexActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.pull_scroll_city_index)PullToZoomScrollViewEx zoomScrollView;

    @Bind(R.id.iv_zoom_view_city_index)ImageView headerIV;
    @Bind(R.id.tv_city_name)TextView cityNameTV;
    @Bind(R.id.iv_wanna_go)ImageView wannaGoIV;
    @Bind(R.id.iv_has_gone)ImageView hasGoneIV;
    @Bind(R.id.tv_wanna_go_num)TextView wannaGoTV;
    @Bind(R.id.tv_has_gone_num)TextView hasGoneTV;

    @Bind(R.id.tv_routes)TextView routesTV;

    @Bind(R.id.grid_view_local_index)NoScrollGridView gridView;
    @Bind(R.id.tv_local_summary)TextView localSummaryTV;
    @Bind(R.id.tv_can_not_miss)TextView canNotMissTV;
    @Bind(R.id.horizontal_lv)NoScrollGridView recommendListView;
    @Bind(R.id.horizontal_lv_route)HorizontalListView routeListView;
    @Bind(R.id.lv_gallery_food)NoScrollGridView galleryFood;
    @Bind(R.id.lv_gallery_view)NoScrollGridView galleryView;
    @Bind(R.id.circle_progress_common)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed)ImageView loadingFailedIV;
    @Bind(R.id.iv_empty)ImageView emptyIV;



    private int cityId;
    private String cityName;
    private String[] gridTitles;
    private int[] gridIcons;
    private List<CityIndexRecommend> recommendList;//推荐集合
    private List<Routes> routesList;//推荐路线集合
    private CityIndexRecommendAdapter recommendAdapter;
    private CityIndexRecommendRouteAdapter routeAdapter;
    private ImageLoader imageLoader;
    private OkHttpUtils okHttpUtils;
    private WeakReference<Activity> mReference;

    private LocalInformation information;


    @Override
    public int getLayoutId() {
        return R.layout.activity_city_index;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        Intent intent=getIntent();
        cityId=intent.getIntExtra("city_id",0);
        cityName=intent.getStringExtra("city_name");
        okHttpUtils=new OkHttpUtils();
        progressView.spin();
        imageLoader=ImageLoader.getInstance();
        Boolean isNetworkAvailable= CommonUtils.isNetworkAvailable(this);
        if(isNetworkAvailable){
            List<Map<String,Object>> params=new ArrayList<>();
            Map<String,Object> map=new HashMap<>();
            map.put("cityid",cityId+"");
            params.add(map);
            Observable<String> observable=okHttpUtils.asyncPostRequest(params, RequestURLs.GET_CITY_DATAS);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            runOnUiThread(()->{
                                progressView.stopSpinning();
                                progressView.setVisibility(View.INVISIBLE);
                            });
                        }

                        @Override
                        public void onError(Throwable e) {
                            loadingFailedIV.setVisibility(View.VISIBLE);
                            ToastUtils.ShortToast(R.string.server_down_text);
                        }

                        @Override
                        public void onNext(String result) {
                            try {
                                Gson gson=new Gson();
                                JSONObject object=new JSONObject(result);
                                String status=object.getString("result");
                                if(status.equals("success")){
                                    routesList=gson.fromJson(object.getString("routes"),new TypeToken<List<Routes>>(){}.getType());//推荐路线集合
                                    information=gson.fromJson(object.getString("info"),LocalInformation.class);
                                    recommendList=gson.fromJson(object.getString("recommends"),new TypeToken<List<CityIndexRecommend>>(){}.getType());//主页推荐数据
                                    gridTitles=new String[]{getString(R.string.food_text),getString(R.string.hotel_text),getString(R.string.view_text),getString(R.string.shop_text),
                                            getString(R.string.play_text),getString(R.string.live_text),getString(R.string.route_text),getString(R.string.traffics_text),getString(R.string.activity_text),
                                            getString(R.string.tips_text)};
                                    gridIcons=new int[]{R.drawable.ic_local_food,R.drawable.ic_local_hotel,R.drawable.ic_local_scenic,R.drawable.ic_local_shopping,
                                            R.drawable.ic_local_entertainment,R.drawable.ic_local_life,R.drawable.ic_local_path,R.drawable.ic_local_subway,
                                            R.drawable.ic_local_activity,R.drawable.ic_local_tips};
                                    setPullToZoomViewLayoutParams(zoomScrollView);
                                    initHeaderView();
                                    initGridView();
                                    initBoldTextView();
                                    initCityIndexRecommend();
                                    initRecommendRoutes();
                                    initRecommendFoods();
                                    initRecommendViews();
                                }else if(status.equals("empty")){
                                    emptyIV.setVisibility(View.VISIBLE);
                                    zoomScrollView.setVisibility(View.GONE);
                                    ToastUtils.ShortToast(R.string.city_not_open_text);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }else{
            progressView.stopSpinning();
            progressView.setVisibility(View.INVISIBLE);
            zoomScrollView.setVisibility(View.GONE);
            loadingFailedIV.setVisibility(View.VISIBLE);
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }

    /**
     * 初始化头部区域
     */
    private void initHeaderView() {
        cityNameTV.setText(cityName);
    }


    /**
     * 初始化推荐景点
     */
    private void initRecommendViews() {
        String[] viewCategory=new String[]{"公园,广场","博物馆","名胜古迹","建筑人文"};
        int[] viewCover=new int[]{R.drawable.park,R.drawable.bowuguan,R.drawable.msgj,R.drawable.jzrw};
        List<Map<String,Object>> data=new ArrayList<>();
        for(int i=0;i<viewCategory.length;i++){
            Map<String,Object> map=new HashMap<>();
            map.put("cover",viewCover[i]);
            map.put("name",viewCategory[i]);
            map.put("num","26个推荐商家");//TODO:此处需要修改
            data.add(map);
        }
        SimpleAdapter adapter=new SimpleAdapter(this,data,R.layout.item_city_index_all_recommend,new String[]{"cover","name","num"},new
        int[]{R.id.iv_cover,R.id.tv_name,R.id.tv_num});
        galleryView.setAdapter(adapter);
        galleryView.setOnItemClickListener(((parent, view, position, id) ->{
            //TODO:条目点击事件
        }));
    }

    /**
     * 初始化推荐美食列表
     */
    private void initRecommendFoods(){
        String[] foodsCategory=new String[]{"中餐","西餐","面包甜点","日本料理","咖啡店"};
        int[] foodsCover=new int[]{R.drawable.zhongcan,R.drawable.xican,R.drawable.cake,R.drawable.nihonliaoli,R.drawable.coffee};
        List<Map<String,Object>> data=new ArrayList<>();
        for(int i=0;i<foodsCategory.length;i++){
            Map<String,Object> map=new HashMap<>();
            map.put("cover",foodsCover[i]);
            map.put("name",foodsCategory[i]);
            map.put("num","26个推荐商家");//TODO:此处需要修改
            data.add(map);
        }
        SimpleAdapter adapter=new SimpleAdapter(this,data,R.layout.item_city_index_all_recommend,new String[]{"cover","name","num"},new
        int[]{R.id.iv_cover,R.id.tv_name,R.id.tv_num});
        galleryFood.setAdapter(adapter);
        galleryFood.setOnItemClickListener((parent, view, position, id) -> {
            //TODO:条目点击事件
        });
    }

    /**
     * 初始化推荐路线部分
     */
    private void initRecommendRoutes(){
        TextPaint paint=routesTV.getPaint();
        paint.setFakeBoldText(true);
        routeAdapter=new CityIndexRecommendRouteAdapter(this,routesList,imageLoader);
        routeListView.setAdapter(routeAdapter);
        routeListView.setOnItemClickListener((parent, view, position, id) -> {
            Routes route=routesList.get(position);
            Intent intent=new Intent(CityIndexActivity.this,RecommendRouteDetailActivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("route",route);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
    /**
     * 初始化首页集合
     */
    private void initCityIndexRecommend(){
        recommendAdapter=new CityIndexRecommendAdapter(this,recommendList,imageLoader);
        recommendListView.setAdapter(recommendAdapter);
        recommendListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent=new Intent(mReference.get(),CityIndexRecommendActivity.class);
            Bundle bundle=new Bundle();
            CityIndexRecommend recommend=recommendList.get(position);
            bundle.putSerializable("recommend",recommend);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    /**
     * 当地概况点击事件
     * @param v view
     */
    @OnClick(R.id.linear_local_summary)
    public void localSummaryClick(View v){
        Intent intent=new Intent(mReference.get(), LocaltionWebViewActivity.class);
        intent.putExtra("location_content_type", UserStrategyFragment.LOCATION_BRIEF);
        intent.putExtra("content",information.getLocalbref());
        startActivity(intent);
    }

    /**
     * 不可错过点击事件
     * @param v view
     */
    @OnClick(R.id.linear_can_not_miss)
    public void canNotMissClick(View v){
        Intent intent=new Intent(mReference.get(),LocaltionWebViewActivity.class);
        intent.putExtra("location_content_type",UserStrategyFragment.LOCATION_NOT_MISS);
        intent.putExtra("content",information.getLocalcannotmisscontent());
        startActivity(intent);
    }

    /**
     * 初始化界面字体
     */
    private void initBoldTextView() {
        TextPaint paint=localSummaryTV.getPaint();
        paint.setFakeBoldText(true);
        paint=canNotMissTV.getPaint();
        paint.setFakeBoldText(true);
    }

    /**
     * 初始化gridview
     */
    private void initGridView() {
        List<Map<String,Object>> params=getGridData();
        SimpleAdapter adapter=new SimpleAdapter(this,params,R.layout.index_grid_view_item,new String[]{"grid_image","grid_title"},
                new int[]{R.id.iv_grid_item, R.id.tv_grid_item});
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position){
                case 0:{
                    break;
                }
                case 6:{//推荐路线
                    Intent intent=new Intent(mReference.get(), RecommendRouteActivity.class);
                    intent.putExtra("cityid",cityId);
                    startActivity(intent);
                    break;
                }
                case 7:{//交通
                    Intent intent=new Intent(mReference.get(),LocaltionWebViewActivity.class);
                    intent.putExtra("location_content_type",UserStrategyFragment.LOCATION_TRAFFIC);
                    intent.putExtra("content",information.getLocaltraffic());
                    startActivity(intent);
                   break;
                }
                case 8:{//活动
                    Intent intent=new Intent(mReference.get(),LocaltionWebViewActivity.class);
                    intent.putExtra("location_content_type",UserStrategyFragment.LOCATION_GUIDE);
                    intent.putExtra("content",information.getLocalguide());
                    startActivity(intent);
                    break;
                }
                case 9:{//小贴士
                    Intent intent=new Intent(mReference.get(),LocaltionWebViewActivity.class);
                    intent.putExtra("location_content_type",UserStrategyFragment.LOCATION_TIPS);
                    intent.putExtra("content",information.getLocaltips());
                    startActivity(intent);
                    break;
                }
            }
        });
    }

    /**
     * 查看全部美食
     * @param v tv
     */
    @OnClick(R.id.tv_look_all_food)
    public void allFoodClick(View v){
        //TODO:
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

    @Override
    public void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setTitle(cityName);
        toolbar.setNavigationOnClickListener(v->finish());
    }

    /**
     * 设置头部的View的宽高。
     * @param scrollView myscroll
     */
    private void setPullToZoomViewLayoutParams(PullToZoomScrollViewEx scrollView) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth,
                (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);
    }
}
