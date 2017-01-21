package zjut.salu.share.activity.destination;

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
import zjut.salu.share.adapter.destination.ProvincesRecycleAdapter;
import zjut.salu.share.adapter.recommend_route.RecommendRouteRecycleAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.model.Provinces;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;

public class ProvinceActivity extends RxBaseActivity {
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.recycle_view_common)RecyclerView recyclerView;
    @Bind(R.id.circle_progress_common)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed)ImageView loadingFailedIV;
    @Bind(R.id.iv_empty)ImageView emptyIV;
    @Bind(R.id.swipe_view_common)SwipeRefreshLayout refreshLayout;

    private String destinationName="";
    private ProvincesRecycleAdapter adapter;
    private WeakReference<Activity> mReference;
    private OkHttpUtils okHttpUtils;
    private List<Provinces> provincesList;
    @Override
    public int getLayoutId() {
        return R.layout.common_recycle_view;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent=getIntent();
        destinationName=intent.getStringExtra("destinationName");
        okHttpUtils=new OkHttpUtils();
        mReference=new WeakReference<>(this);
        Boolean isNetworkAvailable = CommonUtils.isNetworkAvailable(mReference.get());
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(()->{
            if(refreshLayout.isRefreshing()){
                getProvinceData(destinationName);//刷新时回调
            }
        });
        progressView.spin();
        if(isNetworkAvailable){
            getProvinceData(destinationName);
        }else{
            progressView.stopSpinning();
            progressView.setVisibility(View.INVISIBLE);
            loadingFailedIV.setVisibility(View.VISIBLE);
        }
    }

    private void getProvinceData(String name){
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("destinationName",name);
        params.add(map);
        Observable<String> observable=okHttpUtils.asyncPostRequest(params, RequestURLs.GET_PROVINCES);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        runOnUiThread(()->refreshLayout.setRefreshing(false));
                    }
                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()-> {
                            ToastUtils.ShortToast(R.string.server_down_text);
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
                            provincesList=new ArrayList<>();
                            provincesList=gson.fromJson(result,new TypeToken<List<Provinces>>(){}.getType());
                            recyclerView.setLayoutManager(new LinearLayoutManager(mReference.get()));
                            adapter=new ProvincesRecycleAdapter(recyclerView,provincesList);
                            recyclerView.setAdapter(adapter);
                            adapter.setOnItemClickListener(((position, holder) -> {
                                Provinces province=provincesList.get(position);
                                Intent intent=new Intent(mReference.get(),CityActivity.class);
                                intent.putExtra("province_id",province.getProvinceid());
                                startActivity(intent);
                            }));
                            recyclerView.addOnScrollListener(new MyRecycleViewScrollListener());
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
        toolbar.setTitle(R.string.province_select_text);
    }
}
