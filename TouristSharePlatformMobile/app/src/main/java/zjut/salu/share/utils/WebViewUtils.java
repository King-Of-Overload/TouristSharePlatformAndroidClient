package zjut.salu.share.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import zjut.salu.share.widget.CircleProgressView;

/**webview工具
 * Created by Salu on 2016/11/14.
 */

public class WebViewUtils {

    @SuppressLint("SetJavaScriptEnabled")
    public static void displayHTMLContent(String htmlContent, WebView webView){
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//适应内容大小
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        htmlContent= DomUtils.getHtmlData(htmlContent);
        webView.loadData(htmlContent,"text/html; charset=utf-8","utf-8");
    }

    /**
     * 显示web_view内容，带有进度圈
     * @param htmlContent html body
     * @param webView object
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void displayHTMLContentWithProgressBar(String htmlContent, WebView webView, CircleProgressView progressView){
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
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void displayHTMLWithProgressBar(String url, WebView webView, CircleProgressView progressView){
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
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view,String url)
            {
                progressView.stopSpinning();
                progressView.setVisibility(View.INVISIBLE);
            }
        });
    }
}
