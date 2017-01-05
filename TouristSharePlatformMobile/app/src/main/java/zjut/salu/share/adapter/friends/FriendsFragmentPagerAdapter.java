package zjut.salu.share.adapter.friends;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import zjut.salu.share.R;
import zjut.salu.share.fragment.friends.FriendFocusFragment;
import zjut.salu.share.fragment.friends.FriendFollowFragment;

/**关注的人选项卡列表适配器
 * Created by Salu on 2016/12/8.
 */

public class FriendsFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context=null;
    private String[] friendTitles=null;
    public FriendsFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
        friendTitles=new String[]{context.getString(R.string.my_focus_text),context.getString(R.string.my_fans_text)};
    }
    @Override
    public Fragment getItem(int position) {
        if(position==1){
            return new FriendFollowFragment();
        }
        return new FriendFocusFragment();
    }

    @Override
    public int getCount() {
        return friendTitles.length;
    }

    /**
     * viewpager与TabLayout绑定后，这里获取到pageTitle就是tab的text
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return friendTitles[position];
    }
}
