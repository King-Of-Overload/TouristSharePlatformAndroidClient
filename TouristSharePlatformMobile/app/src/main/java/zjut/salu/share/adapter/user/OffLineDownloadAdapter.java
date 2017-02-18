package zjut.salu.share.adapter.user;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import zjut.salu.share.R;
import zjut.salu.share.fragment.user.download.OffLineDownloadCompleteFragment;
import zjut.salu.share.fragment.user.download.OffLineDownloadOnFragment;

/**离线下载选项卡适配器
 * Created by Salu on 2017/2/17.
 */

public class OffLineDownloadAdapter  extends FragmentPagerAdapter{
    private Context context=null;
    private String[] downloadTitles=null;

    public OffLineDownloadAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
        downloadTitles=new String[]{context.getString(R.string.download_finish_text),context.getString(R.string.downloading_text)};
    }

    @Override
    public Fragment getItem(int position) {
        if(position==1){
            return new OffLineDownloadOnFragment();
        }
        return new OffLineDownloadCompleteFragment();
    }

    @Override
    public int getCount() {
        return downloadTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return downloadTitles[position];
    }
}
