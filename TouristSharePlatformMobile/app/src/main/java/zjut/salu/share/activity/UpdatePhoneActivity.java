package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.exception.MyException;
import zjut.salu.share.utils.BmobSMSUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;

/**
 * 手机验证界面
 */
public class UpdatePhoneActivity extends RxBaseActivity {
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.et_write_phone)EditText phoneET;
    @Bind(R.id.tv_unreceive_identify)TextView tvUnreceiveIdentify;
    @Bind(R.id.et_sms_captcha)EditText codeET;//验证码

    private static final int RETRY_INTERVAL = 60; //设置一个倒计时时间
    private int time = RETRY_INTERVAL;
    private WeakReference<Activity> mReference=null;
    private String phoneNum="";
    private OkHttpUtils httpUtils=null;
    private static final String TAG="UpdatePhoneActivity";
    @Override
    public int getLayoutId() {
        return R.layout.activity_update_phone;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        httpUtils=new OkHttpUtils();
    }

    /**
     * 获取验证码点击事件
     * @param v
     */
    @OnClick(R.id.btn_next)
    public void acquireVarifyCodeClick(View v){
        phoneNum=phoneET.getText().toString().trim();
        if(StringUtils.isEmpty(phoneNum)){
            ToastUtils.ShortToast(R.string.phone_not_null_text);
        }else if(!StringUtils.isMobileNum(phoneNum)){
            ToastUtils.ShortToast(R.string.phone_incorrect_text);
        }else{
            //发送验证码
            BmobSMS.requestSMSCode(mReference.get(), phoneNum, BmobSMSUtils.UPDATE_PHONE, new RequestSMSCodeListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if(e==null){//验证码发送成功
                        countDown();
                        ToastUtils.LongToast(getString(R.string.send_success_text)+getString(R.string.id_text)+integer+","+getText(R.string.validate_twentity_text));
                    }else{
                        ToastUtils.ShortToast(R.string.server_down_text);
                    }
                }
            });
        }
    }

    /**
     * 验证验证码正确性
     * @param v
     */
    @OnClick(R.id.btn_submit)
    public void submitValidateCodeClick(View v){
        String code=codeET.getText().toString();
        if(StringUtils.isEmpty(code)){
            return;
        }
        BmobSMS.verifySmsCode(mReference.get(),phoneNum, "验证码", new VerifySMSCodeListener() {
            @Override
            public void done(BmobException ex) {
                if(ex==null){//短信验证码已验证成功
                    ToastUtils.ShortToast(R.string.validate_success_text);
                    List<Map<String,Object>> params=new ArrayList<>();
                    Map<String,Object> map=new HashMap<>();
                    map.put("updateType","phone");
                    map.put("phone",phoneNum);
                    map.put("userid", PreferenceUtils.getString("userid",null));
                    params.add(map);
                    Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.UPDATE_USER_INFO_URL);
                    observable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onCompleted() {
                                    Log.i(TAG,"这么快就结束了");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    ToastUtils.ShortToast(R.string.server_down_text);
                                }

                                @Override
                                public void onNext(String s) {
                                    ToastUtils.ShortToast(R.string.update_success_text);
                                    PreferenceUtils.put("phone",phoneNum);
                                    setResult(RESULT_OK,new Intent().putExtra("newPhone",s));
                                    finish();
                                }
                            });
                }else{
                    ToastUtils.ShortToast(R.string.validate_error_text);
                }
            }
        });
    }



    /**
     * 倒计时方法
     */
    private void countDown(){
        new Thread(() -> {
            while(time-- > 0){
                final String unReceive = mReference.get().getString(time+R.string.smssdk_receive_msg);
                runOnUiThread(() -> {
                    tvUnreceiveIdentify.setText(unReceive);
                    tvUnreceiveIdentify.setEnabled(false);
                });

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    try {
                        throw new MyException("呵呵,什么情况");
                    } catch (MyException e1) {
                        e1.printStackTrace();
                    }
                }

            }
            time = RETRY_INTERVAL;
        }).start();
    }

    @Override
    public void initToolBar() {
        mToolBar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolBar.setTitle(getText(R.string.edit_phone_text));
        mToolBar.setNavigationOnClickListener(v ->finish());
    }
}
