package zjut.salu.share.activity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.BestChoose;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DeviceUtils;
import zjut.salu.share.utils.DomUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;

public class BestTopicActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar mToolbar;
    @Bind(R.id.web_view_best_topic)WebView webView;
    @Bind(R.id.iv_loading_failed)ImageView loadingFailedIV;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    private BestChoose bestChoose;

    @Override
    public int getLayoutId() {
        return R.layout.activity_best_topic;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initViews(Bundle savedInstanceState) {
        progressView.spin();
        WeakReference<Activity> mReference = new WeakReference<>(this);
        Intent intent=getIntent();
        bestChoose= (BestChoose) intent.getSerializableExtra("best_topic");
        Boolean isNetworkAvailable= CommonUtils.isNetworkAvailable(mReference.get());
        if(isNetworkAvailable){
            String htmlContent=bestChoose.getBestcontent();
            Document document= Jsoup.parse(htmlContent);
            Elements images=document.select("img[src]");
            for(Element element:images){
                String imageURL=element.attr("src");
                imageURL= RequestURLs.MAIN_URL+imageURL;
                element.attr("src",imageURL);
            }
            htmlContent=document.toString();
            WebSettings settings=webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setAppCacheEnabled(true);// 设置启动缓存
            settings.setDefaultTextEncodingName("utf-8");
            if (DeviceUtils.hasKitKat()) {
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//适应内容大小
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            } else {
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            }

                webView.setWebViewClient(new WebViewClient()
                {
                    @Override
                    public void onPageFinished(WebView view,String url)
                    {
                        progressView.stopSpinning();
                        progressView.setVisibility(View.INVISIBLE);
                    }
                });
            htmlContent= DomUtils.getHtmlData(htmlContent);
            webView.loadData(htmlContent,"text/html;charset=utf-8","utf-8");

        }else{
            ToastUtils.ShortToast(R.string.no_network_connection);
            loadingFailedIV.setVisibility(View.VISIBLE);
            progressView.stopSpinning();
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(bestChoose.getBestname());
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolbar.setNavigationOnClickListener(v->finish());
    }
}
