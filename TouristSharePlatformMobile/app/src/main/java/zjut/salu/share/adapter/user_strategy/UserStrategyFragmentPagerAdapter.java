package zjut.salu.share.adapter.user_strategy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import zjut.salu.share.R;
import zjut.salu.share.fragment.user_strategy_fragment.AllUserStrategyFragment;
import zjut.salu.share.fragment.user_strategy_fragment.BoutiqueUserStrategyFragment;
import zjut.salu.share.fragment.user_strategy_fragment.HighQualityUserStrategyFragment;

/**攻略游记的fragment与viewpager之间的适配器
 * Created by Alan on 2016/10/28.
 */

public class UserStrategyFragmentPagerAdapter extends FragmentPagerAdapter{
    private Context context=null;
    private String[] userStrategyTitles=null;
    public UserStrategyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context=context;
        userStrategyTitles=new String[]{context.getString(R.string.all_user_strategy_text),context.getString(R.string.nice_user_no_strategy_text),
        context.getString(R.string.good_quantity_strategy_text)};
    }

    @Override
    public Fragment getItem(int position) {
        if(position==1){//精品用户
            return new BoutiqueUserStrategyFragment();
        }else if(position==2){//优质精华
            return new HighQualityUserStrategyFragment();
        }
        return new AllUserStrategyFragment();
    }

    @Override
    public int getCount() {
        return userStrategyTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return userStrategyTitles[position];
    }

}
