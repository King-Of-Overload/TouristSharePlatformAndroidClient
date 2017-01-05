package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mingle.widget.LoadingView;
import com.yyydjk.library.DropDownMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
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
import zjut.salu.share.adapter.product.MyConstellationAdapter;
import zjut.salu.share.adapter.product.MyGirdDropDownAdapter;
import zjut.salu.share.adapter.product.MyListDropDownAdapter;
import zjut.salu.share.adapter.product.ProductListAdapter;
import zjut.salu.share.base.AbsBaseActivityWithBar;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.exception.MyException;
import zjut.salu.share.model.Product;
import zjut.salu.share.model.ProductCategory;
import zjut.salu.share.model.ProductSecondCategory;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DeviceUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;

/**
 * 旅行装备界面
 */
public class ProductActivity extends RxBaseActivity{
    private Boolean isNetworkAvailable=false;
    private OkHttpUtils okHttpUtils=null;
    private DynamicBox dynamicBox=null;
    private WeakReference<Activity> mReference=null;
    private WeakReference<ListView> listViewReference=null;
    private ProductListAdapter adapter=null;

    private List<View> popupViews = new ArrayList<>();//弹出视图
    private List<ProductCategory> categories=null;
    private MyListDropDownAdapter secondCategoryAdapter=null;
    private MyConstellationAdapter categoryAdapter=null;
    private MyListDropDownAdapter newOrNotAdapter=null;
    private int constellationPosition=0;
    private Boolean categoryIsLoaded=false;
    private List<String> categoryName=null;
    private List<String> secondCategoryName=null;
    private String headers[] = {"主分类","详细分类","成色"};
    private String[] status;

    private List<Product> products=null;

    private String cName="";
    private String scName="";
    private String pStatus="";
    @Bind(R.id.toolbar_product)Toolbar mToolBar;
    @Bind(R.id.linear_product)LinearLayout mainView;

    @Bind(R.id.dropDownMenu)DropDownMenu dropDownMenu;

    //动态生成控件
     private LinearLayout contentView;
     private XRefreshView refreshView;
    private ListView listView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_product;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        mReference=new WeakReference<Activity>(this);
        dynamicBox=new DynamicBox(mReference.get(),mainView);
        DynamicBoxUtils.bindDynamicBox(dynamicBox,getString(R.string.hint_text),getString(R.string.server_exception_text),
                getString(R.string.loading_now_text),new GetDataFailedClickListener());
        dynamicBox.showLoadingLayout();
        isNetworkAvailable= CommonUtils.isNetworkAvailable(mReference.get());
        if(isNetworkAvailable){
            //TODO:调用loadingview
            okHttpUtils=new OkHttpUtils();
            getCategoryAndProductsData("全部","全部","全新");
        }else{
            dynamicBox.showExceptionLayout();
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }

    /**
     * 获取数据
     */
    private void getCategoryAndProductsData(String cName,String scName,String qualityName){
        List<Map<String,Object>> params=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("cName",cName);
        map.put("scName",scName);
        map.put("qualityName",qualityName);
        params.add(map);
        try {
            okHttpUtils.startPostRequestWithParams(params,RequestURLs.GET_CATEGORY_AND_PRODUCTS_URL,new ProductResponseCallback());
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据获取回调
     */
    private class ProductResponseCallback implements Callback{

        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dynamicBox.showExceptionLayout();
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                String responseString=response.body().string();
                try {
                    JSONObject object=new JSONObject(responseString);
                    String categoryString=object.getString("categoryResult");
                    String productString=object.getString("products");
                    Gson gson=new Gson();
                    products=new ArrayList<>();
                    if(!productString.equals("null")){
                        products=gson.fromJson(productString,new TypeToken<List<Product>>(){}.getType());
                    }
                    categories=new ArrayList<>();
                    categories=gson.fromJson(categoryString,new TypeToken<List<ProductCategory>>(){}.getType());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!categoryIsLoaded){
                                categoryName=new ArrayList<>();
                                categoryName.add("全部");
                                for(ProductCategory c:categories){
                                    categoryName.add(c.getPcategoryname());
                                }
                                secondCategoryName=new ArrayList<>();
                                secondCategoryName.add("全部");
                                final View constellationView = getLayoutInflater().inflate(R.layout.custom_layout, null);
                                GridView constellation = ButterKnife.findById(constellationView, R.id.constellation);
                                categoryAdapter = new MyConstellationAdapter(mReference.get(), categoryName);
                                constellation.setAdapter(categoryAdapter);
                                TextView okBtn = ButterKnife.findById(constellationView, R.id.ok);//确定按钮
                                okBtn.setOnClickListener(new OkBtnClickListener());
                                //二级分类
                                final ListView secondView=new ListView(mReference.get());
                                secondCategoryAdapter=new MyListDropDownAdapter(mReference.get(),secondCategoryName);
                                secondView.setAdapter(secondCategoryAdapter);
                                //新旧
                                final ListView newOrOldView=new ListView(mReference.get());
                                status=new String[]{"全新","99新","98新","95新","90新","80新及以下"};
                                newOrNotAdapter=new MyListDropDownAdapter(mReference.get(),Arrays.asList(status));
                                newOrOldView.setAdapter(newOrNotAdapter);
                                popupViews.add(constellationView);
                                popupViews.add(secondView);
                                popupViews.add(newOrOldView);
                                constellation.setOnItemClickListener(new CategoryItemClickListener());//一级分类选项点击事件
                                secondView.setOnItemClickListener(new SecondCategoryClickListener());//二级分类点击事件
                                newOrOldView.setOnItemClickListener(new NewOrOldItemClickListener());//新旧选项点击事件
                                //init 显示区域
                                contentView=new LinearLayout(mReference.get());
                                contentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                                contentView.setOrientation(LinearLayout.VERTICAL);
                                contentView.setGravity(Gravity.CENTER);
                                refreshView=new XRefreshView(mReference.get());
                                LinearLayout.LayoutParams refreshParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                                refreshParams.setMargins(0,100,0,0);
                                refreshView.setLayoutParams(refreshParams);
                                if(DeviceUtils.hasJellyBean_MR1()){
                                    refreshView.setId(View.generateViewId());
                                }else{
                                    refreshView.setId(StringUtils.generateViewId());
                                }
                                listView=new ListView(mReference.get());
                                LinearLayout.LayoutParams listViewParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                                listView.setLayoutParams(listViewParams);
                                listView.setDividerHeight(1);
                                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.gray)));
                                if(DeviceUtils.hasJellyBean_MR1()){
                                    listView.setId(View.generateViewId());
                                }else{
                                    listView.setId(StringUtils.generateViewId());
                                }
                                refreshView.addView(listView);
                                contentView.addView(refreshView);
                                listViewReference=new WeakReference<>(listView);
                                dropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
                                categoryIsLoaded=true;
                                refreshView.setXRefreshViewListener(new MyListViewRefreshListener());
                            }
                            //显示列表数据
                            adapter=new ProductListAdapter(listViewReference.get(),products,mReference.get());
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new ProductListItemClickListener());
                            dynamicBox.hideAll();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dynamicBox.showExceptionLayout();
                        ToastUtils.ShortToast(R.string.server_down_text);
                    }
                });
            }
        }
    }

    /**
     * 商品列表点击事件
     */
    private class ProductListItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Product product=products.get(position);
            Intent intent=new Intent(mReference.get(),ProductDetailActivity.class);
            intent.putExtra("product",product);
            startActivity(intent);
        }
    }

    /**
     * 一级分类选项点击事件
     */
    private class CategoryItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            categoryAdapter.setCheckItem(position);
            constellationPosition = position;
        }
    }

    /**
     * 二级分类点击事件
     */
    private class SecondCategoryClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            secondCategoryAdapter.setCheckItem(position);
            dropDownMenu.setTabText(secondCategoryName.get(position));
            scName=secondCategoryName.get(position);
            dropDownMenu.closeMenu();
            isNetworkAvailable=CommonUtils.isNetworkAvailable(mReference.get());
            if(isNetworkAvailable){
                getCategoryAndProductsData(cName,scName,pStatus);
            }else{
                ToastUtils.ShortToast(R.string.no_network_connection);
            }
        }
    }

    /**
     * 一级分类确定按钮点击事件
     */
    private class OkBtnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            dropDownMenu.setTabText(categoryName.get(constellationPosition));
            cName=categoryName.get(constellationPosition);
            secondCategoryName.clear();
            if(constellationPosition==0){
                secondCategoryName.add("全部");
            }else{
                List<ProductSecondCategory> secondCategory=categories.get(constellationPosition-1).getSecondCategories();
                secondCategoryName.add("全部");

                for(ProductSecondCategory sc:secondCategory){
                    secondCategoryName.add(sc.getPcsname());
                }
            }
            secondCategoryAdapter.notifyDataSetChanged();
            dropDownMenu.closeMenu();
            isNetworkAvailable=CommonUtils.isNetworkAvailable(mReference.get());
            if(isNetworkAvailable){
                getCategoryAndProductsData(cName,scName,pStatus);
            }else{
                ToastUtils.ShortToast(R.string.no_network_connection);
            }
        }
    }

    /**
     * 新旧选项点击事件
     */
    private class NewOrOldItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            newOrNotAdapter.setCheckItem(position);
            dropDownMenu.setTabText(status[position]);
            pStatus=status[position];
            dropDownMenu.closeMenu();
            isNetworkAvailable=CommonUtils.isNetworkAvailable(mReference.get());
            if(isNetworkAvailable){
                getCategoryAndProductsData(cName,scName,pStatus);
            }else{
                ToastUtils.ShortToast(R.string.no_network_connection);
            }
        }
    }

    /**
     * 下拉刷新监听
     */
    private class MyListViewRefreshListener implements XRefreshView.XRefreshViewListener{

        @Override
        public void onRefresh() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isNetworkAvailable=CommonUtils.isNetworkAvailable(mReference.get());
                    if(isNetworkAvailable){
                        getCategoryAndProductsData(cName,scName,pStatus);
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

    @Override
    public void initToolBar() {
        mToolBar.setTitle(getText(R.string.index_trip_equipment_text));
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 出现异常重试按钮
     */
    private class GetDataFailedClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            isNetworkAvailable=CommonUtils.isNetworkAvailable(mReference.get());
            if(isNetworkAvailable){
                getCategoryAndProductsData(cName,scName,pStatus);
            }else{
                ToastUtils.ShortToast(R.string.no_network_connection);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(dropDownMenu.isShowing()){
            dropDownMenu.closeMenu();
        }else{
            super.onBackPressed();
        }
    }
}
