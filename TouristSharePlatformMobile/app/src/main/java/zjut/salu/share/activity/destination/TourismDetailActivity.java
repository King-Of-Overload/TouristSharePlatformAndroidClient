package zjut.salu.share.activity.destination;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.OnClick;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.local.TourismAttraction;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.RequestURLs;

/**
 * 地点详情页面控制层
 */
public class TourismDetailActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.iv_cover)ImageView coverIV;
    @Bind(R.id.tv_name)TextView nameTV;
    @Bind(R.id.ratingbar)RatingBar ratingBar;
    @Bind(R.id.tv_point)TextView pointTV;
    @Bind(R.id.tv_category)TextView categoryTV;
    @Bind(R.id.tv_name_plus_foreignname)TextView mainNameTV;
    @Bind(R.id.tv_description)TextView descriptionTV;
    @Bind(R.id.cardview_foods)CardView foodCardView;
    @Bind(R.id.tv_food_names)TextView foodNameTV;
    @Bind(R.id.tv_price)TextView priceTV;
    @Bind(R.id.tv_time_open)TextView timeOpenTV;
    MapView mapView;
    @Bind(R.id.tv_address)TextView addressTV;
    @Bind(R.id.tv_guide)TextView guideTV;
    @Bind(R.id.tv_phone)TextView phoneTV;
    @Bind(R.id.tv_website)TextView webTV;
    @Bind(R.id.tv_activity)TextView activityTV;


    private TourismAttraction attraction;
    private LatLng location;

    private WeakReference<Activity> mReference;
    @Override
    public int getLayoutId() {
        return R.layout.activity_tourism_detail;
    }
    public static void launch(Activity activity, TourismAttraction attraction,double lat,double log){
        Intent mIntent = new Intent(activity, TourismDetailActivity.class);
        mIntent.putExtra("attraction",attraction);
        mIntent.putExtra("lat",lat);
        mIntent.putExtra("log",log);
        activity.startActivity(mIntent);
    }
    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        Intent intent=getIntent();
        attraction= (TourismAttraction) intent.getSerializableExtra("attraction");
        double lat=intent.getDoubleExtra("lat",0);
        double log=intent.getDoubleExtra("log",0);
        location=new LatLng(lat,log);
        mapView= (MapView) findViewById(R.id.mapview);
        finishTask();
    }

    @Override
    public void finishTask() {
        ImageLoaderUtils.loadAvatarWithURL(mReference.get(),
                RequestURLs.MAIN_URL+attraction.getTourismCoverses().get(0).getTourismurl(), DiskCacheStrategy.NONE,
                coverIV);
        nameTV.setText(attraction.getTourismname());
        ratingBar.setMax(5);
        ratingBar.setNumStars(5);
        ratingBar.setRating(4.6f);//TODO:设置评分
        String point=4.6+ getString(R.string.point_text);
        pointTV.setText(point);
        categoryTV.setText(attraction.getTourismCategory().getTmcategoryname());
        mainNameTV.setText(attraction.getTourismname()+"("+attraction.getTourismforeignname()+")");
        descriptionTV.setText(attraction.getTourismdescription());
        if(attraction.getTourismtype().equals("food")){
            //TODO:获取食物列表
        }else{
            foodCardView.setVisibility(View.GONE);
        }
        String price=attraction.getTourismprice()+attraction.getCurrencytype();
        priceTV.setText(price);
        timeOpenTV.setText(attraction.getTourismopendesc());
        addressTV.setText(attraction.getTourismaddress());
        guideTV.setText(attraction.getTourismguide());
        phoneTV.setText(attraction.getTourismphone());
        webTV.setText(attraction.getTourismurl());
        activityTV.setText(attraction.getTourismactivity());
        initMap();
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        LatLng attractionLng=new LatLng(attraction.getLocation().getLatitude().doubleValue(),attraction.getLocation().getLongitude().doubleValue());
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(attractionLng);
        LatLng desLatLng = converter.convert();
        BaiduMap baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);// 开启定位图层

        MyLocationData locData=new MyLocationData.Builder()
                .direction(100).latitude(desLatLng.latitude)
                .longitude(desLatLng.longitude).build();
        //设置定位数据
        baiduMap.setMyLocationData(locData);
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        BitmapDescriptor currentMarker=BitmapDescriptorFactory.fromResource(R.drawable.nav);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, currentMarker);
        baiduMap.setMyLocationConfigeration(config);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(desLatLng).zoom(18.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        //准备 marker option 添加 marker 使用
        MarkerOptions markerOptions = new MarkerOptions().icon(currentMarker).position(desLatLng);
//获取添加的 marker 这样便于后续的操作
       Marker marker = (Marker) baiduMap.addOverlay(markerOptions);
        // 当不需要定位图层时关闭定位图层
        baiduMap.setMyLocationEnabled(false);
    }

    /**
     * 评论按钮
     */
    @OnClick(R.id.linear_user_best_tab)
    public void commentClick(View v){
        //TODO:评论
    }

    /**
     * 食品推荐点击
     */
    @OnClick(R.id.linear_food_recommend)
    public void foodClick(View v){
        //TODO:食品推荐
    }

    /**
     * 导航
     */
    @OnClick(R.id.tv_nav)
    public void navClick(View v){
        //TODO:导航
    }

    /**
     * 拨打电话
     */
    @OnClick(R.id.tv_phone)
    public void phoneClick(View v){
        //TODO:拨打电话
    }

    /**
     * 访问官网
     */
    @OnClick(R.id.tv_website)
    public void websiteClick(){
        //TODO:访问官网
    }

    @OnClick(R.id.linear_index_tab)
    public void collectClick(View v){
        //TODO:收藏
    }

    @OnClick(R.id.linear_user_strategy_tab)
    public void comment(View v){
        //TODO:点评
    }

    @OnClick(R.id.linear_user_best_tab)
    public void share(View v){
        //TODO:分享
    }

    @OnClick(R.id.linear_personal_setting_tab)
    public void wenluCardClick(View v){
        //TODO:问路卡
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.attraction_detail_text);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
}
