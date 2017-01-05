package zjut.salu.share.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.config.CuteTouristShareConfig;
import zjut.salu.share.model.Currency;
import zjut.salu.share.myinterface.OnFoldListener;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.FoldingLayout;

/**
 * 汇率换算
 */
public class ExchangeCurrencyActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar mToolbar;
    @Bind(R.id.et_china_currency)EditText chinaCurrencyET;
    @Bind(R.id.et_foreign_currency)EditText foreignCurrencyET;
    @Bind(R.id.btn_choose_country)Button chooseCountryBtn;
    private String currentCurrencyCode="";//当前货币代码
    private static final int GET_CURRENCY_TYPE=0;

    private WeakReference<Activity> mReference;
    private OkHttpUtils httpUtils;
    @Override
    public int getLayoutId() {
        return R.layout.activity_exchange_currency;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        httpUtils=new OkHttpUtils();
        chinaCurrencyET.addTextChangedListener(new TextWatcher() {//中国货币框发生变化
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    foreignCurrencyET.setText("");
                    String chinaCurrency=chinaCurrencyET.getText().toString().trim();
                    if(StringUtils.isDecimalNumber(chinaCurrency)){
                        if(!currentCurrencyCode.equals("")){
                            doExchange("CNY",currentCurrencyCode,"china",chinaCurrency);
                        }else {
                            ToastUtils.ShortToast(R.string.please_choose_currency_text);
                            ToastUtils.ShortToast(R.string.please_choose_currency_text);
                            Intent intent=new Intent(mReference.get(),ExchangeTypeActivity.class);
                            startActivityForResult(intent,GET_CURRENCY_TYPE);
                        }
                    }
                }
            }
        });
        foreignCurrencyET.addTextChangedListener(new TextWatcher() {//外币发生变化
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    chinaCurrencyET.setText("");
                    String foreignCurrency=foreignCurrencyET.getText().toString().trim();
                    if(StringUtils.isDecimalNumber(foreignCurrency)){
                        if(!currentCurrencyCode.equals("")){
                            doExchange(currentCurrencyCode,"CNY","foreign",foreignCurrency);
                        }else {
                            ToastUtils.ShortToast(R.string.please_choose_currency_text);
                            Intent intent=new Intent(mReference.get(),ExchangeTypeActivity.class);
                            startActivityForResult(intent,GET_CURRENCY_TYPE);
                        }
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * 进行货币转换
     * @param from 转换前的货币代码
     * @param to  转换后的货币代码
     * @param type 类型  china foreign
     * @param money 金额
     */
    private void doExchange(String from,String to,String type,String money){
        Map<String,Object> params=new HashMap<>();
        params.put("key",CommonUtils.getJuheCurrencyAppkey());
        params.put("from",from);
        params.put("to",to);
        Observable<String> observable=httpUtils.asyncGetRequest(RequestURLs.GET_CURRENCY_EXCHANGE,params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i(ExchangeCurrencyActivity.class.getSimpleName(),"查询成功");}

                    @Override
                    public void onError(Throwable e) {ToastUtils.ShortToast(R.string.server_down_text);}

                    @Override
                    public void onNext(String result) {
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            if(jsonObject.getInt("error_code")==0){
                                JSONArray array=jsonObject.getJSONArray("result");
                                JSONObject object=array.getJSONObject(0);
                                Double exchangeRate=object.getDouble("result");
                                Double exchangeMoney=Double.parseDouble(money)*exchangeRate;//转换完毕的金钱数
                                updateMoneyUI(exchangeMoney,type);
                            }else{
                                ToastUtils.ShortToast(R.string.server_down_text);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 更新金额
     * @param result
     * @param type 类型 china foreign
     */
    private void updateMoneyUI(Double result,String type){
        if(type.equals("china")){//更新外币金额
            foreignCurrencyET.setText(String.valueOf(result));
        }else if(type.equals("foreign")){//更新人民币金额
            chinaCurrencyET.setText(String.valueOf(result));
        }
    }

    /**
     * 选择币种点击事件
     * @param v
     */
    @OnClick(R.id.btn_choose_country)
    public void chooseCountryClick(View v){
        Intent intent=new Intent(mReference.get(),ExchangeTypeActivity.class);
        startActivityForResult(intent,GET_CURRENCY_TYPE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK){
            return;
        }
        switch (requestCode){
            case GET_CURRENCY_TYPE:{//选择币种回调
                String newCurrencyName=data.getStringExtra("newCurrencyName");
                currentCurrencyCode=data.getStringExtra("newCurrencyCode");
                chooseCountryBtn.setText(newCurrencyName);
                break;
            }
        }
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(getString(R.string.currency_text));
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolbar.setNavigationOnClickListener(v->finish());
    }
}
