package zjut.salu.share.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.user.Comments;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;

public class CommentActivity extends RxBaseActivity {
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.et_content)EditText et;

    private String topicId;
    private String topicType;
    private OkHttpUtils okHttpUtils;
    @Override
    public int getLayoutId() {
        return R.layout.activity_comment;
    }

    public static void launch(Activity activity,String topicId,String topicType){
        Intent intent=new Intent(activity,CommentActivity.class);
        intent.putExtra("topicId",topicId);
        intent.putExtra("topicType",topicType);
        activity.startActivityForResult(intent,100);
    }
    @Override
    public void initViews(Bundle savedInstanceState) {
        okHttpUtils=new OkHttpUtils();
        Intent intent=getIntent();
        topicId=intent.getStringExtra("topicId");
        topicType=intent.getStringExtra("topicType");

    }

    /**
     * 添加评论
     */
    @OnClick(R.id.tv_add_comment)
    public void addCommentClick(View v){
        Map<String,Object> params=new HashMap<>();
        params.put("userid", PreferenceUtils.getString("userid",null));
        params.put("topicid",topicId);
        params.put("inputText",et.getText().toString());
        params.put("topicType",topicType);
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.SAVE_COMMENTS,params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(String result) {
                        try {
                            JSONObject object=new JSONObject(result);
                            if(object.getString("message").equals("success")){
                                Comments comments=new Comments();
                                comments.setCommentcontent(et.getText().toString());
                                comments.setCommentdate(StringUtils.formatDateReturnDate(new Date(),"yyyy-MM-dd"));
                                comments.setCommentid(object.getString("commentId"));
                                comments.setFromuser(PreferenceUtils.acquireCurrentUser());
                                comments.setTopicid(topicId);
                                comments.setTopictype(topicType);
                                setResult(RESULT_OK,new Intent().putExtra("newComment",comments));
                                et.setText("");
                                finish();
                            }else{
                                ToastUtils.ShortToast(R.string.server_down_text);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.comment_add_text);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
