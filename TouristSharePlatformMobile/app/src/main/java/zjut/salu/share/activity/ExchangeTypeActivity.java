package zjut.salu.share.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.Currency;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.PullSeparateListView;

public class ExchangeTypeActivity extends RxBaseActivity{
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.pull_lv_exchange)PullSeparateListView listView;
    private List<Currency> currencyList=null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_exchange_type;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        WeakReference<Activity> mReference = new WeakReference<>(this);
        OkHttpUtils httpUtils = new OkHttpUtils();
        Boolean isNetworkAlive= CommonUtils.isNetworkAvailable(mReference.get());
        if(isNetworkAlive){
            List<Map<String,Object>> params=new ArrayList<>();
            Map<String,Object> map=new HashMap<>();
            map.put("key", CommonUtils.getJuheCurrencyAppkey());
            params.add(map);
            Observable<String> observable= httpUtils.asyncPostRequest(params, RequestURLs.GET_CURRENCY_LIST);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            List<Map<String, Object>> list = new LinkedList<>();
                            Map<String,Object> map;
                            for(int i=0;i<currencyList.size();i++){
                                map=new HashMap<>();
                                map.put("name",currencyList.get(i).getName());
                                map.put("code",currencyList.get(i).getCode());
                                list.add(map);
                            }
                            SimpleAdapter adapter=new SimpleAdapter(mReference.get(),list,R.layout.item_currency_list,new String[]{
                            "name","code"},new int[]{R.id.tv_currency_name,R.id.tv_currency_code});
                            listView.setAdapter(adapter);
                        }
                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.ShortToast(R.string.server_down_text);}

                        @Override
                        public void onNext(String result) {
                            try {
                                JSONObject object=new JSONObject(result);
                                if(object.getInt("error_code")==0){
                                    JSONObject res=object.getJSONObject("result");
                                    JSONArray array=res.getJSONArray("list");
                                    currencyList=new ArrayList<>();
                                    for(int i=0;i<array.length();i++){
                                        Currency currency=new Currency();
                                        JSONObject o=array.getJSONObject(i);
                                        currency.setName(o.getString("name"));
                                        currency.setCode(o.getString("code"));
                                        currencyList.add(currency);
                                    }
                                }else{
                                    ToastUtils.ShortToast(R.string.server_down_text);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }else{
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
        listView.setOnItemClickListener((parent, view, position, id) -> {//列表点击事件
            Currency currency=currencyList.get(position);
            setResult(RESULT_OK,new Intent().putExtra("newCurrencyName",currency.getName()).putExtra("newCurrencyCode",currency.getCode()));
            finish();
        });
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(R.string.currency_type_text);
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolbar.setNavigationOnClickListener(v->finish());
    }
}
