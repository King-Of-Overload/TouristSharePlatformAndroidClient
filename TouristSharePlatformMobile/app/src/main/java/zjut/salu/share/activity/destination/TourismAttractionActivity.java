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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyydjk.library.DropDownMenu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.adapter.product.MyConstellationAdapter;
import zjut.salu.share.adapter.product.MyListDropDownAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.TourismCategory;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
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
    private String type="";//当前类型

    private List<View> popupViews = new ArrayList<>();//弹出视图
    private MyListDropDownAdapter touristMainAdapter=null;
    private MyListDropDownAdapter touristCategoryAdapter=null;
    private MyListDropDownAdapter optionAdapter=null;

    private ListView categoryListView;//详细分类列表

    private String[] headers;
    private List<Map<String,Object>> headerList;//美食，旅行
    private List<TourismCategory> tourismCategories=null;//详细分类集合
    private List<String> tourismCategoryArray;//详细分类字符串集合
    private String[] options;//其他选项内容

    private OkHttpUtils okHttpUtils;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tourism_attraction;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        okHttpUtils=new OkHttpUtils();
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
        headers=new String[]{"方向/类型","详细分类","其它选项"};
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
        categoryListView=new ListView(mReference.get());
        popupViews.add(categoryListView);
        //创建其他筛选条件
        options=new String[]{"默认排序","距离最近","评价最好","人均最低","人均最高"};
        ListView optionListView=new ListView(mReference.get());
        optionAdapter=new MyListDropDownAdapter(mReference.get(),Arrays.asList(options));
        optionListView.setAdapter(optionAdapter);
        popupViews.add(optionListView);
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
        progressView.spin();
        //获取详细分类的数据
        acquireTouristDetailListData(title);
        getListData(type,tourismCategories.get(0).getTmcategoryid(),options[0]);
    }

    /**
     * 获取列表数据
     * @param categoryId  分类id
     * @param option
     */
    private void getListData(String type,int categoryId,String option){

    }

    /**
     * 获取详细分类数据
     */
    private void acquireTouristDetailListData(String title){
        List<Map<String,Object>> params=new ArrayList<>();
        switch (title){
            case "美食":{
                type="food";
                break;
            }
            case "酒店":{
                type="hotel";
                break;
            }
            case "景点":{
                type="view";
                break;
            }
            case "购物":{
                type="shop";
                break;
            }
            case "娱乐":{
                type="play";
                break;
            }
            case "生活":{
                type="live";
                break;
            }
        }
        Map<String,Object> map=new HashMap<>();
        map.put("type",type);
        params.add(map);
        Observable<String> observable=okHttpUtils.asyncPostRequest(params, RequestURLs.GET_TOURISM_DETAIL_CATEGORY_BY_TYPE);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()->{
                            loadingFailedIV.setVisibility(View.VISIBLE);
                            emptyIV.setVisibility(View.INVISIBLE);
                            ToastUtils.ShortToast(R.string.server_down_text);
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        tourismCategories=gson.fromJson(result,new TypeToken<List<TourismCategory>>(){}.getType());
                        tourismCategoryArray=new ArrayList<>();
                        for(TourismCategory tc:tourismCategories){
                            tourismCategoryArray.add(tc.getTmcategoryname());
                        }
                        touristCategoryAdapter=new MyListDropDownAdapter(mReference.get(),tourismCategoryArray);
                        categoryListView.setAdapter(touristCategoryAdapter);
                    }
                });
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
