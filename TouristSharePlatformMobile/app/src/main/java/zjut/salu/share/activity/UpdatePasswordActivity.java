package zjut.salu.share.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.MoveTouchBackUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;

/**
 * 修改密码界面
 */
public class UpdatePasswordActivity extends RxBaseActivity{
    @Bind(R.id.tv_top_bar_title)TextView titleTV;
    @Bind(R.id.iv_btn_top_back)ImageButton backBtn;
    @Bind(R.id.tv_btn_top_right_optional)TextView sendTV;
    @Bind(R.id.et_old_pwd_content)EditText oldPasswordTV;
    @Bind(R.id.et_new_pwd_content)EditText newPasswordTV;
    @Bind(R.id.et_confirm_pwd_content)EditText confirmPwdTV;

    private WeakReference<Activity> mReference=null;
    private OkHttpUtils httpUtils=null;
    private static final String TAG=UpdatePasswordActivity.class.getSimpleName();
    @Override
    public int getLayoutId() {
        return R.layout.activity_update_password;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        httpUtils=new OkHttpUtils();
        if(sendTV.getVisibility()== View.INVISIBLE){sendTV.setVisibility(View.VISIBLE);}
        sendTV.setOnClickListener(v->{
            String oldPaassword=oldPasswordTV.getText().toString();
            String newPassword=newPasswordTV.getText().toString();
            String confirmPassword=confirmPwdTV.getText().toString();
            if(validatePasswordForm(oldPaassword,newPassword,confirmPassword)){
                List<Map<String,Object>> params=new ArrayList<>();
                Map<String,Object> map=new HashMap<>();
                map.put("updateType","password");
                map.put("userid", PreferenceUtils.getString("userid",null));
                try {
                    map.put("oldPassword",Base64.encodeToString(oldPaassword.getBytes("UTF-8"),Base64.DEFAULT));
                    map.put("newPassword", Base64.encodeToString(newPassword.getBytes("UTF-8"),Base64.DEFAULT));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                params.add(map);
                Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.UPDATE_USER_INFO_URL);
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                                Log.i(TAG,"修改密码方法调用结束");
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtils.ShortToast(R.string.server_down_text);
                            }

                            @Override
                            public void onNext(String result) {
                                if(result.equals("updateSuccess")){
                                    ToastUtils.ShortToast(R.string.update_pwd_success_text);
                                    finish();
                                }else if(result.equals("updatePwdError")){
                                    ToastUtils.ShortToast(R.string.old_pwd_incorrent_text);
                                }
                            }
                        });
            }
        });
    }

    /**
     * 验证表单
     * @return
     */
    private Boolean validatePasswordForm(String oldPwd,String newPwd,String confirmPwd){
        Boolean result=true;
        if(StringUtils.isEmpty(oldPwd)||StringUtils.isEmpty(newPwd)||StringUtils.isEmpty(confirmPwd)){
            ToastUtils.ShortToast(R.string.not_null_text);
            result=false;
        }else if(!newPwd.equals(confirmPwd)){
            ToastUtils.ShortToast(R.string.register_pwd_not_same);
            result=false;
        }
        return result;
    }

    @Override
    public void initToolBar() {
        titleTV.setText(R.string.update_pwd_text);
        MoveTouchBackUtils utils=new MoveTouchBackUtils(backBtn,mReference.get());
        utils.bindClickBackListener();
    }
}
