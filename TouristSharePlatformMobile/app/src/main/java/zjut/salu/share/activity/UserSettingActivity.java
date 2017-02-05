package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.base.AbsBaseActivity;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.exception.MyException;
import zjut.salu.share.fragment.PersonalSettingFragment;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.MoveTouchBackUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.SweetAlertUtils;
import zjut.salu.share.utils.ToastUtils;

/**
 * 用户设置子集菜单界面
 */
public class UserSettingActivity extends RxBaseActivity{
    private WeakReference<Activity> mReference;
    @Bind(R.id.tv_top_bar_title)TextView title;
    @Bind(R.id.iv_btn_top_back)ImageButton button;
    @Bind(R.id.btn_quit_login)Button quitLoginBtn;
    private OkHttpUtils okHttpUtils=null;
    @Override
    public int getLayoutId() {
        return R.layout.activity_user_setting;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        okHttpUtils=new OkHttpUtils();
        title.setText(getText(R.string.user_setting_title_text));
        //TODO:要根据用户当前登录情况确定退出按钮是否显示
        if(mReference==null){
            mReference=new WeakReference<>(this);
        }
        MoveTouchBackUtils utils=new MoveTouchBackUtils(button,mReference.get());
        utils.bindClickBackListener();
        Boolean loginStatus=PreferenceUtils.getBoolean("loginStatus",false);
        if(!loginStatus){
            quitLoginBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void initToolBar() {

    }

    /**
     * 退出登录按钮
     */
    @OnClick(R.id.btn_quit_login)
    public void quitLoginClickEvent(View v){
        String title=getString(R.string.xiaoyuan_alert_text);
        String content="您确定要退出吗?";
        Drawable drawable=getResources().getDrawable(R.drawable.alert_icon);
        int type=SweetAlertDialog.CUSTOM_IMAGE_TYPE;
        String pText="退出";
        String nText="再等等";
        SweetAlertUtils.showTitleAndContentDialogWithStyle(mReference.get(),title,content,type,drawable,pText,nText,
        new QuitLoginOnSweetConfirmListener(),new QuitLoginOnSweetCancelListener());
    }

    /**
     * 退出登录确定按钮点击事件
     */
    private class QuitLoginOnSweetConfirmListener implements SweetAlertDialog.OnSweetClickListener{
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            Log.i("clickLoginConfirm","退出成功");
            //删除后台用户登录码
            Boolean isNetworkAvailable = CommonUtils.isNetworkAvailable(mReference.get());
            if(isNetworkAvailable ==true){//访问网络退出登录
                List<Map<String,Object>> params=new ArrayList<>();
                Map<String,Object> map=new HashMap<>();
                map.put("userid",PreferenceUtils.getString("userid",null));
                params.add(map);
                try {
                    okHttpUtils.startPostRequestWithParams(params,RequestURLs.REMOVE_USER_LOGIN_CODE_OVERTIME,new QuitLoginResponseCallback());
                } catch (MyException e) {
                    e.printStackTrace();
                }
            }else{
                ToastUtils.ShortToast(R.string.no_network_connection);
            }
            sweetAlertDialog.dismiss();
        }
    }

    /**
     * 退出登录回调事件
     */
    private class QuitLoginResponseCallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {ToastUtils.ShortToast(R.string.server_down_text);}
        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if(response.isSuccessful()){
                final String responseStr=response.body().string();
                runOnUiThread(() -> {
                    if(responseStr.equals("success")){
                        PreferenceUtils.remove("userid","loginStatusCode","loginTime","headerImage","followNum","focusNum");
                        PreferenceUtils.put("loginStatus",false);
                        ToastUtils.ShortToast(R.string.login_quit_success_text);
                        quitLoginBtn.setVisibility(View.GONE);//隐藏退出登录按钮
                    }else if(responseStr.equals("error")){
                        ToastUtils.ShortToast(R.string.server_down_text);
                    }
                });
            }
        }
    }

    /**
     * 退出登录取消按钮点击事件
     */
    private class QuitLoginOnSweetCancelListener implements SweetAlertDialog.OnSweetClickListener{
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            Log.i("clickLoginCancel","点击了取消");
            sweetAlertDialog.dismiss();
        }
    }

    /**
     * 关于作者点击事件
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_about_author)
    public void aboutAuthorClickEvent(View v){
        Intent intent=new Intent(mReference.get(), AboutAuthorActivity.class);
        startActivity(intent);
    }

    /**
     * 关于芳草寻源点击事件
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_about_app)
    public void aboutAppClickEvent(View v){
        Intent intent=new Intent(mReference.get(),AboutAppActivity.class);
        startActivity(intent);
    }

}
