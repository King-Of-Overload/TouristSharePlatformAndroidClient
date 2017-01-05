package zjut.salu.share.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RadialGradient;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import butterknife.OnTouch;
import zjut.salu.share.R;

/**返回转场动画工具类
 * Created by Alan on 2016/10/20.
 */

public class MoveTouchBackUtils{
    private View view;
    //手指向右滑动时的最小速度
    private static final int XSPEED_MIN = 200;

    //手指向右滑动时的最小距离
    private static final int XDISTANCE_MIN = 150;

    //记录手指按下时的横坐标。
    private float xDown;

    //记录手指移动时的横坐标。
    private float xMove;

    //用于计算手指滑动的速度。
    private VelocityTracker mVelocityTracker;
    private Activity currentContext;

    public MoveTouchBackUtils(View v,Activity currentContext){
        this.view=v;
        this.currentContext=currentContext;
    }

   public void bindClickBackListener(){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentContext.finish();
                //设置切换动画，从左边进入，右边退出
                currentContext.overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
            }
        });
    }

    public void bindOnTouchBackListener(int layoutId){
        View v=currentContext.findViewById(layoutId);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                createVelocityTracker(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDown = event.getRawX();
                        Log.i("dsds","dsdsdsdsdsdsdsds");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xMove = event.getRawX();
                        //活动的距离
                        int distanceX = (int) (xMove - xDown);
                        //获取顺时速度
                        int xSpeed = getScrollVelocity();
                        //当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
                        if(distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
                            currentContext.finish();
                            currentContext.overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        recycleVelocityTracker();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event
     *
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }


}
