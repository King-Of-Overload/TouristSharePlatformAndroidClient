package zjut.salu.share.activity.destination;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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

import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;

public class MapViewActivity extends RxBaseActivity{
    private MapView mapView;
    @Override
    public int getLayoutId() {
        return R.layout.activity_map_view;
    }

    public static void launch(Activity activity,double lat,double log){
        Intent mIntent = new Intent(activity, MapViewActivity.class);
        mIntent.putExtra("lat",lat);
        mIntent.putExtra("log",log);
        activity.startActivity(mIntent);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent=getIntent();
        double lat=intent.getDoubleExtra("lat",0);
        double log=intent.getDoubleExtra("log",0);
        mapView= (MapView) findViewById(R.id.mapview);
        LatLng desLatLng=new LatLng(lat,log);
        BaiduMap baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);// 开启定位图层

        MyLocationData locData=new MyLocationData.Builder()
                .direction(100).latitude(desLatLng.latitude)
                .longitude(desLatLng.longitude).build();
        //设置定位数据
        baiduMap.setMyLocationData(locData);
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        BitmapDescriptor currentMarker= BitmapDescriptorFactory.fromResource(R.drawable.nav);
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

    @Override
    public void initToolBar() {

    }
}
