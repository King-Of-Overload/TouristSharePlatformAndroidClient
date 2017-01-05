package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.base.AbsBaseActivityWithBar;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.fragment.product.BigPictureFragment;
import zjut.salu.share.widget.HackyViewPager;

public class UserInfoBigImageActivity extends RxBaseActivity {
    private ArrayList<String> nineImgRes=new ArrayList<>();
    private int currentPosition=0;

    @Bind(R.id.viewPager_show_bigPic)HackyViewPager hackyViewPager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_info_big_image;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        nineImgRes=bundle.getStringArrayList("nineImgRes");
        currentPosition=bundle.getInt("position");
        initViewPager();
    }

    private void initViewPager(){
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        hackyViewPager.setAdapter(adapter);
        //跳转到第几个界面
        hackyViewPager.setCurrentItem(currentPosition);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String imgStr=nineImgRes.get(position);
            return new BigPictureFragment(imgStr);
        }

        @Override
        public int getCount() {
            return nineImgRes.size();
        }


    }

    @Override
    public void initToolBar() {

    }
}
