package zjut.salu.share.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.google.gson.Gson;

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
import zjut.salu.share.config.CuteTouristShareConfig;
import zjut.salu.share.model.LocalInformation;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.NoScrollGridView;

/**当地fragment控制层
 * Created by Alan on 2016/10/16.
 */

public class UserStrategyFragment extends RxLazyFragment{
    @Bind(R.id.pull_scroll_local)PullToZoomScrollViewEx scrollViewEx;
    @Bind(R.id.toolbar)Toolbar mToolbar;
    @Bind(R.id.tv_current_destination)TextView currentAreaTV;//当地右上角名称
    @Bind(R.id.grid_view_local_index)NoScrollGridView roundGridView;//美食区域
    @Bind(R.id.grid_view_local_square_index)NoScrollGridView squareGridView;//导览区域
    @Bind(R.id.iv_zoom_view_skill_academy_detail)ImageView headCoverIV;//顶部封面
    @Bind(R.id.iv_wanna_go)ImageView wannaGoIV;//想去按钮
    @Bind(R.id.tv_wanna_go_num)TextView wannaGoNumTV;//想去人数
    @Bind(R.id.iv_went)ImageView hasGoneIV;//去过按钮
    @Bind(R.id.tv_gone_num)TextView hasGoneNumTV;//去过人数

    private Context context;
    private OkHttpUtils httpUtils;

    private int currentCityId;//当前城市id
    private LocalInformation currentInformation;

    private static final String LOCATION_BRIEF = CuteTouristShareConfig.mInstance.getString(R.string.location_bref_text);
    private static final String LOCATION_NOT_MISS =CuteTouristShareConfig.mInstance.getString(R.string.not_miss_text);
    private static final String LOCATION_ARRIVE = CuteTouristShareConfig.mInstance.getString(R.string.arrive_text);
    private static final String LOCATION_TRAFFIC = CuteTouristShareConfig.mInstance.getString(R.string.traffic_text);
    private static final String LOCATION_GUIDE = CuteTouristShareConfig.mInstance.getString(R.string.guide_text);
    private static final String LOCATION_TIPS = CuteTouristShareConfig.mInstance.getString(R.string.tips_text);
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_user_strategy;
    }

    @Override
    public void finishCreateView(Bundle state) {
        httpUtils=new OkHttpUtils();
        mToolbar.setTitle("");
        context=getActivity();
        setPullToZoomViewLayoutParams(scrollViewEx);
        Drawable drawable=getResources().getDrawable(R.drawable.icon_destnation_off);
        drawable.setBounds(0, 0, 40, 40);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        currentAreaTV.setCompoundDrawables(drawable, null, null, null);//只放左边
        int[] roundGridIcons = new int[]{R.drawable.ic_restaurant, R.drawable.ic_hotel, R.drawable.ic_scenery, R.drawable.ic_shopping,
                R.drawable.ic_entertainment, R.drawable.ic_life};
        SimpleAdapter adapter=new SimpleAdapter(context,getGridData(roundGridIcons),R.layout.item_local_index_grid_view,
                new String[]{"grid_image"},new int[]{R.id.cciv_local_round_icon});
        roundGridView.setAdapter(adapter);
        int[] squareGridIcons=new int[]{R.drawable.ic_destination,R.drawable.ic_cant_miss,R.drawable.ic_transit
        ,R.drawable.ic_transportaion,R.drawable.ic_event,R.drawable.ic_tips};
        adapter=new SimpleAdapter(context,getGridData(squareGridIcons),R.layout.item_local_index_square_grid_view,
                new String[]{"grid_image"},new int[]{R.id.iv_local_square_icon});
        squareGridView.setAdapter(adapter);
        roundGridView.setOnItemClickListener(new RoundGridViewItemClickListener());
        squareGridView.setOnItemClickListener(new SquareGridViewItemClickListener());
        //TODO:获取当前的地理位置
        //此处首先进行地点模拟测试功能：定位于日本奈良
        currentCityId=398;
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("cityid",currentCityId+"");
        params.add(map);
        Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.LOCATE_CURRENT_POSITION);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {
                        ((Activity)context).runOnUiThread(()-> ToastUtils.ShortToast(R.string.server_down_text));
                    }
                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        currentInformation=gson.fromJson(result,LocalInformation.class);
                        Glide.with(context)//城市封面加载
                                .load(RequestURLs.MAIN_URL+currentInformation.getLocalcover())
                                .centerCrop()
                                .dontAnimate()
                                .placeholder(R.drawable.ico_user_default)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(headCoverIV);//加载头部城市图片
                    }
                });
    }

    /**
     * 为grid_view填充数据
     * @return 集合
     */
    private List<Map<String,Object>> getGridData(int[] gridIcons){
        List<Map<String, Object>> gridList = new LinkedList<>();
        Map<String,Object> map;
        for(int i=0;i<gridIcons.length;i++){
            map=new HashMap<>();
            map.put("grid_image",gridIcons[i]);
            gridList.add(map);
        }
        return gridList;
    }

    /**
     * 推荐路线点击事件
     * @param v
     */
    @OnClick(R.id.iv_recommand_local_route)
    public void recommandRouteClick(View v){
        //TODO:推荐路线
    }


    /**
     * 设置头部的View的宽高。
     * @param scrollView 下拉scrollview
     */
    private void setPullToZoomViewLayoutParams(PullToZoomScrollViewEx scrollView) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth,
                (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);
    }

    /**
     * 圆形grid_view点击事件
     */
    private class RoundGridViewItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:{//美食
                    break;
                }
                case 1:{//酒店
                    break;
                }
                case 2:{//景点
                    break;
                }
                case 3:{//购物
                    break;
                }
                case 4:{//娱乐
                    break;
                }
                case 5:{//生活
                    break;
                }
            }
        }
    }

    /**
     * 方形grid_view点击事件
     */
    private class SquareGridViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:{//目的地概况
                    Intent intent=new Intent(context, LocaltionWebViewActivity.class);
                    intent.putExtra("location_content_type",LOCATION_BRIEF);
                    intent.putExtra("content",currentInformation.getLocalbref());
                    startActivity(intent);
                    break;
                }
                case 1:{//不可错过
                    Intent intent=new Intent(context,LocaltionWebViewActivity.class);
                    intent.putExtra("location_content_type",LOCATION_NOT_MISS);
                    intent.putExtra("content",currentInformation.getLocalcannotmisscontent());
                    startActivity(intent);
                    break;
                }
                case 2:{//如何到达
                    Intent intent=new Intent(context,LocaltionWebViewActivity.class);
                    intent.putExtra("location_content_type",LOCATION_ARRIVE);
                    intent.putExtra("content",currentInformation.getLocalarrive());
                    startActivity(intent);
                    break;
                }
                case 3:{//交通攻略
                    Intent intent=new Intent(context,LocaltionWebViewActivity.class);
                    intent.putExtra("location_content_type",LOCATION_TRAFFIC);
                    intent.putExtra("content",currentInformation.getLocaltraffic());
                    startActivity(intent);
                    break;
                }
                case 4:{//活动指南
                    Intent intent=new Intent(context,LocaltionWebViewActivity.class);
                    intent.putExtra("location_content_type",LOCATION_GUIDE);
                    intent.putExtra("content",currentInformation.getLocalguide());
                    startActivity(intent);
                    break;
                }
                case 5:{//小贴士
                    Intent intent=new Intent(context,LocaltionWebViewActivity.class);
                    intent.putExtra("location_content_type",LOCATION_TIPS);
                    intent.putExtra("content",currentInformation.getLocaltips());
                    startActivity(intent);
                    break;
                }
            }
        }
    }
}
