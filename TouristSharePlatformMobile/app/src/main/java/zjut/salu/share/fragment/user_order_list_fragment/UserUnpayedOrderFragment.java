package zjut.salu.share.fragment.user_order_list_fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import mehdi.sakout.dynamicbox.DynamicBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.adapter.user_order.UserUnPayedOrderListAdapter;
import zjut.salu.share.exception.MyException;
import zjut.salu.share.model.OrderItem;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.MyJsonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;

/**未付款订单
 * Created by Alan on 2016/10/27.
 */

public class UserUnpayedOrderFragment extends Fragment {
    private Context context;
    private Boolean isNetWorkAlive=false;
    private OkHttpUtils httpUtils;
    private DynamicBox dynamicBox=null;
    @Nullable @Bind(R.id.recycle_view_un_payed_order_list)ListView listView;
    @Nullable @Bind(R.id.tv_un_payed_no_order)TextView noOrderTV;
    @Nullable @Bind(R.id.xref_un_payed)XRefreshView refreshView;
    private List<OrderItem> items=null;
    private UserUnPayedOrderListAdapter payedAdapter=null;
    private WeakReference<ListView> listViewWeakReference=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isNetWorkAlive= CommonUtils.isNetworkAvailable(getActivity());
        View unpayedView=null;
        if(isNetWorkAlive==true){
            unpayedView=inflater.inflate(R.layout.fragment_user_unpayed_order,container,false);
            ButterKnife.bind(this,unpayedView);
            httpUtils=new OkHttpUtils();
            listViewWeakReference=new WeakReference<ListView>(listView);
            PullRefreshUtils.bindPullRefreshView(refreshView,true,false,new UnPayedOrderListRefreshViewListener());//绑定下拉刷新
            dynamicBox=new DynamicBox(getActivity(),listView);
            DynamicBoxUtils.bindDynamicBox(dynamicBox,getString(R.string.hint_text),getString(R.string.server_exception_text),
                    getString(R.string.loading_now_text),new GetDataFailedClickListener());
            dynamicBox.showLoadingLayout();
            getUnPayedData();
        }else{
            unpayedView=inflater.inflate(R.layout.layout_connect_error,container,false);
            ButterKnife.bind(this,unpayedView);
            ToastUtils.LongToast(R.string.no_network_connection);
        }
        context=getActivity();
        return unpayedView;
    }

    /**
     * 获取未付款订单数据
     */
    private void getUnPayedData(){
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("userid", PreferenceUtils.getString("userid",null));
        params.add(map);
        try {
            httpUtils.startPostRequestWithParams(params, RequestURLs.GET_USER_UN_PAYED_ORDER_LIST_URL,new GetUserUnPayedResponseCallback());
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取未支付订单的回调事件
     */
    private class GetUserUnPayedResponseCallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(dynamicBox!=null){
                        dynamicBox.showExceptionLayout();
                    }
                    ToastUtils.ShortToast(R.string.server_down_text);
                }
            });
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                final String responseResult=response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseResult.equals("null")){//没有未支付订单
                            listView.setVisibility(View.GONE);//隐藏recycleview
                            if(dynamicBox!=null){dynamicBox.hideAll();}
                            noOrderTV.setVisibility(View.VISIBLE);
                        }else{//有未支付订单
                            if(dynamicBox!=null){dynamicBox.hideAll();}
                            listView.setVisibility(View.VISIBLE);
                            refreshView.setVisibility(View.VISIBLE);
                            noOrderTV.setVisibility(View.GONE);
                            try {
                                JSONArray array=new JSONArray(responseResult);
                                Log.i("UNPAYEDORDERLIST",responseResult);
                                items=new ArrayList<OrderItem>();
                                List<OrderItem> resultData= MyJsonUtils.getOrderItemFromJSONArray(array);
                                items=resultData;//赋值
                                //创建适配器对象
                                payedAdapter=new UserUnPayedOrderListAdapter(context,items,listViewWeakReference.get());
                                listView.setAdapter(payedAdapter);
                                //TODO:为订单绑定点击事件
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dynamicBox!=null){dynamicBox.showExceptionLayout();}
                    }
                });
            }
        }
    }

    /**
     * 获取失败retry按钮
     */
    private class GetDataFailedClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            getUnPayedData();//重新获取数据
        }
    }

    /**
     * 下拉刷新监听事件
     */
    private class UnPayedOrderListRefreshViewListener implements XRefreshView.XRefreshViewListener{

        @Override
        public void onRefresh() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isNetWorkAlive==true){
                        getUnPayedData();
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
        public void onLoadMore(boolean isSilence) {

        }

        @Override
        public void onRelease(float direction) {

        }

        @Override
        public void onHeaderMove(double headerMovePercent, int offsetY) {

        }
    }



}
