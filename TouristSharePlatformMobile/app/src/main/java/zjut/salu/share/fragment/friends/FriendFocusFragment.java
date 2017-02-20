package zjut.salu.share.fragment.friends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mingle.widget.LoadingView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.FriendsActivity;
import zjut.salu.share.activity.UserInfoActivity;
import zjut.salu.share.activity.user.ChatActivity;
import zjut.salu.share.adapter.friends.FriendsRecycleAdapter;
import zjut.salu.share.adapter.helper.AbsRecyclerViewAdapter;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.fragment.RxLazyFragment;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;

/**关注的人当中我的关注
 * Created by Salu on 2016/12/8.
 */

public class FriendFocusFragment extends RxLazyFragment {
    @Bind(R.id.swipe_friends_focus)WaveSwipeRefreshLayout refreshLayout;
    @Bind(R.id.recycle_view_friend_focus)RecyclerView recyclerView;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed_friend_focus)ImageView loadingFailedIV;
    @Bind(R.id.iv_empty_friend_focus)ImageView emptyIV;
    private Context context;
    private OkHttpUtils httpUtils=null;
    private List<TripUser> userList=null;
    private FriendsRecycleAdapter adapter=null;
    private ImageLoader imageLoader;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_friend_focus;
    }

    @Override
    public void finishCreateView(Bundle state) {
        imageLoader=ImageLoader.getInstance();
        context=getActivity();
        progressView.setVisibility(View.VISIBLE);
        progressView.spin();
        refreshLayout.setOnRefreshListener(() ->{
                refreshLayout.setVisibility(View.VISIBLE);
                Observable.timer(2, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {}
            @Override
            public void onError(Throwable e) {}
            @Override
            public void onNext(Long aLong) {
                loadData();
            }
        });});
        httpUtils=new OkHttpUtils();
        loadData();
    }

    @Override
    protected void loadData() {
        super.loadData();
        progressView.setVisibility(View.VISIBLE);
        progressView.spin();
        loadingFailedIV.setVisibility(View.INVISIBLE);
        emptyIV.setVisibility(View.INVISIBLE);
        Boolean isNetworkAvailable= CommonUtils.isNetworkAvailable(context);
        if(isNetworkAvailable){
            List<Map<String,Object>> params=new ArrayList<>();
            Map<String,Object> map=new HashMap<>();
            map.put("userid", PreferenceUtils.getString("userid",null));
            map.put("type","focus");
            params.add(map);
            Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.GET_USER_FRIENDS);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                            ((Activity)context).runOnUiThread(()->{
                                loadingFailedIV.setVisibility(View.VISIBLE);
                                progressView.stopSpinning();
                                progressView.setVisibility(View.INVISIBLE);
                                ToastUtils.ShortToast(R.string.server_down_text);
                            });
                        }

                        @Override
                        public void onNext(String result) {
                            Gson gson=new Gson();
                            userList=new ArrayList<>();
                            userList=gson.fromJson(result,new TypeToken<List<TripUser>>(){}.getType());
                            if(null==userList||userList.size()==0){
                                emptyIV.setVisibility(View.VISIBLE);
                            }else{
                                WeakReference<RecyclerView> recyclerReference=new WeakReference<>(recyclerView);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                adapter=new FriendsRecycleAdapter(recyclerReference.get(),userList,imageLoader);
                                recyclerView.setAdapter(adapter);
                                adapter.setOnItemClickListener((position, holder) ->{
                                    Intent intent=new Intent(context, UserInfoActivity.class);
                                    intent.putExtra("isCurrentUser",false);//非当前用户
                                    TripUser clickedUser=userList.get(position);
                                    intent.putExtra("userid",clickedUser.getUserid());
                                    startActivity(intent);
                                });
                                adapter.setOnItemLongClickListener((position, holder) -> {
                                    TripUser user=userList.get(position);
                                    Intent intent = new Intent(context, ChatActivity.class);
                                    intent.putExtra("username", user.getUserid());
                                    intent.putExtra("nickname",user.getUsername());
                                    startActivity(intent);
                                    return true;
                            });
                                recyclerView.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
                            }
                            refreshLayout.setRefreshing(false);
                            progressView.stopSpinning();
                            progressView.setVisibility(View.INVISIBLE);
                        }
                    });
        }else{
            loadingFailedIV.setVisibility(View.VISIBLE);
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }
}
