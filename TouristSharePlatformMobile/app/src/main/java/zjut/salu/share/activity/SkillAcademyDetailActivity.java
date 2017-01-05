package zjut.salu.share.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomBase;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.base.AbsBaseActivityWithBar;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.SkillAcademy;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DeviceUtils;
import zjut.salu.share.utils.DomUtils;
import zjut.salu.share.utils.MoveTouchBackUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CommonCircleImageView;

/**
 * 技法学院详细信息界面
 */
public class SkillAcademyDetailActivity extends RxBaseActivity {
    private int imageHeight=0;
    private int orginalHeight=0;
    @Bind(R.id.pull_scroll_view_skill_academy_detail)PullToZoomScrollViewEx scrollViewEx;
    @Bind(R.id.cciv_avatar_skill_academy_detail)CommonCircleImageView avatarCCIV;//头像
    @Bind(R.id.tv_title_name_skill_academy_detail)TextView titleTV;//标题
    @Bind(R.id.tv_time_skill_academy_detail)TextView dateTV;//日期
    @Bind(R.id.tv_watched_skill_academy_detail)TextView clickedNumTV;//点击数
    @Bind(R.id.tv_essence_skill_academy_detail)TextView isEssenceTV;//是否精华
    @Bind(R.id.tv_big_title_skill_academy_detail)TextView bigTitleTV;//大标题
    @Bind(R.id.web_view_skill_academy_detail)WebView webView;
    @Bind(R.id.iv_zoom_view_skill_academy_detail)ImageView coverIV;//封面
    @Bind(R.id.linear_layout_view_top_bar_width)LinearLayout topBar;

    @Bind(R.id.tv_top_bar_title_width)TextView title;
    @Bind(R.id.iv_btn_top_back_width)ImageButton button;
    @Override
    public int getLayoutId() {
        return R.layout.activity_skill_academy_detail;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initViews(Bundle savedInstanceState) {
        WeakReference<Activity> mReference = new WeakReference<>(this);
        setPullToZoomViewLayoutParams(scrollViewEx);
        MoveTouchBackUtils utils=new MoveTouchBackUtils(button, mReference.get());
        utils.bindClickBackListener();
        Intent intent=getIntent();
        SkillAcademy currentSkillAcademy = (SkillAcademy) intent.getSerializableExtra("skill_academy");
        title.setText(currentSkillAcademy.getSkilltitle());
        titleTV.setText(currentSkillAcademy.getSkilltitle());
        dateTV.setText(StringUtils.formatDate(currentSkillAcademy.getSkilldate(),"yyyy-MM-dd"));
        clickedNumTV.setText(String.valueOf(currentSkillAcademy.getClickednum()));
        if(currentSkillAcademy.getIsessence()==0){
            isEssenceTV.setVisibility(View.VISIBLE);
        }else{
            isEssenceTV.setVisibility(View.GONE);
        }
        bigTitleTV.setText(currentSkillAcademy.getSkilltitle());
        String htmlContent= currentSkillAcademy.getSkillcontent();
        Document document= Jsoup.parse(htmlContent);
        Elements images=document.select("img[src]");
        for (Element element:images){
            String imageURL=element.attr("src");
            imageURL= RequestURLs.MAIN_URL+imageURL;
            element.attr("src",imageURL);
        }
        htmlContent=document.toString();
        WebSettings settings=webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//适应内容大小
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        htmlContent= DomUtils.getHtmlData(htmlContent);
        webView.loadData(htmlContent,"text/html; charset=utf-8","utf-8");
        Boolean isNetworkAlive = CommonUtils.isNetworkAvailable(this);
        if(isNetworkAlive){
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.feedback_cartoon)
                    .showImageOnFail(R.drawable.feedback_cartoon)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader imageLoader=ImageLoader.getInstance();
            imageLoader.displayImage(RequestURLs.MAIN_URL+ currentSkillAcademy.getCoverImage(),coverIV,options);
            imageLoader.displayImage(RequestURLs.MAIN_URL+ currentSkillAcademy.getUser().getHeaderimage(),avatarCCIV,options);
        }else{
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }




    /**
     * 设置头部的View的宽高。
     * @param scrollView myscroll
     */
    private void setPullToZoomViewLayoutParams(PullToZoomScrollViewEx scrollView) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth,
                (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);
    }


    @Override
    public void initToolBar() {

    }
}
