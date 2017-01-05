package zjut.salu.share.fragment.user_order_list_fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import mehdi.sakout.dynamicbox.DynamicBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.adapter.user_order.UserPayedOrderListAdapter;
import zjut.salu.share.exception.MyException;
import zjut.salu.share.model.OrderItem;
import zjut.salu.share.model.Product;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.MyJsonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.PullRefreshUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;

/**已经付款订单fragment
 * Created by Alan on 2016/10/27.
 */

public class UserPayedOrderFragment extends Fragment {
    private Context context;
    private Boolean isNetWorkAlive=false;
    private OkHttpUtils httpUtils;
    private DynamicBox dynamicBox=null;
    @Nullable@Bind(R.id.recycle_view_payed_order_list) ListView listView;
    @Nullable@Bind(R.id.tv_payed_no_order)TextView noOrderTV;
    @Nullable@Bind(R.id.xref_payed)XRefreshView refreshView;
    private List<OrderItem> items=null;
    private UserPayedOrderListAdapter payedAdapter=null;
    private WeakReference<ListView> listViewWeakReference=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isNetWorkAlive= CommonUtils.isNetworkAvailable(getActivity());
        View payedView=null;
        if(isNetWorkAlive){
            payedView=inflater.inflate(R.layout.fragment_user_payed_order,container,false);
            ButterKnife.bind(this,payedView);
            httpUtils=new OkHttpUtils();
            listViewWeakReference=new WeakReference<>(listView);
            PullRefreshUtils.bindPullRefreshView(refreshView,true,false,new PayedOrderListRefreshViewListener());
            dynamicBox=new DynamicBox(getActivity(),listView);
            DynamicBoxUtils.bindDynamicBox(dynamicBox,getString(R.string.hint_text),getString(R.string.server_exception_text),
                    getString(R.string.loading_now_text),new GetDataFailedClickListener());
            dynamicBox.showLoadingLayout();
            getPayedData();
        }else{
            payedView=inflater.inflate(R.layout.layout_connect_error,container,false);
            ButterKnife.bind(this,payedView);
            ToastUtils.LongToast(R.string.no_network_connection);
        }
        context=getActivity();
        return payedView;
    }

    /**
     * 获取失败retry按钮
     */
    private class GetDataFailedClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            getPayedData();//重新获取数据
        }
    }

    /**
     * 获取已付款订单
     */
    private void getPayedData(){
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("userid", PreferenceUtils.getString("userid",null));
        params.add(map);
        try {
            httpUtils.startPostRequestWithParams(params, RequestURLs.GET_USER_PAYED_ORDER_LIST_URL,new GetUserPayedResponseCallback());
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取已支付订单的回调事件
     */
    private class GetUserPayedResponseCallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {((Activity)context).runOnUiThread(() -> {
            if(dynamicBox!=null){
                dynamicBox.showExceptionLayout();
            }
            ToastUtils.ShortToast(R.string.server_down_text);
        });}
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                final String responseResult=response.body().string();
                ((Activity)context).runOnUiThread(() -> {
                    if(responseResult.equals("null")){//没有已支付订单
                        listView.setVisibility(View.GONE);//隐藏recycleview
                        if(dynamicBox!=null){dynamicBox.hideAll();}
                        noOrderTV.setVisibility(View.VISIBLE);
                    }else{//有支付订单
                        if(dynamicBox!=null){dynamicBox.hideAll();}
                        listView.setVisibility(View.VISIBLE);
                        refreshView.setVisibility(View.VISIBLE);
                        noOrderTV.setVisibility(View.GONE);
                        try {
                            JSONArray array=new JSONArray(responseResult);
                            Log.i("PAYEDORDERLIST",responseResult);
                            items=new ArrayList<>();
                            List<OrderItem> resultData= MyJsonUtils.getOrderItemFromJSONArray(array);
                            items=resultData;//赋值
                            //创建适配器对象
                            payedAdapter=new UserPayedOrderListAdapter(context,items,listViewWeakReference.get());
                            listView.setAdapter(payedAdapter);
                            //TODO:为订单绑定点击事件
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                ((Activity)context).runOnUiThread(() -> {
                    if(dynamicBox!=null){dynamicBox.showExceptionLayout();}
                });
            }
        }
    }

    /**
     * 下拉刷新监听事件
     */
    private class PayedOrderListRefreshViewListener implements XRefreshView.XRefreshViewListener{
        @Override
        public void onRefresh() {//下拉刷新时回调
            new Handler().postDelayed(() -> {
                isNetWorkAlive=CommonUtils.isNetworkAvailable(context);
                if(isNetWorkAlive){
                    getPayedData();
                    refreshView.stopRefresh();
                    ToastUtils.ShortToast(R.string.refresh_text);
                }else{
                    refreshView.stopRefresh();
                    ToastUtils.ShortToast(R.string.no_network_connection);
                }
            }, 2000);
        }
        @Override
        public void onLoadMore(boolean isSilence) {//上拉加载时回调

        }
        @Override
        public void onRelease(float direction) {//手指释放时回调

        }
        @Override
        public void onHeaderMove(double headerMovePercent, int offsetY) {//顶部移动时回调

        }
    }

}
