package zjut.salu.share.activity.destination;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
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
import zjut.salu.share.adapter.product.MyListDropDownAdapter;
import zjut.salu.share.adapter.tourism.TourismAttractionAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.local.TourismAttraction;
import zjut.salu.share.model.local.TourismCategory;
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

    private int currentCategoryId;//当前分类id
    private String currentOption;//当前选项

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
    private List<TourismAttraction> attractionList;//地点集合

    private OkHttpUtils okHttpUtils;
    private ImageLoader imageLoader;
    private TourismAttractionAdapter attractionAdapter;

    private Boolean firstLoad=true;
    private BDLocation location;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tourism_attraction;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        okHttpUtils=new OkHttpUtils();
        imageLoader=ImageLoader.getInstance();
        mReference=new WeakReference<>(this);
        Intent intent=getIntent();
        currentType=intent.getIntExtra("type",0);
        title=intent.getStringExtra("title");
        initGPSData();
        initDropDownList();//初始化筛选条件
    }

    private void initGPSData() {
        LocationClient locationClient = new LocationClient(mReference.get());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setPriority(LocationClientOption.GpsFirst);
        option.setProdName("TouristSharePlatformAndroid");
        locationClient.setLocOption(option);
        //注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                location = bdLocation;
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {
            }
        });
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
        touristMainAdapter.setCheckItem(currentType);
        placeListView.setOnItemClickListener((parent, view, position, id) -> {//方向/类型点击事件
            touristMainAdapter.setCheckItem(position);
            currentType=position;
            type=dropType[position];
            firstLoad=true;
            acquireTouristDetailListData(dropHeaders[position]);
        });
        popupViews.add(placeListView);
        //创建详细分类列表
        categoryListView=new ListView(mReference.get());
        popupViews.add(categoryListView);
        //创建其他筛选条件
        options=new String[]{"默认排序","距离最近","评价最好","人均最低","人均最高"};
        currentOption=options[0];
        ListView optionListView=new ListView(mReference.get());
        optionAdapter=new MyListDropDownAdapter(mReference.get(),Arrays.asList(options));
        optionListView.setAdapter(optionAdapter);
        optionListView.setOnItemClickListener((parent, view, position, id) -> {//其他选项点击事件
            optionAdapter.setCheckItem(position);
            currentOption=options[position];
        });
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
                getListData(type,currentCategoryId,currentOption);
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
    }

    /**
     * 获取列表数据
     * @param categoryId  分类id
     * @param option
     */
    private void getListData(String type,int categoryId,String option){
        Map<String,Object> params=new HashMap<>();
        params.put("type",type);
        params.put("categoryId",categoryId+"");
        params.put("option",option);
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.LOAD_TOURISM_DATA,params);
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
                        runOnUiThread(()->{
                            loadingFailedIV.setVisibility(View.VISIBLE);
                            progressView.stopSpinning();
                            progressView.setVisibility(View.INVISIBLE);
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        attractionList=gson.fromJson(result,new TypeToken<List<TourismAttraction>>(){}.getType());
                        attractionAdapter=new TourismAttractionAdapter(mReference.get(),attractionList,imageLoader,location);
                        listView.setAdapter(attractionAdapter);
                        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,true,true));
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            //TODO:点击事件
                        });
                        if(attractionList.size()==0){
                            emptyIV.setVisibility(View.VISIBLE);
                        }else{
                            emptyIV.setVisibility(View.INVISIBLE);
                        }
                    }
                });
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
                        categoryListView.setOnItemClickListener((parent, view, position, id) -> {//详细分类点击事件
                            touristCategoryAdapter.setCheckItem(position);
                            currentCategoryId=tourismCategories.get(position).getTmcategoryid();
                        });
                        if(firstLoad){
                            getListData(type,tourismCategories.get(0).getTmcategoryid(),options[0]);//获取列表数据
                            firstLoad=false;
                        }else{
                            getListData(type,currentCategoryId,currentOption);//获取列表数据
                        }

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
