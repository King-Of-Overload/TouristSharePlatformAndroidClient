package zjut.salu.share.activity.banggumi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import retrofit2.http.PATCH;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.AppBarStateChangeEvent;
import zjut.salu.share.fragment.banggumi.BanggumeIndroductionFragment;
import zjut.salu.share.fragment.banggumi.BanggumiCommentFragment;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DeviceUtils;
import zjut.salu.share.utils.DisplayUtil;
import zjut.salu.share.utils.ImageUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.SystemBarHelper;

/**
 * 小视频详情显示页面
 */
public class BanggumiDetailActivity extends RxBaseActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.video_preview) ImageView mVideoPreview;

    @Bind(R.id.tab_layout) SlidingTabLayout mSlidingTabLayout;

    @Bind(R.id.view_pager) ViewPager mViewPager;

    @Bind(R.id.fab) FloatingActionButton mFAB;

    @Bind(R.id.app_bar_layout) AppBarLayout mAppBarLayout;

    @Bind(R.id.tv_player) TextView mTvPlayer;

    @Bind(R.id.tv_av) TextView mAvText;

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    //private String av;//标题
    private String imgUrl;
    private Banggume banggume;
    private OkHttpUtils okHttpUtils;

    public static void launch(Activity activity, Banggume banggume){
        Intent mIntent = new Intent(activity, BanggumiDetailActivity.class);
        mIntent.putExtra("banggume",banggume);
        activity.startActivity(mIntent);
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_banggumi_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        okHttpUtils=new OkHttpUtils();
        Intent intent=getIntent();
        banggume= (Banggume) intent.getSerializableExtra("banggume");
         Glide.with(BanggumiDetailActivity.this)//加载封面
                .load(RequestURLs.MAIN_URL+banggume.getBangumecover())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.ico_user_default)
                .dontAnimate()
                .into(mVideoPreview);
        increaseBanggumeClickNum();//更新点击量
        mFAB.setClickable(false);
        mFAB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_20)));
        mFAB.setTranslationY(-getResources().getDimension(R.dimen.floating_action_button_size_half));
        mFAB.setOnClickListener(v -> BanggumiPlayerActivity.launch(BanggumiDetailActivity.this,
                banggume.getBangumeid(), banggume.getBangumename(),banggume));
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> setViewsTranslation(verticalOffset));
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeEvent()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset)
            {

                if (state == State.EXPANDED)
                {
                    //展开状态
                    mTvPlayer.setVisibility(View.GONE);
                    mAvText.setVisibility(View.VISIBLE);
                    mToolbar.setContentInsetsRelative(DisplayUtil.dp2px(BanggumiDetailActivity.this, 15), 0);
                } else if (state == State.COLLAPSED)
                {
                    //折叠状态
                    mTvPlayer.setVisibility(View.VISIBLE);
                    mAvText.setVisibility(View.GONE);
                    mToolbar.setContentInsetsRelative(DisplayUtil.dp2px(BanggumiDetailActivity.this, 150), 0);
                } else
                {
                    mTvPlayer.setVisibility(View.GONE);
                    mAvText.setVisibility(View.VISIBLE);
                    mToolbar.setContentInsetsRelative(DisplayUtil.dp2px(BanggumiDetailActivity.this, 15), 0);
                }
            }
        });
        finishTask();
    }

    private void increaseBanggumeClickNum(){//更新点击量
        Map<String,Object> params=new HashMap<>();
        params.put("banggumeId",banggume.getBangumeid());
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.INCREASE_BANGGUME, params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onNext(String s) {}
                });
    }

    @Override
    public void finishTask() {
        mFAB.setClickable(true);
        mFAB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        mCollapsingToolbarLayout.setTitle("");
        BanggumeIndroductionFragment indroductionFragment=BanggumeIndroductionFragment.newInstance(banggume.getBangumename(),banggume);
        fragments.add(indroductionFragment);
        BanggumiCommentFragment commentFragment=BanggumiCommentFragment.newInstance(banggume.getBangumeid());
        fragments.add(commentFragment);
        setPageTitle("6666");//初始化fragment侧滑标题，评论数
    }

    /**
     * 设置fragment
     * @param commentNum 评论个数
     */
    private void setPageTitle(String commentNum){
        titles.add(getString(R.string.bref_text));
        titles.add(getString(R.string.comment_text) + "(" + commentNum + ")");
        BanggumiDetailsPagerAdapter adapter=new BanggumiDetailsPagerAdapter(getSupportFragmentManager(),fragments,titles);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout.setViewPager(mViewPager);
        measureTabLayoutTextWidth(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position)
            {
                measureTabLayoutTextWidth(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void measureTabLayoutTextWidth(int position)
    {

        String title = titles.get(position);
        TextView titleView = mSlidingTabLayout.getTitleView(position);
        TextPaint paint = titleView.getPaint();
        float textWidth = paint.measureText(title);
        mSlidingTabLayout.setIndicatorWidth(textWidth / 3);
    }

    private void setViewsTranslation(int target)
    {

        mFAB.setTranslationY(target);
        if (target == 0)
        {
            showFAB();
        } else if (target < 0)
        {
            hideFAB();
        }
    }

    /**
     * 显示按钮
     */
    private void showFAB()
    {

        mFAB.animate().scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .start();

        mFAB.setClickable(true);
    }

    /**
     * 隐藏按钮
     */
    private void hideFAB()
    {

        mFAB.animate().scaleX(0f).scaleY(0f)
                .setInterpolator(new AccelerateInterpolator())
                .start();

        mFAB.setClickable(false);
    }

    @Override
    public void initToolBar() {
        //设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        //设置收缩后Toolbar上字体的颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        //设置StatusBar透明
        SystemBarHelper.immersiveStatusBar(this);
        SystemBarHelper.setHeightAndPadding(this, mToolbar);
        mAvText.setText(banggume.getBangumename());
        mToolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolbar.setNavigationOnClickListener(v->finish());
    }

    /**
     * fragment内部类
     */
    public static class BanggumiDetailsPagerAdapter extends FragmentStatePagerAdapter
    {

        private List<Fragment> fragments;

        private List<String> titles;

        BanggumiDetailsPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles)
        {

            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position)
        {

            return fragments.get(position);
        }

        @Override
        public int getCount()
        {

            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {

            return titles.get(position);
        }
    }
}
