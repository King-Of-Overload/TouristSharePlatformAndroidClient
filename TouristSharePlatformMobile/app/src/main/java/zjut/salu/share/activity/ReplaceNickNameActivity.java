package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.MoveTouchBackUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;

/**
 * 更换昵称界面
 */
public class ReplaceNickNameActivity extends RxBaseActivity{
    @Bind(R.id.tv_top_bar_title)TextView titleTV;
    @Bind(R.id.iv_btn_top_back)ImageButton backBtn;
    @Bind(R.id.tv_btn_top_right_optional)TextView sendTV;

    @Bind(R.id.et_change_nickname_content)EditText contentTV;//昵称编辑框

    private OkHttpUtils httpUtils=null;
    private String updateType="";

    @Override
    public int getLayoutId() {
        return R.layout.activity_replace_nick_name;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        WeakReference<Activity> mReference = new WeakReference<>(this);
        Intent intent=getIntent();
        updateType=intent.getStringExtra("updateType");
        if(updateType.equals("nickname")){
            titleTV.setText(R.string.change_nick_name_text);
            String nickName=intent.getStringExtra("nickname");
            contentTV.setText(nickName);
        }else if(updateType.equals("email")){
            titleTV.setText(R.string.change_email_text);
            String mail=intent.getStringExtra("email");
            contentTV.setHint(getString(R.string.input_new_mail_text));
            contentTV.setText(mail);
        }else if(updateType.equals("signature")){
            titleTV.setText(R.string.update_signature_text);
            contentTV.setHint(R.string.input_new_signature_text);
            String signature=intent.getStringExtra("signature");
            contentTV.setText(signature);
        }
        MoveTouchBackUtils utils=new MoveTouchBackUtils(backBtn, mReference.get());
        utils.bindClickBackListener();
        sendTV.setVisibility(View.VISIBLE);
        sendTV.setText(getString(R.string.save_text));
        httpUtils=new OkHttpUtils();
    }

    /**
     * 昵称保存
     * @param v
     */
    @OnClick(R.id.tv_btn_top_right_optional)
    public void saveClick(View v){
        Boolean isNetWorkAvailable = CommonUtils.isNetworkAvailable(this);
        if(isNetWorkAvailable){
            if(updateType.equals("nickname")){//修改昵称
                String newNickName=contentTV.getText().toString();
                if(("").equals(newNickName)){
                    ToastUtils.ShortToast(R.string.nickname_empty_text);
                    return;
                }
                List<Map<String,Object>> params=new ArrayList<>();
                Map<String,Object> map=new HashMap<>();
                map.put("updateType","nickname");
                map.put("userid", PreferenceUtils.getString("userid",null));
                map.put("newNickName",newNickName);
                params.add(map);
                Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.UPDATE_USER_INFO_URL);
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                                Log.i(ReplaceNickNameActivity.class.getSimpleName(),"success");
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtils.ShortToast(R.string.server_down_text);
                            }

                            @Override
                            public void onNext(String s) {
                                ToastUtils.ShortToast(R.string.update_success_text);
                                PreferenceUtils.put("username",s);
                                setResult(RESULT_OK,new Intent().putExtra("newNickname",contentTV.getText().toString()));
                                finish();
                            }
                        });
            }else if(updateType.equals("email")){
                String newMail=contentTV.getText().toString();
                if(StringUtils.isEmail(newMail)){
                    List<Map<String,Object>> params=new ArrayList<>();
                    Map<String,Object> map=new HashMap<>();
                    map.put("updateType","email");
                    map.put("userid", PreferenceUtils.getString("userid",null));
                    map.put("newEmail",newMail);
                    params.add(map);
                    Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.UPDATE_USER_INFO_URL);
                    observable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onCompleted() {Log.i(ReplaceNickNameActivity.class.getSimpleName(),"success");}

                                @Override
                                public void onError(Throwable e) {
                                    ToastUtils.ShortToast(R.string.server_down_text);
                                }

                                @Override
                                public void onNext(String s) {
                                    ToastUtils.ShortToast(R.string.update_success_text);
                                    PreferenceUtils.put("email",s);
                                    setResult(RESULT_OK,new Intent().putExtra("newMail",contentTV.getText().toString()));
                                    finish();
                                }
                            });
                }else{
                    ToastUtils.ShortToast(R.string.email_format_exception);
                }
            }else if(updateType.equals("signature")){//修改签名
                String newSignature=contentTV.getText().toString();
                if(StringUtils.isEmpty(newSignature)){
                   ToastUtils.ShortToast(R.string.signature_not_empty_text);
                    return;
                }
                List<Map<String,Object>> params=new ArrayList<>();
                Map<String,Object> map=new HashMap<>();
                map.put("updateType","signature");
                map.put("userid", PreferenceUtils.getString("userid",null));
                map.put("newSignature",newSignature);
                params.add(map);
                Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.UPDATE_USER_INFO_URL);
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {Log.i(ReplaceNickNameActivity.class.getSimpleName(),"success");}

                            @Override
                            public void onError(Throwable e) {
                                ToastUtils.ShortToast(R.string.server_down_text);
                            }

                            @Override
                            public void onNext(String s) {
                                ToastUtils.ShortToast(R.string.update_success_text);
                                PreferenceUtils.put("usignature",s);
                                setResult(RESULT_OK,new Intent().putExtra("newSignature",contentTV.getText().toString()));
                                finish();
                            }
                        });
            }
        }else{
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }

    @Override
    public void initToolBar() {
    }

}
