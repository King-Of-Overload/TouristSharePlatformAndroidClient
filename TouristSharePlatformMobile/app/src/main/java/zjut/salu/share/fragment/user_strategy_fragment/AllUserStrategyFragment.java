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
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import mehdi.sakout.dynamicbox.DynamicBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.activity.UserStrategyDetailActivity;
import zjut.salu.share.adapter.user_strategy.AllUserStrategyListAdapter;
import zjut.salu.share.fragment.user_order_list_fragment.UserPayedOrderFragment;
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;

/**全部用户攻略
 * Created by Alan on 2016/10/28.
 */

public class AllUserStrategyFragment extends Fragment {
    private String TAG="AllUserStrategyFragment";
    @Nullable @Bind(R.id.lv_all_user_strategy)ListView allUserStrategyListView;
    @Nullable @Bind(R.id.xref_all_user_strategy)XRefreshView refreshView;
    @Nullable @Bind(R.id.layout__user_strategy_no_net)LinearLayout noNetLayout;
    private WeakReference<ListView> listViewWeakReference=null;
    private Context context=null;
    Boolean isNetWorkAlive=false;//网络状态
    private OkHttpUtils httpUtils=null;
    private DynamicBox dynamicBox=null;
    private List<UserStrategy> allStrategyList=null;
    private AllUserStrategyListAdapter adapter=null;
    private Activity mActivity=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View allStrategyView=null;
        context=getActivity();
        isNetWorkAlive= CommonUtils.isNetworkAvailable(getActivity());
        allStrategyView=inflater.inflate(R.layout.fragment_all_user_strategy,container,false);
        ButterKnife.bind(this,allStrategyView);
        httpUtils=new OkHttpUtils();
        listViewWeakReference=new WeakReference<ListView>(allUserStrategyListView);
        PullRefreshUtils.bindPullRefreshView(refreshView,true,false,new AllUserStrategyRefreshListener());
        dynamicBox=new DynamicBox(context,allUserStrategyListView);
        DynamicBoxUtils.bindDynamicBox(dynamicBox,getString(R.string.hint_text),getString(R.string.server_exception_text),
                getString(R.string.loading_now_text),new GetDataFailedClickListener());
        if(isNetWorkAlive==true){//网络正常
            dynamicBox.showLoadingLayout();
            getAllUserStrategyData();
        }else{
            allUserStrategyListView.setVisibility(View.GONE);
            noNetLayout.setVisibility(View.VISIBLE);
            Log.i(TAG,"没有网络");
        }
        return allStrategyView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity=getActivity();
    }

    /**
     * 访问网络获取所有攻略游记数据
     */
    private void getAllUserStrategyData(){
        httpUtils.startAsyncGetRequest(RequestURLs.GET_ALL_USER_STRATEGY_URL,new GetAllUserStrategyResponseCallback());
    }

    /**
     * 网络访问回调
     */
    private class GetAllUserStrategyResponseCallback implements Callback{
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
                        if(allUserStrategyListView.getVisibility()==View.GONE){allUserStrategyListView.setVisibility(View.VISIBLE);}
                        allStrategyList=new ArrayList<UserStrategy>();
                        Gson gson=new Gson();
                        allStrategyList=gson.fromJson(responseResult,new TypeToken<List<UserStrategy>>(){}.getType());
                        adapter=new AllUserStrategyListAdapter(listViewWeakReference.get(),context,allStrategyList);
                        allUserStrategyListView.setAdapter(adapter);
                        allUserStrategyListView.setOnItemClickListener(new AllUserStrategyListViewItemClickListener());
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
     * list_view点击事件监听器
     */
    private class AllUserStrategyListViewItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UserStrategy strategy=allStrategyList.get(position);
            Intent intent=new Intent(context, UserStrategyDetailActivity.class);
            intent.putExtra("user_strategy",strategy);
            startActivity(intent);
        }
    }

    /**
     * 下拉刷新监听器
     */
    private class AllUserStrategyRefreshListener implements XRefreshView.XRefreshViewListener{
        @Override
        public void onRefresh() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isNetWorkAlive=CommonUtils.isNetworkAvailable(context);
                    if(isNetWorkAlive==true){
                        getAllUserStrategyData();
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
            getAllUserStrategyData();
        }
    }
}
