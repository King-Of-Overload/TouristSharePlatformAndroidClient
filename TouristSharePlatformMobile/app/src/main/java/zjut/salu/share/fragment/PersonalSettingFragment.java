package zjut.salu.share.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.activity.AboutAuthorActivity;
import zjut.salu.share.activity.HomeActivity;
import zjut.salu.share.activity.LoginActivity;
import zjut.salu.share.activity.PersonalInfoActivity;
import zjut.salu.share.activity.SplashActivity;
import zjut.salu.share.activity.UserFeedBackActivity;
import zjut.salu.share.activity.UserOrderListActivity;
import zjut.salu.share.activity.UserSettingActivity;
import zjut.salu.share.config.CuteTouristShareConfig;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.ImageUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;

/**个人中心
 * Created by Alan on 2016/10/16.
 */

public class PersonalSettingFragment extends RxLazyFragment {
    private Context context;
    @Bind(R.id.relative_layout_below_frame)RelativeLayout header_rl_layout;
    @Bind(R.id.btn_login_now)Button loginNowBtn;
    @Bind(R.id.iv_user_avatar)ImageView iv_user_avatar;//用户头像
    @Bind(R.id.tv_login_username)TextView tv_username;//用户昵称
    @Bind(R.id.tv_fans_and_focus)TextView tv_fans_and_focus;//关注粉丝信息
    @Bind(R.id.xref_view_setting)XRefreshView refreshView;
    private Boolean isNetworkAvailable=false;
    private static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);//总内存
    private static int cacheSize = maxMemory / 12;//缓存大小
    public static LruCache<String, Bitmap> mMemoryCache;//缓存对象
    private OkHttpUtils okHttpUtils=null;
    private WeakReference<Activity> mReference=null;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_personal_setting;
    }

    @Override
    public void finishCreateView(Bundle state) {
        okHttpUtils=new OkHttpUtils();
        mReference=new WeakReference<>(getActivity());
        PullRefreshUtils.bindPullRefreshView(refreshView,true,false,new PersonalInfoRefreshViewListener());
        context =getActivity();
        //初始化缓存对象
        mMemoryCache=new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key,Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            }
        };
        Drawable drawable=header_rl_layout.getBackground();
        BitmapDrawable bd= (BitmapDrawable) drawable;
        Bitmap bitmap=bd.getBitmap();
        Bitmap frostGlassBitmap=ImageUtils.addFrostedGlassEffect(context,bitmap,24);
        header_rl_layout.setBackgroundDrawable(new BitmapDrawable(frostGlassBitmap));
    }


    @Override
    public void onResume() {
        super.onResume();
        readLoginInfo();
    }

    /**
     * 下拉刷新回调
     */
    private class PersonalInfoRefreshViewListener implements XRefreshView.XRefreshViewListener{

        @Override
        public void onRefresh() {
            new Handler().postDelayed(() -> {
                isNetworkAvailable=CommonUtils.isNetworkAvailable(mReference.get());
                if(isNetworkAvailable){
                    removeImageCache("avatar_bitmap");
                    readLoginInfo();
                    refreshView.stopRefresh();
                    ToastUtils.ShortToast(R.string.refresh_text);
                }else{
                    refreshView.stopRefresh();
                    ToastUtils.ShortToast(R.string.no_network_connection);
                }
            }, 2000);
        }

        @Override
        public void onLoadMore(boolean isSilence) {}

        @Override
        public void onRelease(float direction) {}

        @Override
        public void onHeaderMove(double headerMovePercent, int offsetY) {}
    }

    /**
     * 读取登录信息
     */
    private void readLoginInfo(){
        SharedPreferences preferences=PreferenceUtils.getPreferences();
        Boolean loginStatus=preferences.getBoolean("loginStatus",false);
        if(loginStatus){
            loginNowBtn.setVisibility(View.GONE);
            tv_username.setVisibility(View.VISIBLE);
            tv_fans_and_focus.setVisibility(View.VISIBLE);
            String username=preferences.getString("username",null);
            String headerImage=preferences.getString("headerImage",null);
            int followNum=preferences.getInt("followNum",0);//粉丝数
            int focusNum=preferences.getInt("focusNum",0);//关注数
            tv_username.setText(username);
            tv_fans_and_focus.setText(focusNum+"关注 | "+followNum+"粉丝");//设置粉丝数
            //读取该用户的头像
            if(!headerImage.equals("images/headerImages/default.jpg")){
                if(getBitmapFromMemCache("avatar_bitmap")!=null){
                    iv_user_avatar.setImageBitmap(getBitmapFromMemCache("avatar_bitmap"));
                }else{//去网络下载
                    //异步读取用户头像
                    isNetworkAvailable= CommonUtils.isNetworkAvailable(context);
                    if(isNetworkAvailable==true){
                        okHttpUtils.startAsyncGetRequest(RequestURLs.MAIN_URL+headerImage,new UserAvatarBinaryResponseCallback());
                    }else{
                        ToastUtils.ShortToast(R.string.no_network_connection);
                    }
                }
            }
        }else{
            removeImageCache("avatar_bitmap");
            loginNowBtn.setVisibility(View.VISIBLE);
            iv_user_avatar.setImageResource(R.drawable.user_avatar_unlogin);
            tv_username.setVisibility(View.GONE);
            tv_fans_and_focus.setVisibility(View.GONE);
        }
    }

    /**
     * 读取用户头像回调函数
     */
    private class UserAvatarBinaryResponseCallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {ToastUtils.ShortToast(R.string.server_down_text);}
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                final byte[] bytes=response.body().bytes();
                ((Activity)context).runOnUiThread(() -> {
                    Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    addBitmapToMemoryCache("avatar_bitmap",bitmap);
                    iv_user_avatar.setImageBitmap(bitmap);
                });
            }

        }
    }

    /**
     * 将位图对象放入缓存
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 取出缓存对象
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    private synchronized void removeImageCache(String key) {
        if (key != null) {
            if (mMemoryCache != null) {
                Bitmap bm = mMemoryCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }

    /**
     * 立即登录按钮点击事件
     */
    @OnClick(R.id.btn_login_now)
    public void loginNowBtnEvent(){
        Intent intent=new Intent(context, LoginActivity.class);
        intent.putExtra("activity_name", HomeActivity.class.getName());
        startActivity(intent);
    }

    /**
     * 我的订单点击事件
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_order_item)
    public void myOrderClickEvent(View v){
        Boolean loginStatus=PreferenceUtils.getBoolean("loginStatus",false);
        if(loginStatus==true){
            Intent intent=new Intent(context, UserOrderListActivity.class);
            startActivity(intent);
        }else{
         ToastUtils.ShortToast(R.string.please_login_first_text);
            Intent intent=new Intent(context,LoginActivity.class);
            intent.putExtra("activity_name",((Activity)context).getClass().getName());
            startActivity(intent);
        }
    }

    /**
     * 个人信息点击事件
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_personal_info)
    public void personalInfoClickEvent(View v){
        Boolean loginStatus=PreferenceUtils.getBoolean("loginStatus",false);
        if(loginStatus){
            Intent intent=new Intent(context, PersonalInfoActivity.class);
            intent.putExtra("isCurrentUser",true);
            startActivity(intent);
        }else{
            ToastUtils.ShortToast(R.string.please_login_first_text);
            Intent intent=new Intent(context,LoginActivity.class);
            intent.putExtra("activity_name",((Activity)context).getClass().getName());
            startActivity(intent);
        }

    }

    /**
     * 意见反馈按钮点击事件
     */
    @OnClick(R.id.rl_layout_btn_feedback)
    public void userFeedBackClickEvent(View v){
        Intent intent=new Intent(context, UserFeedBackActivity.class);
        startActivity(intent);
    }

    /**
     * 设置按钮点击事件
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_settings)
    public void userSettingClickEvent(View v){
        Intent intent=new Intent(context, UserSettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        readLoginInfo();//读取登录信息
    }
}
