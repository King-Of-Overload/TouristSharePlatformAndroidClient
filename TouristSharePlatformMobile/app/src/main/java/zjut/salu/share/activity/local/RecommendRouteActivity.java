package zjut.salu.share.activity.local;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

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
import zjut.salu.share.adapter.recommend_route.RecommendRouteRecycleAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.model.Routes;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;

public class RecommendRouteActivity extends RxBaseActivity {
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.recycle_view_recommend_route)RecyclerView recyclerView;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed)ImageView loadingFailedIV;
    @Bind(R.id.iv_empty_recommend)ImageView emptyIV;
    @Bind(R.id.swipe_recommend_route)SwipeRefreshLayout refreshLayout;

    private ImageLoader imageLoader;
    private RecommendRouteRecycleAdapter adapter;
    private List<Routes> routesList;
    private WeakReference<Activity> mReference;
    private OkHttpUtils okHttpUtils;
    private int cityid;
    @Override
    public int getLayoutId() {
        return R.layout.activity_recommend_route;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        okHttpUtils=new OkHttpUtils();
        mReference=new WeakReference<>(this);
        imageLoader=ImageLoader.getInstance();
        Intent intent=getIntent();
        cityid=intent.getIntExtra("cityid",0);
        Boolean isNetworkAvailable = CommonUtils.isNetworkAvailable(mReference.get());
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(()->{
            if(refreshLayout.isRefreshing()){
                getRoutesData();//刷新时回调
            }
        });
        progressView.spin();
        if(isNetworkAvailable){
            getRoutesData();
        }else{
            progressView.stopSpinning();
            progressView.setVisibility(View.INVISIBLE);
            loadingFailedIV.setVisibility(View.VISIBLE);
        }
    }

    private void getRoutesData(){
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("selected_cityid",cityid+"");
        params.add(map);
        Observable<String> observable=okHttpUtils.asyncPostRequest(params, RequestURLs.GET_ALL_ROUTES_URL);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                     runOnUiThread(()->refreshLayout.setRefreshing(false));
                    }
                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()-> {ToastUtils.ShortToast(R.string.server_down_text);
                            progressView.stopSpinning();
                            progressView.setVisibility(View.INVISIBLE);
                            loadingFailedIV.setVisibility(View.VISIBLE);
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        progressView.stopSpinning();
                        progressView.setVisibility(View.INVISIBLE);
                        loadingFailedIV.setVisibility(View.INVISIBLE);
                        if(!result.equals("")){
                            emptyIV.setVisibility(View.INVISIBLE);
                            Gson gson=new Gson();
                            routesList=new ArrayList<>();
                            routesList=gson.fromJson(result,new TypeToken<List<Routes>>(){}.getType());
                            recyclerView.setLayoutManager(new LinearLayoutManager(mReference.get()));
                            adapter=new RecommendRouteRecycleAdapter(recyclerView,routesList,imageLoader);
                            recyclerView.setAdapter(adapter);
                            adapter.setOnItemClickListener(((position, holder) -> {
                                Routes route=routesList.get(position);
                                Intent intent=new Intent(mReference.get(),RecommendRouteDetailActivity.class);
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("route",route);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }));
                            recyclerView.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
                        }else{
                            emptyIV.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
        toolbar.setTitle(R.string.recommend_route_text);
    }
}
