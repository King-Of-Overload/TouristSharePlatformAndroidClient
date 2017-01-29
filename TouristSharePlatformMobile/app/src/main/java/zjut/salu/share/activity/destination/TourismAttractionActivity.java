package zjut.salu.share.activity.destination;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yyydjk.library.DropDownMenu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.adapter.product.MyConstellationAdapter;
import zjut.salu.share.adapter.product.MyListDropDownAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.TourismCategory;
import zjut.salu.share.widget.CircleProgressView;

public class TourismAttractionActivity extends RxBaseActivity {
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.dropDownMenu)DropDownMenu dropDownMenu;

    private LinearLayout contentView;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private CircleProgressView progressView;
    private ImageView loadingFailedIV;
    private ImageView emptyIV;
    private FrameLayout frameLayout;

    private WeakReference<Activity> mReference;
    private int currentType=0;//当前板块
    private String title="";//当前标题
    private List<View> popupViews = new ArrayList<>();//弹出视图
    private MyListDropDownAdapter touristMainAdapter=null;
    private MyListDropDownAdapter touristCategoryAdapter=null;
    private MyListDropDownAdapter optionAdapter=null;

    private String[] headers;
    private List<Map<String,Object>> headerList;//美食，旅行
    private List<TourismCategory> tourismCategories=null;//详细分类集合

    @Override
    public int getLayoutId() {
        return R.layout.activity_tourism_attraction;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        Intent intent=getIntent();
        currentType=intent.getIntExtra("type",0);
        title=intent.getStringExtra("title");
        initDropDownList();//初始化筛选条件
    }

    /**
     * 初始化筛选数据
     */
    private void initDropDownList(){
//        headers=new String[]{"方向/类型","详细分类","其它选项"};
        headers=new String[]{"方向/类型"};
        String[] dropType=new String[]{"food","hotel","view","shop","play","live"};
        String[] dropHeaders=new String[]{getString(R.string.food_text),getString(R.string.hotel_text),getString(R.string.view_text),
                getString(R.string.shop_text),getString(R.string.play_text),getString(R.string.live_text)};
        headerList=new ArrayList<>();
        for(int i=0;i<dropType.length;i++){
            Map<String,Object> map=new HashMap<>();
            map.put("type",dropType[i]);
            map.put("title",dropHeaders[i]);
            headerList.add(map);
        }
        //创建地点分类列表
        ListView placeListView=new ListView(mReference.get());
        touristMainAdapter=new MyListDropDownAdapter(mReference.get(), Arrays.asList(dropHeaders));
        placeListView.setAdapter(touristMainAdapter);
        popupViews.add(placeListView);
        //创建详细分类列表
        ListView categoryListView=new ListView(mReference.get());
        //touristCategoryAdapter=new MyListDropDownAdapter(mReference.get(),);
        //动态生成控件
        contentView=new LinearLayout(mReference.get());
        contentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentView.setOrientation(LinearLayout.VERTICAL);
        contentView.setGravity(Gravity.CENTER);
        //创建refreshlayout对象
        refreshLayout=new SwipeRefreshLayout(mReference.get());
        LinearLayout.LayoutParams refreshParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        refreshParams.setMargins(0,0,0,4);
        refreshLayout.setLayoutParams(refreshParams);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(()->{
            if(refreshLayout.isRefreshing()){
                //TODO:刷新回调
                //getProvinceData(destinationName);//刷新时回调
            }
        });
        //创建framelayout
        frameLayout=new FrameLayout(mReference.get());
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        listView=new ListView(mReference.get());
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        progressView=new CircleProgressView(mReference.get());
        LinearLayout.LayoutParams progressParams=new LinearLayout.LayoutParams(56,56);
        progressParams.gravity=Gravity.CENTER;
        progressParams.setMargins(0,20,0,0);
        progressView.setLayoutParams(progressParams);
        progressView.setBarColor(getResources().getColor(R.color.colorAccent));
        progressView.setBarWidth(4);
        loadingFailedIV=new ImageView(mReference.get());
        LinearLayout.LayoutParams failParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        failParams.gravity=Gravity.CENTER;
        loadingFailedIV.setLayoutParams(failParams);
        loadingFailedIV.setVisibility(View.INVISIBLE);
        loadingFailedIV.setImageResource(R.drawable.loading_failed);
        emptyIV=new ImageView(mReference.get());
        LinearLayout.LayoutParams emptyParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        emptyParams.gravity=Gravity.CENTER;
        emptyIV.setLayoutParams(failParams);
        emptyIV.setVisibility(View.INVISIBLE);
        emptyIV.setImageResource(R.drawable.empty);
        frameLayout.addView(listView); //lv->frame
        frameLayout.addView(progressView); //progress->frame
        frameLayout.addView(loadingFailedIV); //fail=>frame
        frameLayout.addView(emptyIV);//empty->frame
        refreshLayout.addView(frameLayout); //frame->refresh
        contentView.addView(refreshLayout);//将刷新控件添加到内容父控件中
        dropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews,contentView);
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
