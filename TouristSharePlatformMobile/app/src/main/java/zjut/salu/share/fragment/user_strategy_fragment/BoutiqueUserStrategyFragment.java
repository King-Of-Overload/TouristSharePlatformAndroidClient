package zjut.salu.share.fragment.user_strategy_fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mehdi.sakout.dynamicbox.DynamicBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.activity.UserStrategyDetailActivity;
import zjut.salu.share.adapter.user_strategy.AllUserStrategyListAdapter;
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;

/**精品用户攻略游记
 * Created by Alan on 2016/10/28.
 */

public class BoutiqueUserStrategyFragment extends Fragment {
    private String TAG="BoutiqueUserStrategy";
    @Nullable @Bind(R.id.lv_boutique_user_strategy)ListView boutiqueUserStrategyListView;
    @Nullable @Bind(R.id.xref_boutique_user_strategy)XRefreshView refreshView;
    @Nullable @Bind(R.id.layout_user_strategy_no_net)LinearLayout noNetLayout;
    private WeakReference<ListView> listViewWeakReference=null;
    private Context context=null;
    Boolean isNetWorkAlive=false;//网络状态
    private OkHttpUtils httpUtils=null;
    private DynamicBox dynamicBox=null;
    private List<UserStrategy> boutiqueStrategyList=null;
    private AllUserStrategyListAdapter adapter=null;
    private Activity mActivity=null;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View boutiqueStrategyView=null;
        context=getActivity();
        isNetWorkAlive= CommonUtils.isNetworkAvailable(getActivity());
        boutiqueStrategyView=inflater.inflate(R.layout.fragment_boutique_user_strategy,container,false);
        ButterKnife.bind(this,boutiqueStrategyView);
        httpUtils=new OkHttpUtils();
        listViewWeakReference=new WeakReference<ListView>(boutiqueUserStrategyListView);
        PullRefreshUtils.bindPullRefreshView(refreshView,true,false,new BoutiqueUserStrategyRefreshListener());
        dynamicBox=new DynamicBox(context,boutiqueUserStrategyListView);
        DynamicBoxUtils.bindDynamicBox(dynamicBox,getString(R.string.hint_text),getString(R.string.server_exception_text),
                getString(R.string.loading_now_text),new GetDataFailedClickListener());
        return boutiqueStrategyView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity=getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isNetWorkAlive==true){//网络正常
            dynamicBox.showLoadingLayout();
            getBoutiqueUserStrategyData();
        }else{
            boutiqueUserStrategyListView.setVisibility(View.GONE);
            noNetLayout.setVisibility(View.VISIBLE);
            Log.i(TAG,"没有网络");
        }
    }

    /**
     * 访问网络获取精品用户攻略游记数据
     */
    private void getBoutiqueUserStrategyData(){
        httpUtils.startAsyncGetRequest(RequestURLs.GET_BOUTIQUE_USER_STRATEGY_URL,new GetBoutiqueUserStrategyResponseCallback());
    }

    /**
     * 网络访问回调
     */
    private class GetBoutiqueUserStrategyResponseCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noNetLayout.setVisibility(View.GONE);
                    dynamicBox.showExceptionLayout();
                    ToastUtils.ShortToast(R.string.server_down_text);
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                final String responseResult=response.body().string();
                Log.i(TAG,responseResult);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noNetLayout.setVisibility(View.GONE);
                        dynamicBox.hideAll();
                        if(refreshView.getVisibility()==View.GONE){refreshView.setVisibility(View.VISIBLE);}
                        if(boutiqueUserStrategyListView.getVisibility()==View.GONE){boutiqueUserStrategyListView.setVisibility(View.VISIBLE);}
                        boutiqueStrategyList=new ArrayList<UserStrategy>();
                        Gson gson=new Gson();
                        boutiqueStrategyList=gson.fromJson(responseResult,new TypeToken<List<UserStrategy>>(){}.getType());
                        adapter=new AllUserStrategyListAdapter(listViewWeakReference.get(),context,boutiqueStrategyList);
                        boutiqueUserStrategyListView.setAdapter(adapter);
                        boutiqueUserStrategyListView.setOnItemClickListener(new BoutiqueStrategyItemClickListener());
                    }
                });
            }else{
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dynamicBox.showExceptionLayout();
                    }
                });
            }
        }
    }

    /**
     * list_view点击事件
     */
    private class BoutiqueStrategyItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UserStrategy strategy=boutiqueStrategyList.get(position);
            Intent intent=new Intent(context, UserStrategyDetailActivity.class);
            intent.putExtra("user_strategy",strategy);
            startActivity(intent);
        }
    }


    /**
     * 下拉刷新监听器
     */
    private class BoutiqueUserStrategyRefreshListener implements XRefreshView.XRefreshViewListener{
        @Override
        public void onRefresh() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isNetWorkAlive=CommonUtils.isNetworkAvailable(context);
                    if(isNetWorkAlive==true){
                        getBoutiqueUserStrategyData();
                        refreshView.stopRefresh();
                        ToastUtils.ShortToast(R.string.refresh_text);
                    }else{
                        refreshView.stopRefresh();
                        ToastUtils.ShortToast(R.string.no_network_connection);
                    }
                }
            }, 2000);
        }
        @Override
        public void onLoadMore(boolean isSilence) {}
        @Override
        public void onRelease(float direction) {}
        @Override
        public void onHeaderMove(double headerMovePercent, int offsetY) {}
    }

    /**
     * 重试按钮点击事件
     */
    private class GetDataFailedClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            getBoutiqueUserStrategyData();
        }
    }


}
