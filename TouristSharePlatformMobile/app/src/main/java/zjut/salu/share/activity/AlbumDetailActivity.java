package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

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
import zjut.salu.share.adapter.albums.AlbumDetailRecycleAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.AppBarStateChangeEvent;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.model.UserPhotos;
import zjut.salu.share.params.SpacesItemDecoration;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.SystemBarHelper;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;
import zjut.salu.share.widget.CommonCircleImageView;

public class AlbumDetailActivity extends RxBaseActivity{
    @Bind(R.id.collapsing_toolbar_album_detail)CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.toolbar)Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)AppBarLayout mAppBarLayout;
    @Bind(R.id.line)View mLineView;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_failed_album_detail)ImageView failedIV;
    @Bind(R.id.recycle_view_album_detail)RecyclerView recyclerView;
    @Bind(R.id.user_avatar_view)CommonCircleImageView avatarIV;
    @Bind(R.id.user_pic)ImageView coverIV;
    @Bind(R.id.user_name)TextView usernameTV;
    @Bind(R.id.user_sex)ImageView sexIV;
    @Bind(R.id.user_desc)TextView descTV;
    private String albumid="";
    private String title="";
    private WeakReference<Activity> mReference=null;
    private List<UserPhotos> photosList=null;
    private OkHttpUtils httpUtils=null;
    private AlbumDetailRecycleAdapter adapter=null;
    private ImageLoader imageLoader=null;
    @Override
    public int getLayoutId() {
        return R.layout.activity_album_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        httpUtils=new OkHttpUtils();
        imageLoader=ImageLoader.getInstance();
        Intent intent=getIntent();
        albumid=intent.getStringExtra("albumid");
        title=intent.getStringExtra("title");
        Boolean isNetworkAvailable = CommonUtils.isNetworkAvailable(mReference.get());
        if(isNetworkAvailable){
            getPhotoData();
        }else{
            progressView.stopSpinning();
            progressView.setVisibility(View.GONE);
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }

    /**
     * 获取相册数据
     */
    private void getPhotoData(){
        if(progressView.getVisibility()==View.GONE){progressView.setVisibility(View.VISIBLE);}
        if(failedIV.getVisibility()==View.VISIBLE){failedIV.setVisibility(View.INVISIBLE);}
        if(recyclerView.getVisibility()==View.VISIBLE){recyclerView.setVisibility(View.GONE);}
        progressView.spin();
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("albumid",albumid);
        params.add(map);
        Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.GET_ALBUM_PHOTOS_URL);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i("AlbumDetailActivity","访问完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressView.stopSpinning();
                        progressView.setVisibility(View.GONE);
                        failedIV.setVisibility(View.VISIBLE);
                        ToastUtils.ShortToast(R.string.server_down_text);
                    }

                    @Override
                    public void onNext(String result) {
                        Gson gson=new Gson();
                        photosList=gson.fromJson(result,new TypeToken<List<UserPhotos>>(){}.getType());
                        UserPhotos p=photosList.get(0);
                        TripUser u=p.getAlbums().getTripUser();
                        Glide.with(mReference.get())//头像加载
                                .load(RequestURLs.MAIN_URL+p.getPhotourl())
                                .centerCrop()
                                .dontAnimate()
                                .placeholder(R.drawable.test_cover)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(coverIV);
                        Glide.with(mReference.get())//头像加载
                                .load(RequestURLs.MAIN_URL+u.getHeaderimage())
                                .centerCrop()
                                .dontAnimate()
                                .placeholder(R.drawable.ico_user_default)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(avatarIV);
                        usernameTV.setText(u.getUsername());
                        if(u.getSex().equals("男")){
                            sexIV.setImageResource(R.drawable.ic_user_male);
                        }else if(u.getSex().equals("女")){
                            sexIV.setImageResource(R.drawable.ic_user_female);
                        }else{
                            sexIV.setImageResource(R.drawable.ic_user_gay_border);
                        }
                        descTV.setText(p.getAlbums().getAlbumname());
                        if(recyclerView.getVisibility()==View.GONE){recyclerView.setVisibility(View.VISIBLE);}
                        failedIV.setVisibility(View.INVISIBLE);
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                        adapter=new AlbumDetailRecycleAdapter(recyclerView,photosList,imageLoader);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener((position,holder)->{
                            //TODO:点击某一张图片大图查看
                        });
                        //设置item之间的间隔
                        SpacesItemDecoration decoration=new SpacesItemDecoration(16);
                        recyclerView.addItemDecoration(decoration);
                        progressView.stopSpinning();
                    }
                });
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        //设置StatusBar透明
        SystemBarHelper.immersiveStatusBar(this);
        SystemBarHelper.setHeightAndPadding(this, mToolbar);

        //设置AppBar展开折叠状态监听
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeEvent()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset)
            {

                if (state == State.EXPANDED)
                {
                    //展开状态
                    mCollapsingToolbarLayout.setTitle("");
                    mLineView.setVisibility(View.VISIBLE);
                } else if (state == State.COLLAPSED)
                {
                    //折叠状态
                    mCollapsingToolbarLayout.setTitle(title);
                    mLineView.setVisibility(View.GONE);
                } else
                {
                    mCollapsingToolbarLayout.setTitle("");
                    mLineView.setVisibility(View.VISIBLE);
                }
            }
        });
        mToolbar.setNavigationOnClickListener(v-> finish());
    }
}
