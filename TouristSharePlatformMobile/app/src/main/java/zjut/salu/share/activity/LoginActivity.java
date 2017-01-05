package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.base.AbsBaseActivity;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.exception.MyException;
import zjut.salu.share.model.City;
import zjut.salu.share.model.Provinces;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;

/**
 * 登录界面
 * @Alan さる
 */
public class LoginActivity extends RxBaseActivity {
    @Bind(R.id.login_username)EditText et_username;
    @Bind(R.id.login_password) EditText et_password;
    @Bind(R.id.iv_btn_login_delete_username) ImageButton deleteUserNameBtn;
    @Bind(R.id.iv_login_left)ImageView loginLeftImageView;
    @Bind(R.id.iv_login_right)ImageView loginRightImageView;
    public static LoginActivity loginInstance=null;
    private OkHttpUtils okHttpUtils=null;
    private String prevClassName;
    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    /**
     * 左上角关闭点击事件
     * @param v
     */
    @OnClick(R.id.btn_login_close)
    public void loginCloseListenerEvent(View v){
        if(prevClassName.equals(SplashActivity.class.getName())){
            //ToastUtils.ShortToast("即将返回到主页");
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }else{
            //TODO:返回上一页
            //ToastUtils.ShortToast("即将返回到上一页");
            finish();//返回上一层
        }
    }

    /**
     * 注册按钮点击事件
     * @param v
     */
    @OnClick(R.id.btn_register)
    public void registerBtnListenerEvent(View v){
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * 清除用户名按钮点击事件
     * @param v
     */
    @OnClick(R.id.iv_btn_login_delete_username)
    public void onDeleteBtnClick(View v){
        et_username.setText("");
        et_password.setText("");
        deleteUserNameBtn.setVisibility(View.GONE);
        et_username.setFocusable(true);
        et_username.setFocusableInTouchMode(true);
        et_username.requestFocus();
    }

    /**
     * 登录按钮点击事件
     * @param v
     */
    @OnClick(R.id.btn_login)
    public void loginBtnClickEvent(View v){
        //首先检查网络状态
        Boolean isNetConnected= CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){//没有网络
            ToastUtils.ShortToast((String) this.getResources().getText(R.string.no_network_connection));
            return;
        }
        prepareLogin();
    }

    /**
     * 登录操作
     */
    private void prepareLogin(){
        String username=et_username.getText().toString();
        String password=et_password.getText().toString();
        if(TextUtils.isEmpty(username)){
            ToastUtils.ShortToast((String) this.getResources().getText(R.string.login_username_cannot_null));
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtils.ShortToast((String) this.getResources().getText(R.string.login_pwd_cannot_null));
            return;
        }
        Boolean isNetConnected=CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            ToastUtils.ShortToast((String) getText(R.string.no_network_connection));
            return;
        }else{//开始登录
            try {
                String passwordMD5= StringUtils.getMD5(password);
                List<Map<String,Object>> params=new ArrayList<>();
                Map<String,Object> map=new HashMap<>();
                map.put("username",username);
                map.put("passwordMD5",passwordMD5);
                params.add(map);
                okHttpUtils.startPostRequestWithParams(params, RequestURLs.START_LOGIN_URL,new LoginResponseCallback(username));
            } catch (MyException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 登录操作回调方法
     */
    private class LoginResponseCallback implements Callback{
        private String username;
        public LoginResponseCallback(String username){
            this.username=username;
        }
        @Override
        public void onFailure(Call call, IOException e) {
            ToastUtils.ShortToast(R.string.server_down_text);
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                try {
                    String resultStr=response.body().string();
                    JSONObject resultObject=new JSONObject(resultStr);
                    if("loginSuccess".equals(resultObject.getString("loginStatus"))){
                        TripUser user=extractTripUserFromJSONObject(resultObject);
                        PreferenceUtils.put("loginStatus",true);//修改登录状态
                        PreferenceUtils.put("username",username);
                        PreferenceUtils.put("userid",user.getUserid());
                        PreferenceUtils.put("loginStatusCode",user.getMobilelogincode());//登录状态码
                        PreferenceUtils.put("loginTime",resultObject.getString("loginTime"));//登录时间
                        PreferenceUtils.put("headerImage",user.getHeaderimage());//头像
                        PreferenceUtils.put("followNum",resultObject.getInt("followNum"));//粉丝数
                        PreferenceUtils.put("focusNum",resultObject.getInt("focusNum"));//关注数
                        PreferenceUtils.put("usignature",resultObject.getString("usignature"));//个人签名
                        PreferenceUtils.put("sex",user.getSex());//签名
                        PreferenceUtils.put("email",user.getUseremail());
                        PreferenceUtils.put("phone",user.getPhone());
                        PreferenceUtils.put("city",user.getCity().getProvince().getProvincename()+user.getCity().getCityname());
                        if(prevClassName.equals(SplashActivity.class.getName())){
                            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);//跳转到首页
                            startActivity(intent);
                        }
                        finish();
                    }else if("loginError".equals(resultObject.getString("loginStatus"))){
                        ToastUtils.ShortToast(R.string.login_error_text);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从jsonobject中抽取出用户对象
     * @param object
     * @return
     */
    private TripUser extractTripUserFromJSONObject(JSONObject object) throws JSONException {
        TripUser user=new TripUser();
        user.setUserid(object.getString("userid"));
        user.setMobilelogincode(object.getString("loginStatusCode"));
        user.setHeaderimage(object.getString("headerImage"));
        user.setSex(object.getString("sex"));
        user.setUseremail(object.getString("email"));
        user.setPhone(object.getString("phone"));
        City city=new City();
        Provinces provinces=new Provinces();
        provinces.setProvincename(object.getString("provinceName"));
        city.setProvince(provinces);
        city.setCityname(object.getString("cityName"));
        user.setCity(city);
        return user;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent=getIntent();
        prevClassName=intent.getStringExtra("activity_name");
        loginInstance=this;
        okHttpUtils=new OkHttpUtils();
        et_username.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && et_username.getText().length() > 0) {
                deleteUserNameBtn.setVisibility(View.VISIBLE);
            } else {
                deleteUserNameBtn.setVisibility(View.GONE);
            }
            loginLeftImageView.setImageResource(R.drawable.ic_login11);
            loginRightImageView.setImageResource(R.drawable.ic_login_33);

        });//登录框焦点监听
        et_password.setOnFocusChangeListener((v, hasFocus) -> {
            //更新顶部蒙脸UI
            loginLeftImageView.setImageResource(R.drawable.ic_login_22_hide);
            loginRightImageView.setImageResource(R.drawable.ic_login_33_hide);
        });//密码框焦点监听
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_password.setText("");//用户名改变清空密码
                //如果用户名清空了，清空密码
                if(s.length()>0){
                    //如果有用户名内容的时候，显示删除按钮
                    deleteUserNameBtn.setVisibility(View.VISIBLE);
                }else{
                    //没有内容，删除按钮隐藏
                    deleteUserNameBtn.setVisibility(View.GONE);
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });//用户名框文字改变时调用
    }

    @Override
    public void initToolBar() {

    }

}
