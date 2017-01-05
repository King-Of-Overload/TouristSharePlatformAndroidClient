package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.andview.refreshview.XRefreshView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import zjut.salu.share.adapter.love_card.LoveCardListAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.LovePostCard;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.PullSeparateListView;

/**
 * 爱の明信片
 */
public class LoveCardActivity extends RxBaseActivity {
    @Bind(R.id.pull_lv_love_card)PullSeparateListView listView;
    @Bind(R.id.toolbar_love_card)Toolbar mToolBar;
    @Bind(R.id.pull_refresh_view_love_card) XRefreshView refreshView;
    @Bind(R.id.view_error_connection_love_card)LinearLayout noNetErrorView;//无网络界面

    private WeakReference<Activity> mReference=null;
    private WeakReference<PullSeparateListView> listViewWeakReference=null;
    private Boolean isNetworkAvailable=false;
    private DynamicBox dynamicBox=null;
    private List<LovePostCard> postCards=null;
    private LoveCardListAdapter adapter=null;

    private OkHttpUtils okHttpUtils=null;
    @Override
    public int getLayoutId() {
        return R.layout.activity_love_card;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        listViewWeakReference=new WeakReference<>(listView);
        okHttpUtils=new OkHttpUtils();
        PullRefreshUtils.bindPullRefreshView(refreshView,true,false,new LovePostCardListRefreshViewListener());
        dynamicBox=new DynamicBox(mReference.get(),listView);
        DynamicBoxUtils.bindDynamicBox(dynamicBox,getString(R.string.hint_text),getString(R.string.server_exception_text),
                getString(R.string.loading_now_text),new GetDataFailedClickListener());
        isNetworkAvailable= CommonUtils.isNetworkAvailable(mReference.get());
        if(isNetworkAvailable){
            dynamicBox.showLoadingLayout();
            getLoveCardData();
        }else {
            if(listView.getVisibility()==View.VISIBLE){listView.setVisibility(View.GONE);}
            if(noNetErrorView.getVisibility()==View.VISIBLE){noNetErrorView.setVisibility(View.VISIBLE);}
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }


    /**
     * 获取明信片数据
     */
    private void getLoveCardData(){
        okHttpUtils.startAsyncGetRequest(RequestURLs.GET_ALL_LOVE_POST_CARD_URL,new GetLoveCardDataResponseCallback());
    }

    /**
     * 明信片数据获取回调
     */
    private class GetLoveCardDataResponseCallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(() -> {
                dynamicBox.showExceptionLayout();
                ToastUtils.ShortToast(R.string.server_down_text);
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                String responseResult=response.body().string();
                postCards=new ArrayList<>();
                Gson gson=new Gson();
                postCards=gson.fromJson(responseResult,new TypeToken<List<LovePostCard>>(){}.getType());
                runOnUiThread(() -> {
                    if(noNetErrorView.getVisibility()==View.VISIBLE){noNetErrorView.setVisibility(View.GONE);}
                    if(listView.getVisibility()==View.GONE){listView.setVisibility(View.VISIBLE);}
                    dynamicBox.hideAll();
                    adapter=new LoveCardListAdapter(mReference.get(),postCards,listViewWeakReference.get());
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new PostCardOnItemClickListener());
                });
            }else{
                runOnUiThread(() -> {
                    dynamicBox.showExceptionLayout();
                    ToastUtils.ShortToast(R.string.server_error_text);
                });
            }
        }
    }

    /**
     * listview单个条目点击事件
     */
    private class PostCardOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LovePostCard card=postCards.get(position);
            Intent intent=new Intent(mReference.get(),LoveCardDetailActivity.class);
            intent.putExtra("love_card",card);
            startActivity(intent);
        }
    }

    /**
     * 出现异常重试按钮
     */
    private class GetDataFailedClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            getLoveCardData();
        }
    }


    /**
     * 下拉刷新监听器
     */
    private class LovePostCardListRefreshViewListener implements XRefreshView.XRefreshViewListener{

        @Override
        public void onRefresh() {
            new Handler().postDelayed(() -> {
                isNetworkAvailable=CommonUtils.isNetworkAvailable(mReference.get());
                if(isNetworkAvailable){
                    getLoveCardData();
                    refreshView.stopRefresh();
                    ToastUtils.ShortToast(R.string.refresh_text);
                }else{
                    refreshView.stopRefresh();
                    ToastUtils.ShortToast(R.string.no_network_connection);
                }
            }, 2000);
        }

        @Override
        public void onLoadMore(boolean isSilence) {

        }

        @Override
        public void onRelease(float direction) {

        }

        @Override
        public void onHeaderMove(double headerMovePercent, int offsetY) {

        }
    }


    @Override
    public void initToolBar() {
        mToolBar.setTitle(getText(R.string.index_love_card_text));
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolBar.setNavigationOnClickListener(v -> finish());

    }
}
