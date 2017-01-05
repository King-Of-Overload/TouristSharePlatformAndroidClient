package zjut.salu.share.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.adapter.UserOrderFragmentPagerAdapter;
import zjut.salu.share.base.AbsBaseActivity;
import zjut.salu.share.base.AbsBaseActivityWithBar;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.MoveTouchBackUtils;

/**
 * 我的订单界面
 */
public class UserOrderListActivity extends RxBaseActivity {
    @Bind(R.id.tab_layout_order_list)TabLayout mTabLayout;
    @Bind(R.id.viewpager_order_list)ViewPager mViewPager;
    @Bind(R.id.tv_top_bar_title)TextView title;
    @Bind(R.id.iv_btn_top_back)ImageButton button;
    private UserOrderFragmentPagerAdapter pagerAdapter;
    private WeakReference<Activity> mReference;

    private TabLayout.Tab payedTab;
    private TabLayout.Tab un_payedTab;
    @Override
    public int getLayoutId() {
        return R.layout.activity_user_order_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        title.setText(getText(R.string.fragment_setting_user_order_text));
        MoveTouchBackUtils utils=new MoveTouchBackUtils(button,mReference.get());
        utils.bindClickBackListener();
        pagerAdapter=new UserOrderFragmentPagerAdapter(getSupportFragmentManager(),mReference.get());
        mViewPager.setAdapter(pagerAdapter);
        //绑定tablayout与viewpager
        mTabLayout.setupWithViewPager(mViewPager);
        //指定tab的位置
        payedTab=mTabLayout.getTabAt(0);
        un_payedTab=mTabLayout.getTabAt(1);

    }

    @Override
    public void initToolBar() {

    }
}
