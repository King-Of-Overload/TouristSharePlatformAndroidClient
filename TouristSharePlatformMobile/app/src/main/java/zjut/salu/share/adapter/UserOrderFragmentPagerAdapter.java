package zjut.salu.share.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import zjut.salu.share.R;
import zjut.salu.share.fragment.user_order_list_fragment.UserPayedOrderFragment;
import zjut.salu.share.fragment.user_order_list_fragment.UserUnpayedOrderFragment;

/**我的订单的fragment与viewpager之间的适配器
 * Created by Alan on 2016/10/27.
 */

public class UserOrderFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context=null;
    private String[] orderTitles=null;
    public UserOrderFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
        orderTitles=new String[]{context.getString(R.string.order_has_payed_text),context.getString(R.string.order_not_payed_text)};
    }

    @Override
    public Fragment getItem(int position) {
        if(position==1){
            return new UserUnpayedOrderFragment();
        }
        return new UserPayedOrderFragment();
    }

    @Override
    public int getCount() {
        return orderTitles.length;
    }

    /**
     * viewpager与TabLayout绑定后，这里获取到pageTitle就是tab的text
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return orderTitles[position];
    }
}
