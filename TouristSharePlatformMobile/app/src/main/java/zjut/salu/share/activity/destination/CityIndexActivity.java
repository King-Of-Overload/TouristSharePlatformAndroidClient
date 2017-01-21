package zjut.salu.share.activity.destination;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import zjut.salu.share.R;
import zjut.salu.share.adapter.destination.CityIndexRecommendAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.CityIndexRecommend;
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

    @Bind(R.id.grid_view_local_index)NoScrollGridView gridView;
    @Bind(R.id.tv_local_summary)TextView localSummaryTV;
    @Bind(R.id.tv_can_not_miss)TextView canNotMissTV;
    @Bind(R.id.horizontal_lv)NoScrollGridView recommendListView;




    private int cityId;
    private String cityName;
    private String[] gridTitles;
    private int[] gridIcons;
    private List<CityIndexRecommend> recommendList;
    private CityIndexRecommendAdapter recommendAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_city_index;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        gridTitles=new String[]{getString(R.string.food_text),getString(R.string.hotel_text),getString(R.string.view_text),getString(R.string.shop_text),
        getString(R.string.play_text),getString(R.string.live_text),getString(R.string.route_text),getString(R.string.traffics_text),getString(R.string.activity_text),
        getString(R.string.tips_text)};
        gridIcons=new int[]{R.drawable.ic_local_food,R.drawable.ic_local_hotel,R.drawable.ic_local_scenic,R.drawable.ic_local_shopping,
        R.drawable.ic_local_entertainment,R.drawable.ic_local_life,R.drawable.ic_local_path,R.drawable.ic_local_subway,
        R.drawable.ic_local_activity,R.drawable.ic_local_tips};
        Intent intent=getIntent();
        cityId=intent.getIntExtra("city_id",0);
        cityName=intent.getStringExtra("city_name");
        setPullToZoomViewLayoutParams(zoomScrollView);
        initGridView();
        initBoldTextView();
        initCityIndexRecommend();
    }

    private void initCityIndexRecommend(){
        //TODO:
        recommendList=new ArrayList<>();
        for(int i=0;i<3;i++){
            CityIndexRecommend r=new CityIndexRecommend();
            r.setCityindexcover(R.drawable.test_japan);
            r.setCityindexdes("萌物的前世今生");
            r.setCityindexname("奈良小鹿");
            recommendList.add(r);
        }
        recommendAdapter=new CityIndexRecommendAdapter(this,recommendList);
        recommendListView.setAdapter(recommendAdapter);
    }

    /**
     * 当地概况点击事件
     * @param v view
     */
    @OnClick(R.id.linear_local_summary)
    public void localSummaryClick(View v){
        //TODO:
    }

    /**
     * 不可错过点击事件
     * @param v view
     */
    @OnClick(R.id.linear_can_not_miss)
    public void canNotMissClick(View v){
        //TODO:
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
            //TODO:监听事件
        });
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
