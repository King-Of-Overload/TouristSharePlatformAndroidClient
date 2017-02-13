package zjut.salu.share.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.andview.refreshview.XRefreshView;
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mingle.widget.LoadingView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import mehdi.sakout.dynamicbox.DynamicBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.adapter.albums.AlbumListAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.photo.UserAlbums;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;

/**
 * 相册界面
 */
public class AlbumActivity extends RxBaseActivity{
    @Bind(R.id.toolbar_album)Toolbar mToolbar;
    @Bind(R.id.xref_album)XRefreshView refreshView;
    @Bind(R.id.sgv_grid_view)StaggeredGridView staggeredGridView;
    @Bind(R.id.iv_error_content)ImageView errorIV;//错误界面
    @Bind(R.id.loading_view_album)LoadingView loadingView;//加载界面
    private Boolean isNetworkAvailable=false;
    private OkHttpUtils httpUtils=null;
    private WeakReference<Activity> mReference=null;
    private List<UserAlbums> albumsList=null;
    private AlbumListAdapter adapter=null;
    private ImageLoader imageLoader=null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_album;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        imageLoader=ImageLoader.getInstance();
        mReference=new WeakReference<>(this);
        httpUtils=new OkHttpUtils();
        PullRefreshUtils.bindPullRefreshView(refreshView,true,false,new AlbumListRefreshViewListener());
        isNetworkAvailable= CommonUtils.isNetworkAvailable(mReference.get());
        if(isNetworkAvailable){
            getAlbumData();
        }else{
            errorIV.setVisibility(View.VISIBLE);
            staggeredGridView.setVisibility(View.GONE);
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }

    /**
     * 访问相册数据
     */
    private void getAlbumData(){
        if(errorIV.getVisibility()==View.VISIBLE){errorIV.setVisibility(View.GONE);}
        if(staggeredGridView.getVisibility()==View.VISIBLE){staggeredGridView.setVisibility(View.GONE);}
        if(loadingView.getVisibility()==View.GONE){loadingView.setVisibility(View.VISIBLE);}
        httpUtils.startAsyncGetRequest(RequestURLs.GET_ALL_ALBUMS_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    loadingView.setVisibility(View.GONE);
                    errorIV.setVisibility(View.VISIBLE);
                    staggeredGridView.setVisibility(View.GONE);
                    refreshView.stopRefresh();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseResult=response.body().string();
                albumsList=new ArrayList<>();
                Gson gson=new Gson();
                albumsList=gson.fromJson(responseResult,new TypeToken<List<UserAlbums>>(){}.getType());
                runOnUiThread(()->{
                    loadingView.setVisibility(View.GONE);
                    errorIV.setVisibility(View.GONE);
                    if(staggeredGridView.getVisibility()==View.GONE){staggeredGridView.setVisibility(View.VISIBLE);}
                    adapter=new AlbumListAdapter(albumsList,mReference.get(),imageLoader);
                    staggeredGridView.setAdapter(adapter);
                    staggeredGridView.setOnItemClickListener(new MyAlbumItemClickListener());
                    staggeredGridView.setOnScrollListener(new PauseOnScrollListener(imageLoader,false,true));
                    refreshView.stopRefresh();
                });
            }
        });
    }

    /**
     * gridview点击事件
     */
    private class MyAlbumItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent(mReference.get(),AlbumDetailActivity.class);
            intent.putExtra("albumid",albumsList.get(position).getAlbumid());
            intent.putExtra("title",albumsList.get(position).getAlbumname());
            startActivity(intent);
        }
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(R.string.index_nice_shot_text);
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolbar.setNavigationOnClickListener(v -> finish());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_index,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 刷新时调用
     */
    private class AlbumListRefreshViewListener implements XRefreshView.XRefreshViewListener {
        @Override
        public void onRefresh() {
            new Handler().postDelayed(() -> {
                isNetworkAvailable=CommonUtils.isNetworkAvailable(mReference.get());
                if(isNetworkAvailable){
                    getAlbumData();
                    ToastUtils.ShortToast(R.string.refresh_text);
                }else{
                    refreshView.stopRefresh();
                    ToastUtils.ShortToast(R.string.no_network_connection);
                }
            }, 1500);
        }
        @Override
        public void onLoadMore(boolean isSilence) {}
        @Override
        public void onRelease(float direction) {}
        @Override
        public void onHeaderMove(double headerMovePercent, int offsetY) {}
    }
}
