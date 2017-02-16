package zjut.salu.share.activity.common;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.Routes;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.utils.WebViewUtils;
import zjut.salu.share.widget.CircleProgressView;

/**
 * 通用webview控制层
 * @author Alan-Mac
 */
public class CommonWebActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar mToolbar;
    @Bind(R.id.common_web_view)WebView mWebView;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed)ImageView loadingFailedIV;

    @Override
    public int getLayoutId() {
        return R.layout.view_common_web_view;
    }

    public static void launch(Activity activity, String url){
        Intent mIntent = new Intent(activity, CommonWebActivity.class);
        mIntent.putExtra("url",url);
        activity.startActivity(mIntent);
    }
    @Override
    public void initViews(Bundle savedInstanceState) {
        progressView.spin();
        Intent intent=getIntent();
        String url=intent.getStringExtra("url");
            Boolean isNetWorkAvailable= CommonUtils.isNetworkAvailable(new WeakReference<>(this).get());
            if(isNetWorkAvailable){
                WebViewUtils.displayHTMLWithProgressBar(url,mWebView,progressView);
            }else{
                ToastUtils.ShortToast(R.string.no_network_connection);
                loadingFailedIV.setVisibility(View.VISIBLE);
                progressView.stopSpinning();
                progressView.setVisibility(View.INVISIBLE);
            }
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(getString(R.string.web_detail_text));
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolbar.setNavigationOnClickListener(v->finish());
    }
}
