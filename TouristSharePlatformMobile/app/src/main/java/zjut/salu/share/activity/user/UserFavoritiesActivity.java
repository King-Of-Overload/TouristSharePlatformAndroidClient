package zjut.salu.share.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.banggumi.BanggumiDetailActivity;
import zjut.salu.share.adapter.favorite.UserFavoriteAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.model.user.UserFavorite;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.widget.view.CustomEmptyView;

/**
 * 我的收藏界面
 */
public class UserFavoritiesActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.recycle)RecyclerView recyclerView;
    @Bind(R.id.empty_view)CustomEmptyView emptyView;

    private WeakReference<Activity> reference;
    private UserFavoriteAdapter adapter;
    private OkHttpUtils okHttpUtils;
    private List<UserFavorite> favorites;
    private ImageLoader imageLoader;

    public static void launch(Activity activity){
        Intent intent=new Intent(activity,UserFavoritiesActivity.class);
        activity.startActivity(intent);
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_user_favorities;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        reference=new WeakReference<>(this);
        okHttpUtils=new OkHttpUtils();
        imageLoader=ImageLoader.getInstance();
        finishTask();
    }

    @Override
    public void finishTask() {
        Map<String,Object> params=new HashMap<>();
        params.put("userid", PreferenceUtils.getString("userid",null));
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.GET_USER_FAVORITIES,params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()->{
                            emptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
                            emptyView.setEmptyText(getString(R.string.load_error_text));
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        favorites=gson.fromJson(result,new TypeToken<List<UserFavorite>>(){}.getType());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(reference.get()));
                        adapter=new UserFavoriteAdapter(recyclerView,favorites,imageLoader);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener((position, holder) -> {
                            UserFavorite favorite=favorites.get(position);
                            switch (favorite.getType()){
                                case "banggume":{
                                    getBanggume(favorite.getEntityid());
                                    break;
                                }
                            }
                        });
                        recyclerView.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
                        if(favorites.isEmpty()){
                            emptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
                            emptyView.setEmptyText(getString(R.string.no_content_text));
                        }
                    }
                });
    }

    private void getBanggume(String banggumeid){
        Map<String,Object> params=new HashMap<>();
        params.put("banggumeid", banggumeid);
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.GET_SINGLE_BANGGUME,params);
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
                        Banggume banggume=gson.fromJson(result,Banggume.class);
                        BanggumiDetailActivity.launch(reference.get(),banggume);
                    }
                });
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.my_favorities_text);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
