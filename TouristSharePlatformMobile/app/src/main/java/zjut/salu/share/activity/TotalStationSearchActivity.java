package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.banggumi.BanggumiDetailActivity;
import zjut.salu.share.adapter.index.IndexAlbumRecycleAdapter;
import zjut.salu.share.adapter.index.IndexBanggumeRecycleAdapter;
import zjut.salu.share.adapter.index.IndexProductRecycleAdapter;
import zjut.salu.share.adapter.index.IndexSkillAcademyRecycleAdapter;
import zjut.salu.share.adapter.index.IndexStrategyRecycleAdapter;
import zjut.salu.share.adapter.index.SearchUserAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.model.photo.UserAlbums;
import zjut.salu.share.model.product.Product;
import zjut.salu.share.model.skillacademy.SkillAcademy;
import zjut.salu.share.utils.KeyBoardUtil;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StatusBarUtil;
import zjut.salu.share.widget.layout.FullyGridLayoutManager;

public class TotalStationSearchActivity extends RxBaseActivity {
    @Bind(R.id.iv_search_loading) ImageView mLoadingView;
    @Bind(R.id.search_layout)LinearLayout mSearchLayout;
    @Bind(R.id.search_edit) EditText mSearchEdit;
    @Bind(R.id.search_img) ImageView mSearchBtn;
    @Bind(R.id.search_text_clear) ImageView mSearchTextClear;
    private AnimationDrawable mAnimationDrawable;
    private OkHttpUtils okHttpUtils;
    private ImageLoader imageLoader;
    private WeakReference<Activity> reference;

    private String content;

    @Bind(R.id.recycle_strategy)RecyclerView strategyRecycle;
    @Bind(R.id.recycle_album)RecyclerView albumRecycle;
    @Bind(R.id.recycle_skill_academy)RecyclerView academyRecycle;
    @Bind(R.id.recycle_product)RecyclerView productRecycle;
    @Bind(R.id.recycle_banggume)RecyclerView banggumeRecycle;
    @Bind(R.id.index_grid_view)GridView gridView;

    private List<UserStrategy> strategyList;
    private List<UserAlbums> albumsList;
    private List<SkillAcademy> academyList;
    private List<Product> productList;
    private List<Banggume> banggumeList;
    private List<TripUser> userList;

    private IndexStrategyRecycleAdapter strategyRecycleAdapter;
    private IndexAlbumRecycleAdapter albumRecycleAdapter;
    private IndexSkillAcademyRecycleAdapter academyRecycleAdapter;
    private IndexProductRecycleAdapter productRecycleAdapter;
    private IndexBanggumeRecycleAdapter banggumeRecycleAdapter;
    private SearchUserAdapter userAdapter;
    @Override
    public int getLayoutId()
    {

        return R.layout.activity_search;
    }

    @Override
    public void initToolBar()
    {
        //设置6.0以上StatusBar字体颜色
        StatusBarUtil.from(this)
                .setLightStatusBar(true)
                .process();
    }
    @Override
    public void initViews(Bundle savedInstanceState)
    {
        Intent intent=getIntent();
        content=intent.getStringExtra("key");
        okHttpUtils=new OkHttpUtils();
        imageLoader=ImageLoader.getInstance();
        reference=new WeakReference<>(this);
        mLoadingView.setImageResource(R.drawable.anim_search_loading);
        mAnimationDrawable = (AnimationDrawable) mLoadingView.getDrawable();
        showSearchAnim();
        mSearchEdit.clearFocus();
        mSearchEdit.setText(content);
        getSearchData();
        search();
        setUpEditText();
    }

    public static void launch(Activity activity, String str)
    {

        Intent mIntent = new Intent(activity, TotalStationSearchActivity.class);
        mIntent.putExtra("key", str);
        activity.startActivity(mIntent);
    }

    private void setUpEditText()
    {

        RxTextView.textChanges(mSearchEdit)
                .compose(this.bindToLifecycle())
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    if (!TextUtils.isEmpty(s))
                        mSearchTextClear.setVisibility(View.VISIBLE);
                    else
                        mSearchTextClear.setVisibility(View.GONE);
                });


        RxView.clicks(mSearchTextClear)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {

                    mSearchEdit.setText("");
                });


        RxTextView.editorActions(mSearchEdit)
                .filter(integer -> !TextUtils.isEmpty(mSearchEdit.getText().toString().trim()))
                .filter(integer -> integer == EditorInfo.IME_ACTION_SEARCH)
                .flatMap(new Func1<Integer,Observable<String>>()
                {

                    @Override
                    public Observable<String> call(Integer integer)
                    {

                        return Observable.just(mSearchEdit.getText().toString().trim());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    KeyBoardUtil.closeKeybord(mSearchEdit,
                            TotalStationSearchActivity.this);
                    showSearchAnim();
                    //content = s;
//                    getSearchData();
                });
    }
    private void search()
    {

        RxView.clicks(mSearchBtn)
                .throttleFirst(2, TimeUnit.SECONDS)
                .map(aVoid -> mSearchEdit.getText().toString().trim())
                .filter(s -> !TextUtils.isEmpty(s))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    KeyBoardUtil.closeKeybord(mSearchEdit,
                            TotalStationSearchActivity.this);
                    showSearchAnim();
                    content = s;
                    getSearchData();
                });
    }

    private void getSearchData(){
        Map<String,Object> params=new HashMap<>();
        params.put("key",content);
        okHttpUtils.asyncGetRequest(RequestURLs.SEARCH_VALUE,params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()->hideSearchAnim());
                    }

                    @Override
                    public void onNext(String result) {
                        try {
                            hideSearchAnim();
                            JSONObject object=new JSONObject(result);
                            Gson gson=new Gson();
                            userList=gson.fromJson(object.getString("users"),new TypeToken<List<TripUser>>(){}.getType());
                            strategyList=gson.fromJson(object.getString("strategy"),new TypeToken<List<UserStrategy>>(){}.getType());
                            albumsList=gson.fromJson(object.getString("albums"),new TypeToken<List<UserAlbums>>(){}.getType());
                            academyList=gson.fromJson(object.getString("academies"),new TypeToken<List<SkillAcademy>>(){}.getType());
                            productList=gson.fromJson(object.getString("products"),new TypeToken<List<Product>>(){}.getType());
                            banggumeList=gson.fromJson(object.getString("banggumes"),new TypeToken<List<Banggume>>(){}.getType());
                            userAdapter=new SearchUserAdapter(imageLoader,userList,reference.get());
                            gridView.setAdapter(userAdapter);
                            gridView.setOnItemClickListener((parent, view, position, id) -> {
                                if(PreferenceUtils.getBoolean("loginStatus",false)){
                                    TripUser user=userList.get(position);
                                    Intent intent=new Intent(reference.get(),PersonalInfoActivity.class);
                                    if(PreferenceUtils.acquireCurrentUser().getUserid().equals(user.getUserid())){
                                        intent.putExtra("isCurrentUser",true);
                                    }else{
                                        intent.putExtra("isCurrentUser",false);
                                        intent.putExtra("user",user);
                                    }
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Intent mIntent=new Intent(reference.get(),LoginActivity.class);
                                    mIntent.putExtra("activity_name", reference.get().getClass().getName());
                                    startActivity(mIntent);
                                }
                            });
                            gridView.setOnScrollListener(new PauseOnScrollListener(imageLoader,true,true));
                            FullyGridLayoutManager gridLayoutManager=new FullyGridLayoutManager(reference.get(),2);
                            strategyRecycle.setHasFixedSize(true);
                            strategyRecycle.setNestedScrollingEnabled(true);
                            strategyRecycle.setLayoutManager(gridLayoutManager);
                            strategyRecycleAdapter=new IndexStrategyRecycleAdapter(strategyRecycle,strategyList,imageLoader);
                            strategyRecycle.setAdapter(strategyRecycleAdapter);
                            strategyRecycle.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
                            strategyRecycleAdapter.setOnItemClickListener((position, holder) -> {
                                Intent intent=new Intent(reference.get(),UserStrategyDetailActivity.class);
                                intent.putExtra("user_strategy",strategyList.get(position));
                                startActivity(intent);
                            });
                            FullyGridLayoutManager gridLayoutManager2=new FullyGridLayoutManager(reference.get(),2);
                            albumRecycle.setHasFixedSize(true);
                            albumRecycle.setNestedScrollingEnabled(true);
                            albumRecycle.setLayoutManager(gridLayoutManager2);
                            albumRecycleAdapter=new IndexAlbumRecycleAdapter(albumRecycle,albumsList,imageLoader);
                            albumRecycle.setAdapter(albumRecycleAdapter);
                            albumRecycle.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
                            albumRecycleAdapter.setOnItemClickListener((position, holder) -> {
                                Intent intent=new Intent(reference.get(),AlbumDetailActivity.class);
                                UserAlbums albums=albumsList.get(position);
                                intent.putExtra("albumid",albums.getAlbumid());
                                intent.putExtra("title",albums.getAlbumname());
                                startActivity(intent);
                            });
                            FullyGridLayoutManager gridLayoutManager3=new FullyGridLayoutManager(reference.get(),2);
                            academyRecycle.setHasFixedSize(true);
                            academyRecycle.setNestedScrollingEnabled(true);
                            academyRecycle.setLayoutManager(gridLayoutManager3);
                            academyRecycleAdapter=new IndexSkillAcademyRecycleAdapter(academyRecycle,academyList,imageLoader);
                            academyRecycle.setAdapter(academyRecycleAdapter);
                            academyRecycle.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
                            academyRecycleAdapter.setOnItemClickListener((position, holder) -> {
                                Intent intent=new Intent(reference.get(),SkillAcademy.class);
                                intent.putExtra("skill_academy",academyList.get(position));
                                startActivity(intent);
                            });
                            FullyGridLayoutManager gridLayoutManager4=new FullyGridLayoutManager(reference.get(),2);
                            productRecycle.setHasFixedSize(true);
                            productRecycle.setNestedScrollingEnabled(true);
                            productRecycle.setLayoutManager(gridLayoutManager4);
                            productRecycleAdapter=new IndexProductRecycleAdapter(productRecycle,productList,imageLoader);
                            productRecycle.setAdapter(productRecycleAdapter);
                            productRecycle.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
                            productRecycleAdapter.setOnItemClickListener((position, holder) -> {
                                Intent intent=new Intent(reference.get(), ProductDetailActivity.class);
                                intent.putExtra("product",productList.get(position));
                                startActivity(intent);
                            });
                            FullyGridLayoutManager gridLayoutManager5=new FullyGridLayoutManager(reference.get(),2);
                            banggumeRecycle.setHasFixedSize(true);
                            banggumeRecycle.setNestedScrollingEnabled(true);
                            banggumeRecycle.setLayoutManager(gridLayoutManager5);
                            banggumeRecycleAdapter=new IndexBanggumeRecycleAdapter(banggumeRecycle,banggumeList,imageLoader);
                            banggumeRecycle.setAdapter(banggumeRecycleAdapter);
                            banggumeRecycle.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
                            banggumeRecycleAdapter.setOnItemClickListener((position, holder) -> {
                                BanggumiDetailActivity.launch((Activity) reference.get(),banggumeList.get(position));
                            });
                            setRecycleNoScroll();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setRecycleNoScroll()
    {
        strategyRecycle.setOnTouchListener((v, event) -> false);
        albumRecycle.setOnTouchListener((v, event) -> false);
        academyRecycle.setOnTouchListener((v, event) -> false);
        productRecycle.setOnTouchListener((v, event) -> false);
        banggumeRecycle.setOnTouchListener((v, event) -> false);
    }


    private void showSearchAnim()
    {

        mLoadingView.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        mAnimationDrawable.start();
    }

    private void hideSearchAnim()
    {

        mLoadingView.setVisibility(View.GONE);
        mSearchLayout.setVisibility(View.VISIBLE);
        mAnimationDrawable.stop();
    }
}
