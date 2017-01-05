package zjut.salu.share.fragment.best;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.UserStrategyDetailActivity;
import zjut.salu.share.adapter.best.BestStrategyRecycleAdapter;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.fragment.RxLazyFragment;
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;

/**精选内容的子fragment
 * Created by Salu on 2016/12/13.
 */

public class BestStrategyChildFragment extends RxLazyFragment{
    @Bind(R.id.swipe_view_best_strategy)SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recycle_view_best_strategy)RecyclerView mRecyclerView;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed)ImageView loadFailedIV;
    private Context context;
    private List<UserStrategy> bestStrategyList=null;
    private OkHttpUtils httpUtils;
    private BestStrategyRecycleAdapter adapter=null;
    private ImageLoader imageLoader=null;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_child_best_strategy;
    }

    @Override
    public void finishCreateView(Bundle state) {
        httpUtils=new OkHttpUtils();
        imageLoader=ImageLoader.getInstance();
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(()->{
            if(refreshLayout.isRefreshing()){
                getBestStrategyData();//刷新时回调
            }
        });
        context=getActivity();
        Boolean isNetworkAvailable = CommonUtils.isNetworkAvailable(context);
        if(isNetworkAvailable){
            getBestStrategyData();
        }else{
            ToastUtils.ShortToast(R.string.no_network_connection);
            loadFailedIV.setVisibility(View.VISIBLE);
            if(progressView.isSpinning()){progressView.stopSpinning();}
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取精选游记信息
     */
    private void getBestStrategyData() {
        progressView.setVisibility(View.VISIBLE);
        progressView.spin();
        Map<String,Object> map=new HashMap<>();
        map.put("requestType","bestStrategy");
        Observable<String> observable=httpUtils.asyncGetRequest(RequestURLs.GET_ALL_BEST_CHOOSE_URL,map);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {((Activity)context).runOnUiThread(()->{
                        if(progressView.isSpinning()){progressView.stopSpinning();}
                        progressView.setVisibility(View.INVISIBLE);
                        loadFailedIV.setVisibility(View.INVISIBLE);
                        refreshLayout.setRefreshing(false);
                    });}

                    @Override
                    public void onError(Throwable e) {
                        ((Activity)context).runOnUiThread(()->{
                            if(progressView.isSpinning()){progressView.stopSpinning();}
                            progressView.setVisibility(View.INVISIBLE);
                            loadFailedIV.setVisibility(View.VISIBLE);
                            refreshLayout.setRefreshing(false);
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        bestStrategyList=gson.fromJson(result,new TypeToken<List<UserStrategy>>(){}.getType());
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        adapter=new BestStrategyRecycleAdapter(mRecyclerView,bestStrategyList,imageLoader);
                        mRecyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(((position, holder) ->{
                            Intent intent=new Intent(context, UserStrategyDetailActivity.class);
                            intent.putExtra("user_strategy",bestStrategyList.get(position));
                            startActivity(intent);
                        }));
                        mRecyclerView.addOnScrollListener(new MyRecycleViewScrollListener());
                    }
                });
    }
}
