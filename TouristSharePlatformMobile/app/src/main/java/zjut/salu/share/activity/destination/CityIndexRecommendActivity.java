package zjut.salu.share.activity.destination;

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
import zjut.salu.share.model.CityIndexRecommend;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.utils.WebViewUtils;
import zjut.salu.share.widget.CircleProgressView;

/**
 * 城市主页推荐数据controller
 */
public class CityIndexRecommendActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar mToolbar;
    @Bind(R.id.common_web_view)WebView mWebView;
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.iv_loading_failed)ImageView loadingFailedIV;

    private String title;
    @Override
    public int getLayoutId() {
        return R.layout.view_common_web_view;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        progressView.spin();
        Intent intent=getIntent();
        CityIndexRecommend recommend= (CityIndexRecommend) intent.getSerializableExtra("recommend");
        String htmlContent=recommend.getCityindexcontent();
        title=recommend.getCityindexname();
        Boolean isNetworkAvailable= CommonUtils.isNetworkAvailable(new WeakReference<>(this).get());
        if(isNetworkAvailable){
            WebViewUtils.displayHTMLContentWithProgressBar(htmlContent,mWebView,progressView);
        }else{
            ToastUtils.ShortToast(R.string.no_network_connection);
            loadingFailedIV.setVisibility(View.VISIBLE);
            progressView.stopSpinning();
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(title);
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolbar.setNavigationOnClickListener(v->finish());
    }
}
