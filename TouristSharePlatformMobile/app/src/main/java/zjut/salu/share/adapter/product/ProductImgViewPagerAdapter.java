package zjut.salu.share.adapter.product;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**产品展示viewpager适配器
 * Created by Salu on 2016/11/14.
 */

public class ProductImgViewPagerAdapter extends PagerAdapter {
    private List<View> images=null;

    public ProductImgViewPagerAdapter(List<View> images) {
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (View) arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = images.get(position);
        container.addView(view);
        return view;
    }
}
