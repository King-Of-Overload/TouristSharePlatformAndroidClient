package zjut.salu.share.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.adapter.best.BestChooseFragmentPagerAdapter;

import static zjut.salu.share.R.id.toolbar;

/**精选fragment
 * Created by Salu on 2016/12/10.
 */

public class BestFragment extends RxLazyFragment {
    @Bind(R.id.tab_layout_best)TabLayout mTabLayout;
    @Bind(R.id.viewpager_best)ViewPager mViewPager;
    private BestChooseFragmentPagerAdapter pagerAdapter=null;
    private TabLayout.Tab bestChooseTab;
    private TabLayout.Tab bestStrategyTab;
    private Context context;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_best;
    }

    @Override
    public void finishCreateView(Bundle state) {
        context=getActivity();
        setHasOptionsMenu(true);
        pagerAdapter=new BestChooseFragmentPagerAdapter(getActivity().getSupportFragmentManager(),context);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //指定tab位置
        bestChooseTab=mTabLayout.getTabAt(0);
        bestStrategyTab=mTabLayout.getTabAt(1);
    }




}
