package zjut.salu.share.activity.banggumi;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.adapter.lightstrategy.BanggumeListRecycleAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.model.lightstrategy.banggume.BanggumeTag;
import zjut.salu.share.utils.LogUtil;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;

/**
 * 小视频列表显示控制层
 */
public class BanggumeActivity extends RxBaseActivity {
    public static final String BANGGUME_HOT = "banggume_hot";
    public static final String BANGGUME_NEW = "banggume_new";
    public static final String BANGGUME_COLLECT = "banggume_collect";
    public static final String BANGGUME_COMMENT = "banggume_comment";
    public static final String BANGGUME_ISSENSE = "banggume_issense";
    public static final String BANGGUME_ALL = "banggume_all";


    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.search_view)MaterialSearchView mSearchView;
    @Bind(R.id.tags_layout)TagFlowLayout flowLayout;
    @Bind(R.id.swipe_view)SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recycle)RecyclerView recyclerView;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed_friend_focus)ImageView loadingFailedIV;
    @Bind(R.id.iv_empty_friend_focus)ImageView emptyIV;


    private List<BanggumeTag> tags;//网络获取的tags
    private WeakReference<Activity> mReference;
    private BanggumeListRecycleAdapter adapter;
    private ImageLoader imageLoader;
    private WeakReference<RecyclerView> recyclerReference;
    private BanggumeTag banggumeTag=null;//选中的标签
    private String currentOption="";//当前的排序条件
    private String searchValue="";//搜索关键字
    private List<Banggume> banggumeList;

    private OkHttpUtils okHttpUtils;
    private Gson gson;
    @Override
    public int getLayoutId() {
        return R.layout.activity_banggume;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initSearchView();
        mReference=new WeakReference<>(this);
        imageLoader=ImageLoader.getInstance();
        recyclerReference=new WeakReference<>(recyclerView);
        okHttpUtils=new OkHttpUtils();
        gson=new Gson();
        progressView.spin();
        initTagFlow();
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(()->{
            if(refreshLayout.isRefreshing()){
                initTagFlow();
                loadListData();
            }
        });
        loadListData();
    }

    /**
     * 加载列表数据
     */
    private void loadListData(){
        Map<String,Object> params=new HashMap<>();
        if(null!=banggumeTag&&!banggumeTag.getBanggumetagname().equals("")){
            params.put("banggumeTag",gson.toJson(banggumeTag));
        }
        if(null!=currentOption&&!currentOption.equals("")){
            params.put("currentOption",currentOption);
        }
        if(null!=searchValue&&!searchValue.equals("")){
            params.put("searchValue",searchValue);
        }
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.GET_BANGGUME_URL,params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        runOnUiThread(()->{
                            progressView.stopSpinning();
                            loadingFailedIV.setVisibility(View.INVISIBLE);
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(()->{
                            progressView.stopSpinning();
                            ToastUtils.ShortToast(R.string.server_down_text);
                            loadingFailedIV.setVisibility(View.VISIBLE);
                            emptyIV.setVisibility(View.INVISIBLE);
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        banggumeList=gson.fromJson(result,new TypeToken<List<Banggume>>(){}.getType());
                        adapter=new BanggumeListRecycleAdapter(recyclerReference.get(),banggumeList,imageLoader);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mReference.get()));
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener((position, holder) -> {
                            //TODO:跳转到banggume详情界面
                        });
                        recyclerView.addOnScrollListener(new MyRecycleViewScrollListener());
                    }
                });
    }

    /**
     * 初始化tagflow区域数据
     */
    private void initTagFlow(){
        Map<String,Object> params=new HashMap<>();
        params.put("num",8+"");
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.GET_BANGGUME_TAGS,params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()-> ToastUtils.ShortToast(R.string.server_down_text));
                    }

                    @Override
                    public void onNext(String result) {
                        banggumeTag=new BanggumeTag();
                        Gson gson=new Gson();
                        tags=gson.fromJson(result,new TypeToken<List<BanggumeTag>>(){}.getType());
                        setFlowAdapter();
                        flowLayout.setOnSelectListener(selectPosSet -> {
                            Iterator<Integer> iterator=selectPosSet.iterator();
                            while(iterator.hasNext()){
                                banggumeTag=tags.get(iterator.next());
                            }
                            LogUtil.d(banggumeTag.getBanggumetagname());
                        });
                    }
                });
    }

    private void setFlowAdapter(){
        flowLayout.setAdapter(new TagAdapter<BanggumeTag>(tags){
            @Override
            public View getView(FlowLayout parent, int position, BanggumeTag banggumeTag) {
                TextView mTags= (TextView) LayoutInflater.from(mReference.get())
                        .inflate(R.layout.layout_tags_item,parent,false);
                mTags.setText(banggumeTag.getBanggumetagname());
                return mTags;
            }
        });
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.trip_banggume_text);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_banggume,menu);
        MenuItem item=menu.findItem(R.id.id_action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_banggume_all:{//全部
                currentOption=BANGGUME_ALL;
                break;
            }
            case R.id.menu_banggume_hot:{//热度排序
                currentOption=BANGGUME_HOT;
                break;
            }
            case R.id.menu_banggume_new:{//最新投稿
                currentOption=BANGGUME_NEW;
                break;
            }
            case R.id.menu_banggume_collect:{//最多收藏
                currentOption=BANGGUME_COLLECT;
                break;
            }
            case R.id.menu_banggume_comment:{//最多评论
                currentOption=BANGGUME_COMMENT;
                break;
            }
            case R.id.menu_banggume_issense:{//精选内容
                currentOption=BANGGUME_ISSENSE;
                break;
            }
        }
        searchValue="";
        loadListData();
        return super.onOptionsItemSelected(item);
    }


    /**
     * 初始化搜索区
     */
    private void initSearchView(){
        //初始化SearchBar
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.custom_cursor);
        mSearchView.setEllipsize(true);
        // mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener()
        {

            @Override
            public boolean onQueryTextSubmit(String query)
            {

                //TotalStationSearchActivity.launch(HomeActivity.this, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {

                return false;
            }
        });
    }
}
