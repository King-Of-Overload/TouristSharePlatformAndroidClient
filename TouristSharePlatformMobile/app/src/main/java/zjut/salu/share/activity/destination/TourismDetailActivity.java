package zjut.salu.share.activity.destination;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.Bind;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.common.CommonWebActivity;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.local.Cuision;
import zjut.salu.share.model.local.TourismAttraction;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.SweetAlertUtils;

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
    private OkHttpUtils okHttpUtils;
    private WeakReference<Activity> mReference;
    private List<Cuision> cuisions;
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
        okHttpUtils=new OkHttpUtils();
        mReference=new WeakReference<>(this);
        Intent intent=getIntent();
        attraction= (TourismAttraction) intent.getSerializableExtra("attraction");
        double lat=intent.getDoubleExtra("lat",0);
        double log=intent.getDoubleExtra("log",0);
        LatLng location = new LatLng(lat, log);
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
            Map<String,Object> params=new HashMap<>();
            params.put("attractionid",attraction.getTourismid());
            Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.GET_CUISION_DATA,params);
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
                            cuisions=gson.fromJson(result,new TypeToken<List<Cuision>>(){}.getType());
                            StringBuilder builder=new StringBuilder();
                            for(Cuision cuision:cuisions){
                                builder.append(cuision.getCuisionname()+" ");
                            }
                            foodNameTV.setText(builder.toString());
                        }
                    });
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
        LatLng desLatLng=new LatLng(attraction.getLocation().getLatitude().doubleValue(),attraction.getLocation().getLongitude().doubleValue());
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
        baiduMap.setOnMarkerClickListener(marker1 -> {
            //TODO:跳转到大的地图界面
            MapViewActivity.launch(mReference.get(),desLatLng.latitude,desLatLng.longitude);
            return true;
        });
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
        CuisionActivity.launch(mReference.get(),cuisions);
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
        String phone=phoneTV.getText().toString();
        SweetAlertUtils.showTitleAndContentDialogWithStyle(mReference.get(),getString(R.string.xiaoyuan_alert_text),
                getString(R.string.call_phone_to_text)+phone+getString(R.string.ma_text), SweetAlertDialog.CUSTOM_IMAGE_TYPE,
                getResources().getDrawable(R.drawable.alert_icon),getString(R.string.positive_text),getString(R.string.negative_text),
                new CallPhoneOnSweetConfirmListener(),new CallPhoneOnSweetCancelListener());
    }

    /**
     * 访问官网
     */
    @OnClick(R.id.tv_website)
    public void websiteClick(){
        CommonWebActivity.launch(mReference.get(),webTV.getText().toString());
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
        AskRouteActivity.launch(mReference.get(),attraction);
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

    /**
     * 拨打电话确定按钮事件
     */
    private class CallPhoneOnSweetConfirmListener implements SweetAlertDialog.OnSweetClickListener {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            //用intent启动拨打电话
            Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneTV.getText().toString()));
            sweetAlertDialog.dismiss();
            startActivity(intent);
        }
    }

    /**
     * 拨打电话取消按钮事件
     */
    private class CallPhoneOnSweetCancelListener implements SweetAlertDialog.OnSweetClickListener {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
        }
    }
}
