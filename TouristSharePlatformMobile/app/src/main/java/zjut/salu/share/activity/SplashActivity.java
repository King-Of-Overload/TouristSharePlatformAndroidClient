package zjut.salu.share.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.widget.ImageView;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.utils.PreferenceUtils;

/**
 * 芳草寻源欢迎页
 * @Author さる
 */
public class SplashActivity extends Activity {
    ImageView mSplashImage;//启动背景imageView
    private static final int[] SPLASH_PIC=new int[]{R.drawable.splash_default};
    //动画执行时间
    private static final int ANIMATION_DURATION=3000;
    //缩放动画的结束值
    private static final float SCALE_END=1.2F;

    private Handler mHandler;
    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mSplashImage= (ImageView) this.findViewById(R.id.splash_iv);
        mHandler=new MyHandler();
        mSplashImage.setBackgroundResource(SPLASH_PIC[0]);
    }



    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences=PreferenceUtils.getPreferences();
        Boolean loginStatus=preferences.getBoolean("loginStatus",false);
        if(loginStatus){
            mHandler.sendEmptyMessageDelayed(GO_HOME,1000);
        }else{
            mHandler.sendEmptyMessageDelayed(GO_LOGIN,1000);
        }
    }


    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GO_LOGIN:{
                    animateImage(1);
                    break;
                }
                case GO_HOME:{
                    animateImage(0);
                }
            }
        }
    }

    /**
     * 执行初始动画
     * @param flag  0去主页  1去登录
     */
    private void animateImage(final int flag){
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mSplashImage, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mSplashImage, "scaleY", 1f, SCALE_END);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_DURATION).play(animatorX).with(animatorY);
        set.addListener(new MyAnimationListener(flag));
        set.start();
    }
    /**
     * 动画执行的监听器
     */
    public class MyAnimationListener extends AnimatorListenerAdapter {
        private int flag=0;
        MyAnimationListener(int flag){
            this.flag=flag;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if (flag == 0) {//已登录
                //跳转到主界面
                Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
               Intent intent= new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("activity_name",SplashActivity.class.getName());
                startActivity(intent);
                finish();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
