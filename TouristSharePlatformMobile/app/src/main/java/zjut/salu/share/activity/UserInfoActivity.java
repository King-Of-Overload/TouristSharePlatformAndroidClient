package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.adapter.userinfo.UserInfoMainPageAdapter;
import zjut.salu.share.base.AbsBaseActivityWithBar;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.SkillAcademy;
import zjut.salu.share.model.SpaceBean;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.TitleBuilder;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.MyPullToRefreshView;
import zjut.salu.share.widget.UnderlineIndicatorView;

/**
 * 用户中心界面
 * @author Salu
 */
public class UserInfoActivity extends RxBaseActivity{
    private static final String TAG="UserInfoActivity";
    private static WeakReference<Activity> mReference=null;
    private Boolean headerLoaded=false;
    //标题栏部分
    private View viewTitle;

    //获取标题栏信息，需要时进行操作
    @Bind(R.id.titlebar_iv_left)ImageView titlebar_iv_left;//后退按钮
    @Bind(R.id.titlebar_tv)TextView titlebar_tv;//用户名标题

    //初始化头部区域
    //主页面区域 添加至列表中作为header的菜单栏
    @Bind(R.id.iv_user_info_head)ImageView iv_user_info_head;//头部背景封面
    @Bind(R.id.plv_user_info)MyPullToRefreshView plv_user_info;
    //底部view
    private View footView;

    @Bind(R.id.iv_empty_user_info)ImageView emptyIV;//空空如也
    @Bind(R.id.iv_loading_failed_user_info)ImageView loadingFailedIV;//加载失败

    //动态加载封面内部的控件布局
    // headerView - 用户信息
    private View user_info_head;//headerView对象
    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_follows;
    private TextView tv_fans;
    private TextView tv_sign;

    // shadow_tab - 顶部悬浮的菜单栏
    private View shadow_user_info_tab;
    private RadioGroup shadow_rg_user_info;
    private UnderlineIndicatorView shadow_uliv_user_info;
    private View user_info_tab;
    private RadioGroup rg_user_info;
    private UnderlineIndicatorView uliv_user_info;
    //顶部下划线view的选项
    @Bind(R.id.rb_main_page)RadioButton mainPageBtn;//主页按钮
    @Bind(R.id.rb_light_strategy)RadioButton lightStrategyBtn;//轻游记
    @Bind(R.id.rb_photos)RadioButton photoBtn;//相册按钮
    @Bind(R.id.rb_foot_step)RadioButton footBtn;//足迹
    // 背景图片最小高度
    private int minImageHeight = -1;
    // 背景图片最大高度
    private int maxImageHeight = -1;

    private int curScrollY;

    private Boolean isCurrentUser=false;//是否为当前用户
    private TripUser user;//用户对象
    private ImageLoader imageLoader=null;
    private OkHttpUtils httpUtils=null;
    private Intent intent=null;

    private String loadType="";//加载类型 mainPage,lightStrategy,album,footStep
    private static final String MAIN_PAGE="mainPage";//主页
    private static final String LIGHT_STRATEGY="lightStrategy";//轻游记
    private static final String ALBUM="album";//相册
    private static final String FOOT_STEP="footStep";//足迹

   private ListView lv;
    //加载数据
    private UserInfoMainPageAdapter mainPageAdapter=null;
    private List<SpaceBean> mainPageList=new ArrayList<>();//主页
    private List<SpaceBean> albumList=new ArrayList<>();//相册数据






    @Override
    public int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loadingFailedIV.setVisibility(View.INVISIBLE);
        emptyIV.setVisibility(View.INVISIBLE);
        mReference=new WeakReference<>(this);
        httpUtils=new OkHttpUtils();
        imageLoader=ImageLoader.getInstance();
        intent=getIntent();
        isCurrentUser=intent.getBooleanExtra("isCurrentUser",false);
        initInjectionView();//初始化title部分注入式控件
    }

    /**
     * 初始化title部分注入式控件
     */
    private void initInjectionView(){
        viewTitle=new TitleBuilder(this)
                .setLeftImage(R.drawable.navigationbar_back_sel)
                .setLeftOnClickListener(new BackBtnClickListener())
                .build();
        initUserInfoHeader();//初始化头部区域
        initTabSection();//初始化tab区域
        loadUserData();//加载用户相关信息
        initListView();

    }

    /**
     * 初始化头部区域
     */
    private void initUserInfoHeader(){
        user_info_head = View.inflate(this, R.layout.user_info_head, null);
        iv_avatar = (ImageView) user_info_head.findViewById(R.id.iv_avatar);
        tv_name = (TextView) user_info_head.findViewById(R.id.tv_name);
        tv_follows = (TextView) user_info_head.findViewById(R.id.tv_follows);
        tv_fans = (TextView) user_info_head.findViewById(R.id.tv_fans);
        tv_sign = (TextView) user_info_head.findViewById(R.id.tv_sign);
    }

    /**
     * 初始化tab区域
     */
    private void initTabSection(){
        shadow_user_info_tab = findViewById(R.id.user_info_tab);
        shadow_rg_user_info= (RadioGroup) findViewById(R.id.rg_user_info);
        shadow_uliv_user_info= (UnderlineIndicatorView) findViewById(R.id.uliv_user_info);
        shadow_rg_user_info.setOnCheckedChangeListener(new TabCheckedChangeListener());
        shadow_uliv_user_info.setCurrentItemWithoutAnim(0);
        // 添加到列表中的菜单栏
        user_info_tab = View.inflate(this, R.layout.user_info_tab, null);
        rg_user_info = (RadioGroup) user_info_tab.findViewById(R.id.rg_user_info);
        uliv_user_info = (UnderlineIndicatorView) user_info_tab.findViewById(R.id.uliv_user_info);
        rg_user_info.setOnCheckedChangeListener(new TabCheckedChangeListener());
        uliv_user_info.setCurrentItemWithoutAnim(0);
    }



    /**
     * 初始化列表区域
     */
    private void initListView(){
        loadType="mainPage";
        initLoadingLayout();
        footView = View.inflate(this, R.layout.footview_loading, null);
        lv = plv_user_info.getRefreshableView();//listview添加刷新布局
        loadMainPageData(MAIN_PAGE);
        plv_user_info.setOnRefreshListener(refreshView -> {
            loadMainPageData(loadType);//刷新数据
        });
        plv_user_info.setOnPlvScrollListener((l, t, oldl, oldt) -> {

            int scrollY = curScrollY = t;

            if(minImageHeight == -1) {
                minImageHeight = iv_user_info_head.getHeight();
            }

            if(maxImageHeight == -1) {
                Rect rect = iv_user_info_head.getDrawable().getBounds();
                maxImageHeight = rect.bottom - rect.top;
            }

            if(minImageHeight - scrollY < maxImageHeight) {
                iv_user_info_head.layout(0, 0, iv_user_info_head.getWidth(),
                        minImageHeight - scrollY);
            } else {
                iv_user_info_head.layout(0,
                        -scrollY - (maxImageHeight - minImageHeight),
                        iv_user_info_head.getWidth(),
                        -scrollY - (maxImageHeight - minImageHeight) + iv_user_info_head.getHeight());
            }
        });
        iv_user_info_head.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if(curScrollY == bottom - oldBottom) {
                iv_user_info_head.layout(0, 0,
                        iv_user_info_head.getWidth(),
                        oldBottom);
            }
        });
        plv_user_info.getRefreshableView().setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),true,true,new MyPullToRefreshViewScrollListener()));
        plv_user_info.getRefreshableView().setOnItemClickListener(new MyListViewItemClickListener());
    }

    /**
     * listview点击事件
     */
    private class MyListViewItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SpaceBean bean= (SpaceBean) parent.getAdapter().getItem(position);
            if(loadType.equals(MAIN_PAGE)){
               bean=mainPageList.get(position-3);
            }else if(loadType.equals(ALBUM)){
                bean=albumList.get(position-3);
            }
            String type=bean.getType();//获取点击的类型
            switch (type){
                case "strategy":{//攻略游记
                    Intent intent=new Intent(mReference.get(),UserStrategyDetailActivity.class);
                    UserStrategy strategy=new UserStrategy();
                    strategy.setUstrategyid(bean.getId());
                    strategy.setUstrategyname(bean.getTitle());
                    strategy.setCoverImage(bean.getCoverImage());
                    strategy.setTripUser(bean.getUser());
                    strategy.setUstrategycontent(bean.getContent());
                    strategy.setUstrategydate(bean.getTime());
                    strategy.setUclickednum(bean.getClickedNum());
                    strategy.setUlikecount(bean.getLikeCount());
                    intent.putExtra("user_strategy",strategy);
                    startActivity(intent);
                    break;
                }
                case "album":{//跳转到相册
                    Intent intent=new Intent(mReference.get(),AlbumDetailActivity.class);
                    intent.putExtra("albumid",bean.getId());
                    intent.putExtra("title",bean.getTitle());
                    startActivity(intent);
                    break;
                }
                case "skillacademy":{//技法学院
                    Intent intent=new Intent(mReference.get(),SkillAcademyDetailActivity.class);
                    SkillAcademy academy=new SkillAcademy();
                    academy.setClickednum(bean.getClickedNum());
                    academy.setUser(bean.getUser());
                    academy.setSkilldate(bean.getTime());
                    academy.setSkillcontent(bean.getContent());
                    academy.setIsessence(bean.getIsessence());
                    academy.setSkilltitle(bean.getTitle());
                    academy.setCoverImage(bean.getCoverImage());
                    intent.putExtra("skill_academy",academy);
                    startActivity(intent);
                    break;
                }
            }
        }
    }

    /**
     * listview滚动监听器
     */
    private class MyPullToRefreshViewScrollListener implements AbsListView.OnScrollListener{
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            iv_user_info_head.layout(0,
                    user_info_head.getTop(),
                    iv_user_info_head.getWidth(),
                    user_info_head.getTop() + iv_user_info_head.getHeight());

            if(user_info_head.getBottom() < viewTitle.getBottom()) {
                shadow_user_info_tab.setVisibility(View.VISIBLE);
                viewTitle.setBackgroundResource(R.drawable.navigationbar_background);
                titlebar_iv_left.setImageResource(R.drawable.navigationbar_back_sel);
                titlebar_tv.setVisibility(View.VISIBLE);
            } else {
                shadow_user_info_tab.setVisibility(View.GONE);
                viewTitle.setBackgroundResource(R.drawable.userinfo_navigationbar_background);
                titlebar_iv_left.setImageResource(R.drawable.userinfo_navigationbar_back_sel);
                titlebar_tv.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 初始化加载布局
     */
    private void initLoadingLayout() {
        ILoadingLayout loadingLayout = plv_user_info.getLoadingLayoutProxy();
        loadingLayout.setPullLabel("");
        loadingLayout.setRefreshingLabel("");
        loadingLayout.setReleaseLabel("");
        loadingLayout.setLoadingDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
    }

    /**
     * radiogroup change事件
     */
    private class TabCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // 同步悬浮和列表中的标题栏状态
            syncRadioButton(group, checkedId);
        }
    }


    /**
     * 同步悬浮和列表中的标题栏状态
     */
    private void syncRadioButton(RadioGroup group, int checkedId) {
        int index = group.indexOfChild(group.findViewById(checkedId));

        if(shadow_user_info_tab.getVisibility() == View.VISIBLE) {
            shadow_uliv_user_info.setCurrentItem(index);
            ((RadioButton)rg_user_info.findViewById(checkedId)).setChecked(true);
            uliv_user_info.setCurrentItemWithoutAnim(index);
        } else {
            uliv_user_info.setCurrentItem(index);
            ((RadioButton)shadow_rg_user_info.findViewById(checkedId)).setChecked(true);
            shadow_uliv_user_info.setCurrentItemWithoutAnim(index);
        }

        //更新数据
        if(checkedId==mainPageBtn.getId()){//主页
            loadType=MAIN_PAGE;
            if(null!=mainPageList&&mainPageList.size()>0){
                mainPageAdapter=new UserInfoMainPageAdapter(mainPageList,mReference.get());
                lv.setAdapter(mainPageAdapter);
            }else{
                loadMainPageData(loadType);
            }
        }else if(checkedId==lightStrategyBtn.getId()){//轻游记
            //TODO:轻游记
        }else if(checkedId==photoBtn.getId()){//相册
            loadType=ALBUM;
            if(null!=albumList&&albumList.size()>0){
                mainPageAdapter=new UserInfoMainPageAdapter(albumList,mReference.get());
                lv.setAdapter(mainPageAdapter);
            }else{
                loadMainPageData(loadType);
            }
        }else if(checkedId==footBtn.getId()){//足迹
            //TODO:足迹
        }
    }

    /**
     * 加载用户相关信息
     */
    private void loadUserData(){
        user=new TripUser();
        if(isCurrentUser){//是当前用户，即自己的个人空间
            SharedPreferences preferences = PreferenceUtils.getPreferences();
            user.setUserid(preferences.getString("userid",null));
            user.setUsername(preferences.getString("username",null));
            user.setHeaderimage(preferences.getString("headerImage",null));
            user.setFocusNum(preferences.getInt("focusNum",0));
            user.setFollowNum(preferences.getInt("followNum",0));
            user.setUsignature(preferences.getString("usignature",null));
            setPersonalUserInfo();
        }else{//加载其他人的信息
            user.setUserid(intent.getStringExtra("userid"));
            setOtherUserInfo();//加载别人的信息
        }
    }

    /**
     * 获取非当前用户信息
     */
    private void setOtherUserInfo(){
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("userid",user.getUserid());
        params.add(map);
        Observable<String> observable=httpUtils.asyncPostRequest(params,RequestURLs.GET_USER_FULL_INFO);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted(){}
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        user=gson.fromJson(result,TripUser.class);
                        tv_name.setText(user.getUsername());
                        titlebar_tv.setText(user.getUsername());
                        imageLoader.displayImage(RequestURLs.MAIN_URL+user.getHeaderimage(),new ImageViewAware(iv_avatar), ImageLoaderOptionUtils.getAvatarOptions());
                        tv_follows.setText(String.valueOf(getString(R.string.pay_attention_text)+user.getFollowNum()));
                        tv_fans.setText(String.valueOf(getString(R.string.fans_text)+user.getFocusNum()));
                        tv_sign.setText(String.valueOf(getText(R.string.usignature_text)+user.getUsignature()));
                    }
                });
    }

    /**
     * 获取当前用户的信息
     */
    private void setPersonalUserInfo(){
        tv_name.setText(user.getUsername());
        titlebar_tv.setText(user.getUsername());
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String headImageURL=RequestURLs.MAIN_URL+user.getHeaderimage();
                subscriber.onNext(headImageURL);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.i(TAG,"头像读取完成");
            }
            @Override
            public void onError(Throwable e) {
                ToastUtils.ShortToast(R.string.no_network_connection);
            }


            @Override
            public void onNext(String s) {
                imageLoader.displayImage(s,new ImageViewAware(iv_avatar), ImageLoaderOptionUtils.getAvatarOptions());
            }
        });

        tv_follows.setText(String.valueOf(getString(R.string.pay_attention_text)+user.getFollowNum()));
        tv_fans.setText(String.valueOf(getString(R.string.fans_text)+user.getFocusNum()));
        tv_sign.setText(String.valueOf(getText(R.string.usignature_text)+user.getUsignature()));
    }

    /**
     * 加载列表的数据
     * 包括游记攻略，技法，相册
     */
    private void loadMainPageData(String type){
        loadType=type;
        String userid=user.getUserid();
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("userid",userid);
        map.put("loadType",loadType);
        map.put("isCurrentUser",String.valueOf(isCurrentUser));
        params.add(map);
        Observable<String> observable=httpUtils.asyncPostRequest(params,RequestURLs.GET_USER_SPACE_DATA_URL);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG,"complete");
                        plv_user_info.onRefreshComplete();//停止刷新

                    }
                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()->{
                            loadingFailedIV.setVisibility(View.VISIBLE);
                            emptyIV.setVisibility(View.INVISIBLE);
                            ToastUtils.ShortToast(R.string.server_down_text);
                        });

                    }
                    @Override
                    public void onNext(String s) {
                        Gson gson=new Gson();
                        switch (loadType){
                            case MAIN_PAGE:{//主页数据
                                mainPageList=gson.fromJson(s,new TypeToken<List<SpaceBean>>(){}.getType());
                                if(mainPageList.size()>0){
                                    mainPageAdapter=new UserInfoMainPageAdapter(mainPageList,mReference.get());
                                    lv.setAdapter(mainPageAdapter);
                                    if(!headerLoaded){
                                        lv.addHeaderView(user_info_head);
                                        lv.addHeaderView(user_info_tab);
                                        headerLoaded=true;
                                    }
                                }else{//没数据
                                    loadingFailedIV.setVisibility(View.INVISIBLE);
                                    emptyIV.setVisibility(View.VISIBLE);
                                }
                                break;
                            }
                            case LIGHT_STRATEGY:{//轻游记数据
                                //TODO:轻游记
                                break;
                            }
                            case ALBUM:{//相册数据
                                albumList=gson.fromJson(s,new TypeToken<List<SpaceBean>>(){}.getType());
                                if(albumList.size()>0){
                                    mainPageAdapter=new UserInfoMainPageAdapter(albumList,mReference.get());
                                    lv.setAdapter(mainPageAdapter);
                                }else{
                                    loadingFailedIV.setVisibility(View.INVISIBLE);
                                    emptyIV.setVisibility(View.VISIBLE);
                                }
                                break;
                            }
                            case FOOT_STEP:{//个人足迹
                                //TODO:个人足迹
                                break;
                            }
                        }
                    }
                });
    }

    /**
     * 后退按钮点击事件
     */
    private class BackBtnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    @Override
    public void initToolBar() {

    }

    public static WeakReference<Activity> getUserInfoReference(){
        return mReference;
    }
}
