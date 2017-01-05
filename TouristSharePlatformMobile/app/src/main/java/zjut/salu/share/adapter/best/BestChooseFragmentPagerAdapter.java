package zjut.salu.share.adapter.best;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import zjut.salu.share.R;
import zjut.salu.share.fragment.best.BestChooseChildFragment;
import zjut.salu.share.fragment.best.BestStrategyChildFragment;

/**精选选项卡适配器
 * Created by Salu on 2016/12/13.
 */

public class BestChooseFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context=null;
    private String[] bestTitles=null;
    public BestChooseFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
        bestTitles=new String[]{context.getString(R.string.special_section_text),context.getString(R.string.best_strategy_text)};
    }

    @Override
    public Fragment getItem(int position) {
        if(position==1){//精选游记
            return new BestStrategyChildFragment();
        }
        return new BestChooseChildFragment();
    }

    @Override
    public int getCount() {
        return bestTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return bestTitles[position];
    }
}
