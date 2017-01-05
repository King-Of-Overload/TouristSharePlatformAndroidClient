package zjut.salu.share.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.util.LruCache;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zjut.salu.share.R;
import zjut.salu.share.base.AbsBaseActivity;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DomUtils;
import zjut.salu.share.utils.MoveTouchBackUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;

/**
 * 用户攻略详细页面Control层
 */
public class UserStrategyDetailActivity extends RxBaseActivity{
    private WeakReference<Activity> mReference=null;
    private UserStrategy currentStrategy=null;
    private SimpleDateFormat format=null;
    private static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);//总内存
    private static int cacheSize = maxMemory / 12;//缓存大小
    public static LruCache<String, Bitmap> mMemoryCache;//缓存对象
    private OkHttpUtils okHttpUtils=null;
    private Boolean isNetworkAvailable=false;
    @Bind(R.id.tv_author_name_detail_user_strategy)TextView authorNameTV;//作者名字
    @Bind(R.id.tv_time_detail_user_strategy)TextView publishTimeTV;//发布日期
    @Bind(R.id.tv_watched_detail_user_strategy)TextView readCountTV;//阅读数
    @Bind(R.id.tv_zan_detail_user_strategy)TextView zanClickCountTV;//点赞数
    @Bind(R.id.web_view_detail_user_strategy)WebView webViewWV;//攻略详情显示

    @Bind(R.id.iv_header_cover_detail_user_strategy)ImageView coverImage;//封面
    @Bind(R.id.cciv_user_avatar_detail_user_strategy)ImageView avatarImage;//头像



    @Bind(R.id.toolbar_user_strategy_detail)Toolbar mToolBar;//工具条
    @Bind(R.id.collapsing_toolbar_user_strategy_detail)CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_strategy_detail;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initViews(Bundle savedInstanceState) {
        format=new SimpleDateFormat("yyyy-MM-dd");
        mReference=new WeakReference<>(this);
        Intent intent=getIntent();
        currentStrategy= (UserStrategy) intent.getSerializableExtra("user_strategy");
       // titleTV.setText(currentStrategy.getUstrategyname());
        authorNameTV.setText(currentStrategy.getTripUser().getUsername());
        publishTimeTV.setText(format.format(currentStrategy.getUstrategydate()));
        readCountTV.setText(String.valueOf(currentStrategy.getUclickednum()));
        zanClickCountTV.setText(String.valueOf(currentStrategy.getUlikecount()));
        String htmlContent=currentStrategy.getUstrategycontent();
        Document document= Jsoup.parse(htmlContent);
        Elements images=document.select("img[src]");
        for(Element element:images){
            String imageURL=element.attr("src");
            imageURL= RequestURLs.MAIN_URL+imageURL;
            element.attr("src",imageURL);
        }
        htmlContent=document.toString();
        WebSettings settings=webViewWV.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);// 设置启动缓存
        settings.setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//适应内容大小
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        htmlContent= DomUtils.getHtmlData(htmlContent);
        webViewWV.loadData(htmlContent,"text/html; charset=utf-8","utf-8");
        isNetworkAvailable=CommonUtils.isNetworkAvailable(this);
        if(isNetworkAvailable){
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.feedback_cartoon)
                    .showImageOnFail(R.drawable.feedback_cartoon)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader imageLoader=ImageLoader.getInstance();
            imageLoader.displayImage(RequestURLs.MAIN_URL+currentStrategy.getCoverImage(),coverImage,options);
            //初始化缓存对象
            mMemoryCache=new LruCache<String,Bitmap>(cacheSize){
                @Override
                protected int sizeOf(String key,Bitmap bitmap) {
                    return bitmap.getByteCount()/1024;
                }
            };
        okHttpUtils=new OkHttpUtils();
        //读取头像
        if(getBitmapFromMemCache("avatar_bitmap")!=null){
            avatarImage.setImageBitmap(getBitmapFromMemCache("avatar_bitmap"));
        }else{//去网络下载
            okHttpUtils.startAsyncGetRequest(RequestURLs.MAIN_URL+currentStrategy.getTripUser().getHeaderimage(),new UserAvatarBinaryResponseCallback());
        }
        }else{ToastUtils.ShortToast(R.string.no_network_connection);}
    }

    @Override
    public void initToolBar() {
        mToolBar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolBar.setNavigationOnClickListener(v -> finish());
        mCollapsingToolbarLayout.setTitle(currentStrategy.getUstrategyname());

    }

    /**
     * 读取用户头像回调函数
     */
    private class UserAvatarBinaryResponseCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {ToastUtils.ShortToast(R.string.server_down_text);}
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                final byte[] bytes=response.body().bytes();
                runOnUiThread(() -> {
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    addBitmapToMemoryCache("avatar_bitmap",bitmap);
                    avatarImage.setImageBitmap(bitmap);
                });
            }

        }
    }

    /**
     * 将位图对象放入缓存
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 取出缓存对象
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    private synchronized void removeImageCache(String key) {
        if (key != null) {
            if (mMemoryCache != null) {
                Bitmap bm = mMemoryCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }


}
