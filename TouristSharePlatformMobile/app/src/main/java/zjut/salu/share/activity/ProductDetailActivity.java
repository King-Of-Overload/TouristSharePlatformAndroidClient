package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingle.widget.LoadingView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import mehdi.sakout.dynamicbox.DynamicBox;
import zjut.salu.share.R;
import zjut.salu.share.adapter.product.ProductImgViewPagerAdapter;
import zjut.salu.share.base.AbsBaseActivityWithBar;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.config.CuteTouristShareConfig;
import zjut.salu.share.model.City;
import zjut.salu.share.model.Product;
import zjut.salu.share.model.Provinces;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DomUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.ImageUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.utils.WebViewUtils;
import zjut.salu.share.widget.HackyViewPager;

/**
 * 产品详情界面
 */
public class ProductDetailActivity extends RxBaseActivity {
    @Bind(R.id.toolbar_product_detail)Toolbar mToolBar;
    @Bind(R.id.collapsing_toolbar_product_detail)CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.hacky_view_product_detail)HackyViewPager viewPager;//产品展示区
    @Bind(R.id.coordinator_product_detail)CoordinatorLayout mCoordinatorLayout;//主页面区域
    @Bind(R.id.loadView_product_detail)LoadingView loadingView;//加载页面
    @Bind(R.id.tv_name_product_detail)TextView titleTV;//标题
    @Bind(R.id.tv_shop_price_product_detail)TextView shopPriceTV;//商城价
    @Bind(R.id.tv_market_price_product_detail)TextView marketPriceTV;//市场价
    @Bind(R.id.tv_phone_product_detail)TextView phoneTV;//电话
    @Bind(R.id.tv_pay_attention_product_detail)TextView payAttentionTV;//关注量
    @Bind(R.id.tv_city_product_detail)TextView cityTV;//城市
    @Bind(R.id.tv_description_product_detail)TextView descTV;//简介
    @Bind(R.id.web_view_product_detail)WebView detailWebView;//详情
    @Bind(R.id.nested_scroll_view_product_detail)NestedScrollView nestedScrollView;


    private ArrayList<String> productImgRes=new ArrayList<>();
    private int imagePosition=0;
    private List<View> images=null;//所有产品图片集合

    private WeakReference<Activity> mReference=null;
    private WeakReference<WebView> webViewReference=null;
    private Boolean isNetWorkAvailable=false;
    private Product currentProduct=null;
    private DynamicBox dynamicBox=null;
    private ImageLoader imageLoader=null;
    private DisplayImageOptions options=null;
    @Override
    public int getLayoutId() {
        return R.layout.activity_product_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        webViewReference=new WeakReference<>(detailWebView);
        imageLoader=ImageLoader.getInstance();
        dynamicBox=new DynamicBox(mReference.get(),mCoordinatorLayout);
        Intent intent=getIntent();
        currentProduct= (Product) intent.getSerializableExtra("product");
        DynamicBoxUtils.bindDynamicBox(dynamicBox,getString(R.string.hint_text),getString(R.string.server_exception_text),
                getString(R.string.loading_now_text),new GetDataFailedClickListener());
        isNetWorkAvailable= CommonUtils.isNetworkAvailable(mReference.get());
        if(isNetWorkAvailable){
            if(loadingView.getVisibility()==View.INVISIBLE){loadingView.setVisibility(View.VISIBLE);}
            if(mCoordinatorLayout.getVisibility()==View.VISIBLE){mCoordinatorLayout.setVisibility(View.INVISIBLE);}
            initProductImages();//初始化图片区
            initProductData();
        }else{
            dynamicBox.showExceptionLayout();
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }

    /**
     * 初始化商品数据
     */
    private void initProductData(){
        //修正nestedscrollview底部布局问题
        ViewGroup.LayoutParams layoutParams = nestedScrollView.getLayoutParams();
        layoutParams.height = CuteTouristShareConfig.getScreenHeight(mReference.get()) - CuteTouristShareConfig.getActionBarHeight(mReference.get());
        //初始化数据
        titleTV.setText(currentProduct.getPname());
        shopPriceTV.setText(String.valueOf(currentProduct.getShopprice().setScale(2, BigDecimal.ROUND_HALF_UP)));
        marketPriceTV.setText(String.valueOf(currentProduct.getMarketprice().setScale(2, BigDecimal.ROUND_HALF_UP)));
        marketPriceTV.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        String phoneStr=getString(R.string.phone_text)+currentProduct.getContactnum();
        phoneTV.setText(phoneStr);
        payAttentionTV.setText(String.valueOf(currentProduct.getPclickednum()));
        String cityStr=currentProduct.getCity().getProvince().getProvincename()+" "+currentProduct.getCity().getCityname();
        cityTV.setText(cityStr);
        descTV.setText(currentProduct.getPdescription());
        //初始化webview
        String htmlContent=currentProduct.getPcontent();
        WebViewUtils.displayHTMLContent(htmlContent,webViewReference.get());
        dynamicBox.hideAll();
        new Handler().postDelayed(() -> {
            if(loadingView.getVisibility()==View.VISIBLE){loadingView.setVisibility(View.INVISIBLE);}
            if(mCoordinatorLayout.getVisibility()==View.INVISIBLE){mCoordinatorLayout.setVisibility(View.VISIBLE);}
        }, 3500);

    }


    /**
     * 初始化产品展示区
     */
    private void initProductImages(){
        options= ImageLoaderUtils.getImageOptions();
        productImgRes= ImageUtils.getImagesFromHtml(currentProduct.getPcontent(),4);
        if (images != null) {
            images.clear();
            images = null;
        }
        images = new ArrayList<>();
        for (int i = 0; i < productImgRes.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_product_pic, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.product_pic_item);
            imageLoader.displayImage(productImgRes.get(i), imageView, options);
            //imageView.setImageResource(productImgRes[i]);
            imageView.setOnClickListener(arg0 -> {
                //跳转到查看大图界面
                Intent intent = new Intent(mReference.get(),ProductBigImageActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("position",imagePosition);
                bundle.putStringArrayList("productImgRes",productImgRes);
                intent.putExtras(bundle);
                startActivity(intent);
            });
            images.add(view);//将实例化的view对象添加到数组中
        }
        ProductImgViewPagerAdapter adapter = new ProductImgViewPagerAdapter(images);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                imagePosition=arg0;//将当前图片的位置记录下来
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        viewPager.setAdapter(adapter);
    }

    /**
     * 访问网络失败点击刷新回调
     */
    private class GetDataFailedClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

        }
    }



    @Override
    public void initToolBar() {
        mToolBar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolBar.setNavigationOnClickListener(v -> finish());
        mCollapsingToolbarLayout.setTitle(currentProduct.getPname());
    }
}
