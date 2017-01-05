package zjut.salu.share.activity;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.adapter.friends.FriendsFragmentPagerAdapter;
import zjut.salu.share.base.RxBaseActivity;

public class FriendsActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar mToolbar;
    @Bind(R.id.tab_layout_friends_list)TabLayout mTabLayout;
    @Bind(R.id.viewpager_friends_list)ViewPager mViewPager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_friends;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        WeakReference<Activity> mReference = new WeakReference<>(this);
        FriendsFragmentPagerAdapter pagerAdapter = new FriendsFragmentPagerAdapter(getSupportFragmentManager(), mReference.get());
        mViewPager.setAdapter(pagerAdapter);
        //绑定tab_layout与viewpager
        mTabLayout.setupWithViewPager(mViewPager);
        //指定tab位置
        TabLayout.Tab focusTab = mTabLayout.getTabAt(0);
        TabLayout.Tab followTab = mTabLayout.getTabAt(1);
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(R.string.focus_text);
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolbar.setNavigationOnClickListener(v->finish());
    }
}
