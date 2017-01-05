package zjut.salu.share.activity;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import rx.Observable;
import rx.Observer;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;

/**
 * 目的地界面
 */
public class DestinationActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.grid_view_destination)GridView mGridView;
    @Bind(R.id.swipe_destination)WaveSwipeRefreshLayout refreshLayout;
    @Bind(R.id.id_gallery)LinearLayout mGallery;
    private String[] gridTitles;
    private int[] gridIcons;
    @Override
    public int getLayoutId() {
        return R.layout.activity_destination;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        int[] recommandIds=new int[]{R.drawable.test_korea,R.drawable.test_japan,R.drawable.test_geek,R.drawable.test_america,R.drawable.test_thiland};
        String[] recommandTitle=new String[]{"韩国","日本","希腊","美国","泰国"};
        for(int i=0;i<recommandIds.length;i++){
            View view= LayoutInflater.from(this).inflate(R.layout.item_destination_recommand,mGallery,false);
            ImageView imageView= (ImageView) view.findViewById(R.id.iv_cover_destination);
            TextView textView= (TextView) view.findViewById(R.id.tv_recommand_destination);
            imageView.setImageResource(recommandIds[i]);
            textView.setText(recommandTitle[i]);
            mGallery.addView(view);
        }
        gridIcons=new int[]{R.drawable.destination_china,R.drawable.destination_nihong,R.drawable.destination_asia,R.drawable.destination_europe
        ,R.drawable.destination_dayangzhou,R.drawable.destination_beimeizhou};
        String china=getString(R.string.china_text);
        String nihong=getString(R.string.rihan_text);
        String asia=getString(R.string.aisa_text);
        String europe=getString(R.string.europe_text);
        String dayangzhou=getString(R.string.dayangzhou_text);
        String america=getString(R.string.america_text);
        gridTitles=new String[]{china,nihong,asia,europe,dayangzhou,america};
        WeakReference<Activity> reference=new WeakReference<>(this);
        SimpleAdapter adapter=new SimpleAdapter(reference.get(),getGridData(),R.layout.item_destination_grid_view,new String[]{
        "grid_image","grid_title"},new int[]{R.id.cciv_destination_avatar,R.id.tv_destination_name});
        mGridView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(() -> Observable.timer(2, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {refreshLayout.setRefreshing(false);}
            @Override
            public void onError(Throwable e) {}
            @Override
            public void onNext(Long aLong) {}
        }));
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
        mToolBar.setTitle(R.string.index_destination_text);
        mToolBar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolBar.setNavigationOnClickListener(v->finish());
    }
}
