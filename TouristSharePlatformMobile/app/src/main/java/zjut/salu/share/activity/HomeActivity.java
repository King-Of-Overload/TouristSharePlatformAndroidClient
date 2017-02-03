package zjut.salu.share.activity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import net.frakbot.jumpingbeans.JumpingBeans;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.Bind;
import cn.refactor.lib.colordialog.ColorDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.activity.lightstrategy.EditLightStrategy;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.fragment.BestFragment;
import zjut.salu.share.fragment.IndexFragment;
import zjut.salu.share.fragment.PersonalSettingFragment;
import zjut.salu.share.fragment.UserStrategyFragment;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.utils.BroadcastReceiverUtil;
import zjut.salu.share.utils.ColorDialogUtils;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ThemeHelper;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CommonCircleImageView;
import zjut.salu.share.widget.MoreWindow;
import zjut.salu.share.widget.dialog.CardPickerDialog;

/**
 * 芳草寻源fragment载体主活动区
 */
public class HomeActivity extends RxBaseActivity{
    private MoreWindow mMoreWindow;
    private WeakReference<Activity> mReference;
    private int[] normalIcons=new int[]{R.drawable.home_common,R.drawable.user_strategy_common,R.drawable.jingxuan_common,R.drawable.my_common};
    private int[] highlightIcons=new int[]{R.drawable.home_highlight,R.drawable.user_strategy_hoghlight,R.drawable.jingxuan_highlight,R.drawable.my_highlight};
    private String[] tags=new String[]{"home","user_strategy","best","setting"};
    private String[] titles;//文字信息
    //fragment
    private IndexFragment indexFragment=null;
    private UserStrategyFragment strategyFragment=null;
    private BestFragment bestFragment=null;
    private PersonalSettingFragment settingFragment=null;
    //布局对象
    @Bind(R.id.linear_index_tab)LinearLayout indexTab;//首页tab
    @Bind(R.id.linear_user_strategy_tab)LinearLayout userStrategyTab;//当地tab
    @Bind(R.id.linear_user_best_tab)LinearLayout bestTab;//精选tab
    @Bind(R.id.linear_personal_setting_tab)LinearLayout settingTab;//个人中心tab
    private LinearLayout[] layouts=null;//布局数组
    //图片image
    @Bind(R.id.image_iv_index)ImageView indexIv;//首页
    @Bind(R.id.image_iv_user_strategy) ImageView userStrategyIv;//当地
    @Bind(R.id.image_iv_best)ImageView bestIv;//精选
    @Bind(R.id.image_iv_personal_setting)ImageView settingIv;//个人中心
    //文字对象
    @Bind(R.id.tv_index) TextView indexTv;//首页
    @Bind(R.id.tv_user_strategy)TextView userStrategyTv;//当地
    @Bind(R.id.tv_user_best)TextView bestTv;//精选
    @Bind(R.id.tv_personal_setting)TextView settingTv;//个人中心
    @Bind(R.id.toolbar_home)Toolbar mToolBar;

    @Bind(R.id.search_view) MaterialSearchView mSearchView;

    @Bind(R.id.linear_pop_up)LinearLayout popView;

    private CommonCircleImageView drawerAvatarIV;//抽屉头像
    private TextView drawerUsernameTV;//抽屉用户名
    private TextView drawerEmailTV;//抽屉邮箱
    private TextView drawerSignatureTV;//抽屉签名

    @Bind(R.id.toolbar_user_avatar)CommonCircleImageView toolAvatarIV;//工具栏头像
    @Bind(R.id.tv_toolbar_user_name)TextView toolUsernameTV;//工具栏用户名
    private SharedPreferences preferences;
    Boolean isNetWorkActive=false;
    private OkHttpUtils okHttpUtils=null;//网络封装类
    private Drawer drawer=null;
    private HomeDrawerInfoChangeBroadcaster home_receiver;
    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        popView.setOnClickListener(this::showPopWindow);//绑定底部pop
        // 注册更新界面广播接收者
        home_receiver=new HomeDrawerInfoChangeBroadcaster();
        IntentFilter intentFilter=new IntentFilter(BroadcastReceiverUtil.HOME_DRAWER_RECEIVER);
        registerReceiver(home_receiver,intentFilter);
        mReference=new WeakReference<>(this);
        okHttpUtils=new OkHttpUtils();
        String home=getResources().getString(R.string.homeText);
        String userStrategy=getResources().getString(R.string.user_strategy_text);
        String userBest=getResources().getString(R.string.best_text);
        String userSetting=getResources().getString(R.string.personal_setting_text);
        titles=new String[]{home,userStrategy,userBest,userSetting};
        layouts=new LinearLayout[]{indexTab,userStrategyTab,bestTab,settingTab};
        //为底部的linearlayout绑定监听器，以实现tab点击切换
        bindListenerForBottomLinearTab();
        setCurrentPageSetting(0);
        initSearchView();
        //判断登录状态
        preferences=PreferenceUtils.getPreferences();
        Boolean loginStatus=preferences.getBoolean("loginStatus",false);
        if(loginStatus){//处在登录状态
            String loginTime=preferences.getString("loginTime",null);//上次登录时间
            DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date lastTimeDate=format.parse(loginTime);//上次登录时间
                Date currentTimeDate=format.parse(format.format(new Date()));//当前时间
                long subMills=currentTimeDate.getTime()-lastTimeDate.getTime();//获得微秒差值
                long days=subMills/(1000*60*60*24);//算出天数差
                if(days<=20.0){//判断时间,如果小于等于20天，有效
                    //访问网络，更新最近更新时间，并比对登陆码是否一致，并更新登陆码,更新preferences
                    String userid=preferences.getString("userid",null);
                    String loginStatusCode=preferences.getString("loginStatusCode",null);
                    List<Map<String,Object>> params=new ArrayList<>();
                    Map<String,Object> map=new HashMap<>();
                    map.put("userid",userid);map.put("loginStatusCode",loginStatusCode);
                    params.add(map);
                    isNetWorkActive=CommonUtils.isNetworkAvailable(mReference.get());
                    if(isNetWorkActive){
                        okHttpUtils.startPostRequestWithParams(params,RequestURLs.CHECK_USER_LOGIN_STATUS_URL,new CheckLoginStatusCallBack());
                    }else{
                        ToastUtils.ShortToast(R.string.no_network_connection);
                        updateDrawerUI(PreferenceUtils.getBoolean("loginStatus",false));
                    }
                }else{//大于20天，无效
                    //访问网络，消去登录码记录
                    String userid=preferences.getString("userid",null);
                    List<Map<String,Object>> params=new ArrayList<>();
                    Map<String,Object> map=new HashMap<>();
                    map.put("userid",userid);
                    params.add(map);
                    isNetWorkActive= CommonUtils.isNetworkAvailable(this);
                    if(isNetWorkActive){
                        okHttpUtils.startPostRequestWithParams(params, RequestURLs.REMOVE_USER_LOGIN_CODE_OVERTIME,new LoginOverTimeResponse());
                    }else{
                        ToastUtils.ShortToast(R.string.no_network_connection);//没有网络
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 初始化搜索区
     */
    private void initSearchView(){
        //初始化SearchBar
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.custom_cursor);
        mSearchView.setEllipsize(true);
       // mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener()
        {

            @Override
            public boolean onQueryTextSubmit(String query)
            {

                TotalStationSearchActivity.launch(HomeActivity.this, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {

                return false;
            }
        });
    }


    /**
     * 主界面用户信息更新广播接收者
     */
    class HomeDrawerInfoChangeBroadcaster extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean loginStatus=PreferenceUtils.getBoolean("loginStatus",false);
            updateDrawerUI(loginStatus);
        }
    }

    /**
     * 更新drawer界面
     * @param loginStatus 登录状态
     */
    private void updateDrawerUI(Boolean loginStatus){
        if(loginStatus){
            if(toolAvatarIV.getVisibility()==View.GONE){toolAvatarIV.setVisibility(View.VISIBLE);}
            if(toolUsernameTV.getVisibility()==View.GONE){toolUsernameTV.setVisibility(View.VISIBLE);}
            if(drawerEmailTV.getVisibility()==View.GONE){drawerEmailTV.setVisibility(View.VISIBLE);}
            TripUser user=PreferenceUtils.acquireCurrentUser();
            if(user.getHeaderimage().equals("images/headerImages/default.jpg")){
                toolAvatarIV.setImageResource(R.drawable.ico_user_default);
                drawerAvatarIV.setImageResource(R.drawable.ico_user_default);
            }else{
                   Glide.with(HomeActivity.this)//头像加载
                           .load(RequestURLs.MAIN_URL+user.getHeaderimage())
                           .centerCrop()
                           .dontAnimate()
                           .placeholder(R.drawable.ico_user_default)
                           .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                           .into(toolAvatarIV);
                   Glide.with(HomeActivity.this)//头像加载
                           .load(RequestURLs.MAIN_URL+user.getHeaderimage())
                           .centerCrop()
                           .dontAnimate()
                           .placeholder(R.drawable.ico_user_default)
                           .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                           .into(drawerAvatarIV);
            }
            toolUsernameTV.setText(user.getUsername());//TODO:此处有BUG
            int size=toolUsernameTV.getText().toString().length();
            JumpingBeans jumpingBeans = JumpingBeans.with(toolUsernameTV)
                    .makeTextJump(0, 1)
                    .setIsWave(false)
                    .setLoopDuration(1000)
                    .build();
            drawerUsernameTV.setText(user.getUsername());
            drawerEmailTV.setText(user.getUseremail());
            drawerSignatureTV.setText(user.getUsignature());
        }else{
            toolAvatarIV.setImageResource(R.mipmap.ic_launcher);
            toolUsernameTV.setText(R.string.app_name);
            //mToolBar.setTitle(getString(R.string.app_name));
            drawerAvatarIV.setImageResource(R.drawable.ico_user_default);
            drawerUsernameTV.setText(R.string.plese_login_text);
            drawerEmailTV.setVisibility(View.GONE);
            drawerSignatureTV.setText(R.string.after_login_signature_text);
        }
    }




    @Override
    public void initToolBar() {
        //实例化drawer内部控件
        View drawerView= LayoutInflater.from(this).inflate( R.layout.layout_navigation_header,null);
        drawerAvatarIV= (CommonCircleImageView) drawerView.findViewById(R.id.user_avatar_view);
        drawerUsernameTV= (TextView) drawerView.findViewById(R.id.user_name);
        drawerEmailTV= (TextView) drawerView.findViewById(R.id.tv_drawer_email);
        drawerSignatureTV= (TextView) drawerView.findViewById(R.id.user_other_info);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        drawer=new DrawerBuilder()
                .withActionBarDrawerToggleAnimated(true)
                .withActivity(this)
                .withToolbar(mToolBar)
                .withHeader(drawerView)
                .withDrawerItems(ThemeHelper.initDrawerContent())
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {//抽屉菜单点击事件
                    Boolean loginStatus=PreferenceUtils.getBoolean("loginStatus",false);
                    int tag= (int) drawerItem.getIdentifier();
                    switch (tag){
                        case 3:{
                            //TODO:我的收藏
                            break;
                        }
                        case 4:{
                            //TODO:历史记录

                            break;
                        }
                        case 5:{//我的关注
                            new Handler().postDelayed(() -> {
                                if(loginStatus){
                                    Intent intent=new Intent(mReference.get(),FriendsActivity.class);
                                    startActivity(intent);
                                }else{
                                    Intent intent=new Intent(mReference.get(),LoginActivity.class);
                                    intent.putExtra("activity_name", mReference.get().getClass().getName());
                                    startActivity(intent);
                                }
                            },500);
                            break;
                        }
                        case 6:{//好友动态
                            new Handler().postDelayed(() -> {
                                if(loginStatus){
                                    Intent intent=new Intent(mReference.get(),UserInfoActivity.class);
                                    intent.putExtra("isCurrentUser",true);
                                    startActivity(intent);
                                }else{
                                    Intent intent=new Intent(mReference.get(),LoginActivity.class);
                                    intent.putExtra("activity_name", mReference.get().getClass().getName());
                                    startActivity(intent);
                                }
                            },500);
                            break;
                        }
                        case 7:{
                            new Handler().postDelayed(()->{
                                Intent intent=new Intent(mReference.get(),ExchangeCurrencyActivity.class);
                                startActivity(intent);
                            },500);
                            break;
                        }
                        case 8:{//我的订单
                           new Handler().postDelayed(() -> {
                               if(loginStatus){
                                   Intent intent=new Intent(mReference.get(), UserOrderListActivity.class);
                                   startActivity(intent);
                               }else{
                                   ToastUtils.ShortToast(R.string.please_login_first_text);
                                   Intent intent=new Intent(mReference.get(),LoginActivity.class);
                                   intent.putExtra("activity_name",mReference.get().getClass().getName());
                                   startActivity(intent);
                               }
                           },500);
                            break;
                        }
                        case 9:{
                            //TODO:主题选择
                            //主题选择
                            CardPickerDialog dialog = new CardPickerDialog();
                            //dialog.setClickListener(this);
                            dialog.show(getFragmentManager(), CardPickerDialog.TAG);
                            break;
                        }
                        case 10:{
                            //TODO:我的消息
                            break;
                        }
                        case 11:{//设置与帮助
                            new Handler().postDelayed(() -> {
                                Intent intent=new Intent(mReference.get(), UserSettingActivity.class);
                                startActivity(intent);
                            },500);
                            break;
                        }
                    }
                    if(drawer.isDrawerOpen()){drawer.closeDrawer();}
                    return true;
                })
                .build();
        drawer.setGravity(Gravity.START);
        drawer.setFullscreen(false);
        Boolean loginStatus=preferences.getBoolean("loginStatus",false);
        updateDrawerUI(loginStatus);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(home_receiver);
        super.onDestroy();
    }

    /**
     * 检查登录状态
     */
    private class CheckLoginStatusCallBack implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            ToastUtils.ShortToast(R.string.server_down_text);
            runOnUiThread(()-> updateDrawerUI(PreferenceUtils.getBoolean("loginStatus",false)));
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                try {
                    String responseStr=response.body().string();
                    final JSONObject jsonObject=new JSONObject(responseStr);
                    final String checkStatus=jsonObject.getString("checkStatus");
                    runOnUiThread(() -> {
                        if(checkStatus.equals("legal")){//合法
                            try {
                                String userid=jsonObject.getString("userid");
                                String loginStatusCode=jsonObject.getString("loginStatusCode");
                                String loginTime=jsonObject.getString("loginTime");
                                String username=jsonObject.getString("username");
                                String headerImage=jsonObject.getString("headerImage");
                                String sex=jsonObject.getString("sex");
                                PreferenceUtils.put("loginStatus",true);//修改登录状态
                                PreferenceUtils.put("username",username);
                                PreferenceUtils.put("userid",userid);
                                PreferenceUtils.put("loginStatusCode",loginStatusCode);//登录状态码
                                PreferenceUtils.put("loginTime",loginTime);//登录时间
                                PreferenceUtils.put("headerImage",headerImage);
                                PreferenceUtils.put("sex",sex);
                                updateDrawerUI(PreferenceUtils.getBoolean("loginStatus",false));
                                ToastUtils.ShortToast(username+getString(R.string.login_success_welcome_text));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(checkStatus.equals("illegal")){//不合法
                            PreferenceUtils.remove("userid","loginStatusCode","loginTime","headerImage","followNum","focusNum","usignature","sex","email","city","phone");
                            PreferenceUtils.put("loginStatus",false);
                            ToastUtils.ShortToast(R.string.illegal_login_text);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * 因登录时间过期注销登录回调方法
     * 清除所有preference，提示过期，重新登录
     */
    private class LoginOverTimeResponse implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {ToastUtils.ShortToast(R.string.server_down_text);}
        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if(response.isSuccessful()){
                final String responseStr=response.body().string();
                if(!responseStr.equals("")){
                    runOnUiThread(() -> {
                        if(responseStr.equals("success")){
                            PreferenceUtils.remove("userid","loginStatusCode","loginTime","headerImage","followNum","focusNum");
                            PreferenceUtils.put("loginStatus",false);
                            ToastUtils.LongToast(R.string.login_over_time);
                        }else if(responseStr.equals("error")){
                            ToastUtils.ShortToast(R.string.server_down_text);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
//底部按钮改变时调用
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    /**
     * 底部fragment绑定点击监听器
     */
    private void bindListenerForBottomLinearTab(){
        for(LinearLayout layout:layouts){
            layout.setOnClickListener(new BottomLinearTabClickListener());
        }
    }


    /**
     * 底部tab的监听事件
     */
    private class BottomLinearTabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            resetTabTextAndImageView();//重置图片与文字
            switch (v.getId()){
                case R.id.linear_index_tab:{//首页
                    if(mToolBar.getVisibility()==View.GONE){mToolBar.setVisibility(View.VISIBLE);}
                    if(mSearchView.getVisibility()==View.GONE){mSearchView.setVisibility(View.VISIBLE);}
                    setCurrentPageSetting(0);
                    break;
                }
                case R.id.linear_user_strategy_tab:{//当地
//                    if(mToolBar.getVisibility()==View.GONE){mToolBar.setVisibility(View.VISIBLE);}
//                    if(mSearchView.getVisibility()==View.GONE){mSearchView.setVisibility(View.VISIBLE);}
                    if(mToolBar.getVisibility()==View.VISIBLE){mToolBar.setVisibility(View.GONE);}
                    if(mSearchView.getVisibility()==View.VISIBLE){mSearchView.setVisibility(View.GONE);}
                    setCurrentPageSetting(1);
                    break;
                }
                case R.id.linear_user_best_tab:{//精选
                    if(mToolBar.getVisibility()==View.GONE){mToolBar.setVisibility(View.VISIBLE);}
                    if(mSearchView.getVisibility()==View.GONE){mSearchView.setVisibility(View.VISIBLE);}
                    setCurrentPageSetting(2);
                    break;
                }
                case R.id.linear_personal_setting_tab:{//设置与个人中心
                    if(mToolBar.getVisibility()==View.VISIBLE){mToolBar.setVisibility(View.GONE);}
                    if(mSearchView.getVisibility()==View.VISIBLE){mSearchView.setVisibility(View.GONE);}
                    setCurrentPageSetting(3);
                    break;
                }
            }
        }
    }

    /**
     * 点击tab时调用此处对底部与fragment做出改变
     */
    private void setCurrentPageSetting(int position){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(R.anim.fragment_alpha_in,R.anim.fragment_alpha_out);
        hideAllFragment(transaction);
        switch (position){
            case 0:{//首页
                if(indexFragment==null){
                    indexFragment=new IndexFragment();
                    transaction.add(R.id.real_content,indexFragment,tags[0]);
                }else{
                    transaction.show(indexFragment);
                }
                indexTv.setTextColor(getResources().getColor(R.color.cyan));
                indexIv.setImageResource(highlightIcons[0]);
                break;
            }
            case 1:{//当地
                if(strategyFragment==null){
                    strategyFragment=new UserStrategyFragment();
                    transaction.add(R.id.real_content,strategyFragment,tags[1]);
                }else{
                    transaction.show(strategyFragment);
                }
                userStrategyTv.setTextColor(getResources().getColor(R.color.cyan));
                userStrategyIv.setImageResource(highlightIcons[1]);
                break;
            }
            case 2:{//精选
                if(bestFragment==null){
                    bestFragment=new BestFragment();
                    transaction.add(R.id.real_content,bestFragment,tags[2]);
                }else{
                    transaction.show(bestFragment);
                }
                bestTv.setTextColor(getResources().getColor(R.color.cyan));
                bestIv.setImageResource(highlightIcons[2]);
                break;
            }
            case 3:{//个人中心
                if(settingFragment==null){
                    settingFragment=new PersonalSettingFragment();
                    transaction.add(R.id.real_content,settingFragment,tags[3]);
                }else{
                    transaction.show(settingFragment);
                }
                settingTv.setTextColor(getResources().getColor(R.color.cyan));
                settingIv.setImageResource(highlightIcons[3]);
                break;
            }
        }
        transaction.commit();
    }

    /**
     * 隐藏所有fragment
     */
    private void hideAllFragment(FragmentTransaction transaction){
        if(indexFragment!=null){
            transaction.hide(indexFragment);
        }
        if(strategyFragment!=null){
            transaction.hide(strategyFragment);
        }
        if(bestFragment!=null){
            transaction.hide(bestFragment);
        }
        if(settingFragment!=null){
            transaction.hide(settingFragment);
        }
    }

    /**
     * 重置图片与文字
     */
    private void resetTabTextAndImageView(){
        indexTv.setTextColor(getResources().getColor(R.color.black_alpha));
        indexIv.setImageResource(normalIcons[0]);
        userStrategyTv.setTextColor(getResources().getColor(R.color.black_alpha));
        userStrategyIv.setImageResource(normalIcons[1]);
        bestTv.setTextColor(getResources().getColor(R.color.black_alpha));
        bestIv.setImageResource(normalIcons[2]);
        settingTv.setTextColor(getResources().getColor(R.color.black_alpha));
        settingIv.setImageResource(normalIcons[3]);
    }

    /**
     * 退出app前确认操作
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(drawer.isDrawerOpen()){
            drawer.closeDrawer();//关闭抽屉
            return false;
        }else{
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if (mReference == null) {
                mReference = new WeakReference<>(this);
            }
            int color = getResources().getColor(R.color.top_bar_bg);
            String titleText = (String) getResources().getText(R.string.quit_alert_text);
            String contentText = (String) getResources().getText(R.string.are_you_sure_quit);
            String quitText = (String) getResources().getText(R.string.quit_confirm_text);
            String cancelText = (String) getResources().getText(R.string.quit_later_text);
            ColorDialogUtils.showColorfulDialog(mReference.get(), color, titleText, contentText, null, quitText,
                    cancelText, new QuitAppPositiveListener(), new QuitAppNegativeListener());
        }
            return true;
        }
       // return super.onKeyDown(keyCode, event);
    }


    /**
     * 退出app点击确认监听器
     */
    private class QuitAppPositiveListener implements ColorDialog.OnPositiveListener{
        @Override
        public void onClick(ColorDialog colorDialog) {
            colorDialog.dismiss();
            finish();
        }
    }

    /**
     * 退出app点击取消监听器
     */
    private class QuitAppNegativeListener implements ColorDialog.OnNegativeListener{
        @Override
        public void onClick(ColorDialog colorDialog) {
            colorDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_page,menu);
        MenuItem item = menu.findItem(R.id.id_action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示弹出窗口
     * @param view
     */
    private void showPopWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this, v -> {
                switch (v.getId()){
                    case R.id.more_window_local:{//旅行笔记
                        if(PreferenceUtils.getBoolean("loginStatus",false)){
                            Intent intent=new Intent(mReference.get(),EditLightStrategy.class);
                            intent.putExtra("currentUser",PreferenceUtils.acquireCurrentUser());
                            startActivity(intent);
                        }else{
                            ToastUtils.ShortToast(R.string.please_login_first_text);
                            Intent intent=new Intent(mReference.get(),LoginActivity.class);
                            intent.putExtra("activity_name",mReference.get().getClass().getName());
                            startActivity(intent);
                        }
                        break;
                    }
                    case R.id.more_window_online:{//发相册
                        break;
                    }
                    case R.id.more_window_delete:{//小视频
                        break;
                    }
                }
            });
            mMoreWindow.init();
        }

        mMoreWindow.showMoreWindow(view,100);
    }

    //    @Override
//    public void onClick(View v) {//底部菜单点击事件
//        switch (v.getId()) {
//            case R.id.more_window_local:
//                break;
//            case R.id.more_window_online:
//                break;
//            case R.id.more_window_delete:
//                break;
//
//            default:
//                break;
//        }
//    }

}


