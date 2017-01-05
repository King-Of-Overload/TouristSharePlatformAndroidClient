package zjut.salu.share.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;

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
import zjut.salu.share.base.AbsBaseActivity;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.MoveTouchBackUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;

/**
 * 用户反馈界面
 * @Alan
 */
public class UserFeedBackActivity extends RxBaseActivity {
    @Bind(R.id.tv_top_bar_title)TextView mainTitle;//标题
    @Bind(R.id.tv_btn_top_right_optional)TextView rightOptional;//右侧可选按钮
    @Bind(R.id.et_feedback_content)EditText content;//反馈内容
    @Bind(R.id.et_feedback_contact)EditText contactWay;//反馈联系方式
    @Bind(R.id.iv_btn_top_back)ImageButton button;//返回按钮
    private WeakReference<Activity> mReference;
    @Override
    public int getLayoutId() {
        return R.layout.activity_user_feed_back;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mainTitle.setText(getResources().getText(R.string.feed_back_title));
        content.addTextChangedListener(new MyFeedBackTextWatcher());
        if(mReference==null){mReference=new WeakReference<>(this);}
        MoveTouchBackUtils utils=new MoveTouchBackUtils(button,mReference.get());
        utils.bindClickBackListener();
    }

    @Override
    public void initToolBar() {

    }


    /**
     * 发送按钮点击事件
     */
    @OnClick(R.id.tv_btn_top_right_optional)
    public void sendMessageClickEvent(){
        String contentStr=content.getText().toString();
        String contactStr=contactWay.getText().toString();
        if(contentStr.length()==0||("").equals(contentStr)){
            ToastUtils.ShortToast((String) getResources().getText(R.string.feed_back_content_null_text));
            return;
        }else if(contactStr.length()==0||("").equals(contactStr)){
            ToastUtils.ShortToast((String) getResources().getText(R.string.feed_back_contact_null_text));
            return;
        }
        //首先检查网络状态
        Boolean isNetConnected= CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            ToastUtils.ShortToast((String) getText(R.string.no_network_connection));
        }else {
            OkHttpClient client = new OkHttpClient();
            RequestBody dataBody = new FormBody.Builder()
                    .add("feedbackContent", contentStr)
                    .add("feedbackContact", contactStr)
                    .build();
            Request request = new Request.Builder()
                    .url(RequestURLs.SUBMIT_FEEDBACK_URL)
                    .post(dataBody)
                    .build();
            Call call = client.newCall(request);//构造回调对象
            call.enqueue(new SubmitFeedbackResponseCallBack());
        }
    }

   private class SubmitFeedbackResponseCallBack implements Callback {
       @Override
       public void onFailure(Call call, IOException e) {
           ToastUtils.LongToast(getString(R.string.server_down_text));
       }

       @Override
       public void onResponse(Call call, Response response) throws IOException {
           final String replyResult=response.body().string();//返回内容
           String resStr="";//返回信息
           if(null!=response.cacheResponse()){
               resStr=response.cacheResponse().toString();
               Log.i("submitFeedbackStr",resStr);
           }else{
               resStr=response.networkResponse().toString();
               Log.i("submitFeedbackStr",resStr);
           }
           runOnUiThread(() -> {
               if(replyResult.equals("success")){
                   ToastUtils.ShortToast(getString(R.string.send_feed_back_success_text));
                   finish();
               }else{
                   ToastUtils.ShortToast(getString(R.string.send_feed_back_fial_text));
               }
           });
       }
   }

    /**
     * 内容变化时调用
     */
    private class MyFeedBackTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()>0){
                if(rightOptional.getVisibility()!=View.VISIBLE){
                    rightOptional.setText("");
                    rightOptional.setText("发送");
                    rightOptional.setVisibility(View.VISIBLE);
                }
            }else{
                rightOptional.setVisibility(View.GONE);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {}
    }
}
