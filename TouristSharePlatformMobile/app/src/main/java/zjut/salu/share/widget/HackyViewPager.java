package zjut.salu.share.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**viewpager放大组件
 * Created by Salu on 2016/11/14.
 */

public class HackyViewPager extends ViewPager {
    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        }  catch (IllegalArgumentException e) {
            //not deal with
            return false;
        }catch(ArrayIndexOutOfBoundsException e ){
            //not deal with
            return false;
        }
    }
}
