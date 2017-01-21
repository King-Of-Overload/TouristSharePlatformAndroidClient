package zjut.salu.share.activity.local;

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
 * 推荐路线详细内容逻辑
 */
public class RecommendRouteDetailActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar mToolbar;
    @Bind(R.id.common_web_view)WebView mWebView;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed)ImageView loadingFailedIV;

    private String title="";
    @Override
    public int getLayoutId() {
        return R.layout.view_common_web_view;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        progressView.spin();
        Intent intent=getIntent();
        Routes route= (Routes) intent.getExtras().getSerializable("route");
        if(null!=route){
            title=route.getRoutename();
            String htmlContent=route.getRoutecontent();
            Boolean isNetWorkAvailable= CommonUtils.isNetworkAvailable(new WeakReference<>(this).get());
            if(isNetWorkAvailable){
                WebViewUtils.displayHTMLContentWithProgressBar(htmlContent,mWebView,progressView);
            }else{
                ToastUtils.ShortToast(R.string.no_network_connection);
                loadingFailedIV.setVisibility(View.VISIBLE);
                progressView.stopSpinning();
                progressView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(title);
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolbar.setNavigationOnClickListener(v->finish());
    }
}
