package zjut.salu.share.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.adapter.user_strategy.UserStrategyFragmentPagerAdapter;
import zjut.salu.share.base.AbsBaseActivity;
import zjut.salu.share.base.AbsBaseActivityWithBar;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.MoveTouchBackUtils;

/**
 *用户攻略游记主界面
 */
public class UserStrategyActivity extends RxBaseActivity {
    @Bind(R.id.tab_layout_user_strategy)TabLayout mTabLayout;
    @Bind(R.id.viewpager_user_strategy)ViewPager mViewPager;
    @Bind(R.id.tv_top_bar_title)TextView title;
    @Bind(R.id.iv_btn_top_back)ImageButton button;
    private WeakReference<Activity> mReference;
    private UserStrategyFragmentPagerAdapter pagerAdapter=null;
    private TabLayout.Tab allStrategyTab;
    private TabLayout.Tab boutiqueStrategyTab;
    private TabLayout.Tab highQualityStrategyTab;
    @Override
    public int getLayoutId() {
        return R.layout.activity_user_strategy;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        title.setText(getText(R.string.main_user_strategy_text));
        MoveTouchBackUtils utils=new MoveTouchBackUtils(button,mReference.get());
        utils.bindClickBackListener();
        pagerAdapter=new UserStrategyFragmentPagerAdapter(getSupportFragmentManager(),mReference.get());
        mViewPager.setAdapter(pagerAdapter);
        //绑定tablayout与viewpager
        mTabLayout.setupWithViewPager(mViewPager);
        //指定tab位置
        allStrategyTab=mTabLayout.getTabAt(0);
        boutiqueStrategyTab=mTabLayout.getTabAt(1);
        highQualityStrategyTab=mTabLayout.getTabAt(2);
    }

    @Override
    public void initToolBar() {
//        getSupportActionBar().hide();
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }



}
