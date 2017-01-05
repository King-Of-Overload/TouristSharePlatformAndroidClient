package zjut.salu.share.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;

public class RegisterActivity extends RxBaseActivity {
    @Bind(R.id.btn_register_close)ImageButton closeRegisterBtn;
    @Bind(R.id.register_username)EditText username;
    @Bind(R.id.register_password)EditText password;
    @Bind(R.id.register_repassword)EditText re_password;
    @Bind(R.id.register_email)EditText email;
    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {}

    @Override
    public void initToolBar() {

    }


    /**
     * 关闭注册界面按钮监听
     * @param v object
     */
    @OnClick(R.id.btn_register_close)
    public void registerCloseBtnEvent(View v){
        finish();
    }

    /**
     * 注册并登录按钮
     * @param v object
     */
    @OnClick(R.id.btn_register_and_login)
    public void registerAndLoginBtnEvent(View v){
        if(validateRegisterForm()){
            Log.i("passRegisterValidate","通过验证");
            try {
                Boolean isNetConnected= CommonUtils.isNetworkAvailable(this);
                if(!isNetConnected){
                    ToastUtils.ShortToast((String) getText(R.string.no_network_connection));
                }else {
                    String usernameText=username.getText().toString();
                    String passwordText=Base64.encodeToString(password.getText().toString().getBytes("UTF-8"),Base64.DEFAULT);
                    String emailText=email.getText().toString();
                    OkHttpClient client=new OkHttpClient();
                    RequestBody dataBody=new FormBody.Builder()
                            .add("username",usernameText)
                            .add("passwordEncode",passwordText)
                            .add("email",emailText)
                            .build();
                    Request request=new Request.Builder()
                            .url(RequestURLs.SUBMIT_REGISTER_DATA)
                            .post(dataBody)
                            .build();
                    Call call=client.newCall(request);
                    call.enqueue(new RegisterResponseCallBack());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册方法的回调事件
     */
    private class RegisterResponseCallBack implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            ToastUtils.LongToast((String) getText(R.string.server_down_text));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                final String resultStr=response.body().string();
                runOnUiThread(() -> {
                    if(resultStr.equals("registerSuccess")){
                        ToastUtils.ShortToast(R.string.register_success_text);
                        finish();
                        // LoginActivity.loginInstance.finish();
                    }else if(resultStr.equals("registerError")){
                        ToastUtils.ShortToast(R.string.register_error_text);
                    }
                });
            }
        }
    }

    /**
     * 验证表单情况
     * @return 结果
     */
    private Boolean validateRegisterForm(){
        Boolean result=false;
        if(TextUtils.isEmpty(username.getText().toString())){
            ToastUtils.ShortToast((String) this.getResources().getText(R.string.register_username_cannot_null));
        }else if(TextUtils.isEmpty(password.getText().toString())){
            ToastUtils.ShortToast((String) this.getResources().getText(R.string.register_pwd_cannot_null));
        }else if(TextUtils.isEmpty(re_password.getText().toString())){
            ToastUtils.ShortToast((String) this.getResources().getText(R.string.register_re_pwd_cannot_null));
        }else if(!password.getText().toString().equals(re_password.getText().toString())){
            ToastUtils.ShortToast((String) this.getResources().getText(R.string.register_pwd_not_same));
        }else if(TextUtils.isEmpty(email.getText().toString())){
            ToastUtils.ShortToast((String) this.getResources().getText(R.string.register_email_cannot_null));
        }else if(!StringUtils.isEmail(email.getText().toString())){
            ToastUtils.ShortToast((String) this.getResources().getText(R.string.email_format_exception));
        }else{
            result=true;
        }
        return result;
    }
}
