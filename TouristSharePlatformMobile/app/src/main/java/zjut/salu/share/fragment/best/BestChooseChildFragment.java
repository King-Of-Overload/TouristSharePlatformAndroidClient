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

import com.andview.refreshview.XRefreshView;
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
import zjut.salu.share.activity.BestTopicActivity;
import zjut.salu.share.adapter.best.BestChooseRecycleAdapter;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.fragment.RxLazyFragment;
import zjut.salu.share.model.BestChoose;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;

/**精选的内容子fragment
 * Created by Salu on 2016/12/13.
 */

public class BestChooseChildFragment extends RxLazyFragment {
    @Bind(R.id.swipe_view_best_choose)SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recycle_view_best_choose)RecyclerView mRecyclerView;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed)ImageView loadingFailedIV;//加载失败
    private Context context;
    private List<BestChoose> bestChooseList=null;
    private OkHttpUtils httpUtils;
    private BestChooseRecycleAdapter adapter=null;
    private ImageLoader imageLoader=null;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_child_best_choose;
    }

    @Override
    public void finishCreateView(Bundle state) {
        httpUtils=new OkHttpUtils();
        imageLoader=ImageLoader.getInstance();
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(()->{
            if(refreshLayout.isRefreshing()){
                getBestContentData();//刷新时回调
            }
        });
        context=getActivity();
        Boolean isNetworkAvailable = CommonUtils.isNetworkAvailable(context);
        if(isNetworkAvailable){
            getBestContentData();
        }else{
            ToastUtils.ShortToast(R.string.no_network_connection);
            loadingFailedIV.setVisibility(View.VISIBLE);
            if(progressView.isSpinning()){progressView.stopSpinning();}
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取精选数据集合
     */
    private void getBestContentData() {
        progressView.setVisibility(View.VISIBLE);
        progressView.spin();
        Map<String,Object> map=new HashMap<>();
        map.put("requestType","bestChoose");
         Observable<String> observable=httpUtils.asyncGetRequest(RequestURLs.GET_ALL_BEST_CHOOSE_URL,map);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {((Activity)context).runOnUiThread(()->{
                        if(progressView.isSpinning()){progressView.stopSpinning();}
                        progressView.setVisibility(View.INVISIBLE);
                        loadingFailedIV.setVisibility(View.INVISIBLE);
                        refreshLayout.setRefreshing(false);
                    });}

                    @Override
                    public void onError(Throwable e) {
                        ((Activity)context).runOnUiThread(()->{
                            if(progressView.isSpinning()){progressView.stopSpinning();}
                            progressView.setVisibility(View.INVISIBLE);
                            loadingFailedIV.setVisibility(View.VISIBLE);
                            refreshLayout.setRefreshing(false);
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        bestChooseList=gson.fromJson(result,new TypeToken<List<BestChoose>>(){}.getType());
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        adapter=new BestChooseRecycleAdapter(mRecyclerView,bestChooseList,imageLoader);
                        mRecyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(((position, holder) ->{
                            Intent intent=new Intent(context, BestTopicActivity.class);
                            intent.putExtra("best_topic",bestChooseList.get(position));
                            startActivity(intent);//跳转到专题详情页面
                        }));
                        mRecyclerView.addOnScrollListener(new MyRecycleViewScrollListener(null,null));//优化性能，添加滑动监听器
                    }
                });

    }


}
