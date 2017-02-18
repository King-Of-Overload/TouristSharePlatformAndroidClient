package zjut.salu.share.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.adapter.user.OffLineDownloadAdapter;
import zjut.salu.share.base.RxBaseActivity;

/**
 * 离线下载控制层
 */
public class OffLineDownloadActivity extends RxBaseActivity{
    @Bind(R.id.tab_layout)TabLayout mTabLayout;
    @Bind(R.id.viewpager)ViewPager mViewPager;
    @Bind(R.id.toolbar)Toolbar toolbar;

    private OffLineDownloadAdapter adapter;

    public static void launch(Activity activity){
        Intent intent=new Intent(activity,OffLineDownloadActivity.class);
        activity.startActivity(intent);
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_off_line_download;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        adapter=new OffLineDownloadAdapter(getSupportFragmentManager(),this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //指定tab位置
        TabLayout.Tab completeTab=mTabLayout.getTabAt(0);
        TabLayout.Tab downloadingTab=mTabLayout.getTabAt(1);
    }

    @Override
    public void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setTitle(R.string.offline_download_text);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
