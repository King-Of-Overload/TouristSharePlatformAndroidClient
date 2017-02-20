package zjut.salu.share.fragment.banggumi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
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
import zjut.salu.share.activity.LoginActivity;
import zjut.salu.share.activity.user.CommentActivity;
import zjut.salu.share.adapter.comment.CommentAdapter;
import zjut.salu.share.fragment.RxLazyFragment;
import zjut.salu.share.model.user.Comments;
import zjut.salu.share.utils.ConstantUtil;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.MyNestedListView;
import zjut.salu.share.widget.comment.CommentItemView;

import static android.app.Activity.RESULT_OK;

/**小视频评论fragment
 * Created by Salu on 2017/1/29.
 */

public class BanggumiCommentFragment extends RxLazyFragment implements CommentItemView.OnCommentListener{
    @Bind(R.id.recycle)MyNestedListView listView;
    @Bind(R.id.comment_view)View mCommentView;
    @Bind(R.id.edit)EditText et;
    public Boolean isComment=false;//是否为评论
    private WeakReference<MyNestedListView> listViewReference;
    private String banggumeId;
    private CommentAdapter adapter;
    private Context context;
    private List<Comments> commentsList;
    private OkHttpUtils okHttpUtils;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_banggumi_comment;
    }


    @Override
    public void finishCreateView(Bundle state) {
        context=getActivity();
        okHttpUtils=new OkHttpUtils();
        listViewReference=new WeakReference<>(listView);
        banggumeId=getArguments().getString(ConstantUtil.BANGGUMI_ID);
        Map<String,Object> params=new HashMap<>();
        params.put("type","banggume");
        params.put("topicid",banggumeId);
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.GET_COMMENTS,params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        commentsList=gson.fromJson(result,new TypeToken<List<Comments>>(){}.getType());
                        adapter=new CommentAdapter(commentsList,context,mCommentView);
                        listView.setAdapter(adapter);
                    }
                });
    }

    public static BanggumiCommentFragment newInstance(String bangumiId)
    {
        BanggumiCommentFragment fragment = new BanggumiCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantUtil.BANGGUMI_ID, bangumiId);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 添加评论
     */
    @OnClick(R.id.tv_add_comment)
    public void addComentClick(View v){
        if(PreferenceUtils.getBoolean("loginStatus",false)){
            CommentActivity.launch((Activity) context,banggumeId,"banggume");
        }else{
            ToastUtils.ShortToast(R.string.please_login_first_text);
            Intent intent=new Intent(context,LoginActivity.class);
            intent.putExtra("activity_name",context.getClass().getName());
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK){
            return;
        }
        switch (requestCode){
            case 100:{
                Comments comments= (Comments) data.getSerializableExtra("newComment");
                commentsList.add(comments);
                adapter.notifyDataSetChanged();
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onComment(int position) {
        et.setText("");
    }
}
