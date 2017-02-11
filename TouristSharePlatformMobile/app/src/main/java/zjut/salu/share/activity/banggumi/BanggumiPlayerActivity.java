package zjut.salu.share.activity.banggumi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import butterknife.Bind;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.myinterface.DanmukuSwitchListener;
import zjut.salu.share.myinterface.VideoBackListener;
import zjut.salu.share.utils.ConstantUtil;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.widget.MediaController;
import zjut.salu.share.widget.VideoPlayerView;

/**
 * 视频播放器业务逻辑
 */
public class BanggumiPlayerActivity extends RxBaseActivity implements DanmukuSwitchListener, VideoBackListener {
    @Bind(R.id.sv_danmaku) IDanmakuView mDanmakuView;
    @Bind(R.id.playerView) VideoPlayerView mPlayerView;

    @Bind(R.id.buffering_indicator) View mBufferingIndicator;

    @Bind(R.id.video_start) RelativeLayout mVideoPrepareLayout;

    @Bind(R.id.bili_anim) ImageView mAnimImageView;

    @Bind(R.id.video_start_info) TextView mPrepareText;

    private AnimationDrawable mLoadingAnim;

    private DanmakuContext danmakuContext;

    private String cid;

    private String title;

    private Banggume banggume;

    private int LastPosition = 0;

    private String startText = "初始化播放器...";

    @Override
    public int getLayoutId() {
        return R.layout.activity_banggumi_player;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null)
        {
            cid = intent.getStringExtra(ConstantUtil.BANGGUMI_ID);
            title = intent.getStringExtra(ConstantUtil.BANGGUMI_TITLE);
            banggume= (Banggume) intent.getSerializableExtra("banggume");
        }
        initAnimation();
        initMediaPlayer();
    }

    @SuppressLint("UseSparseArrays")
    private void initMediaPlayer()
    {
        //配置播放器
        MediaController mMediaController = new MediaController(this);
        mMediaController.setTitle(title);
        mPlayerView.setMediaController(mMediaController);
        mPlayerView.setMediaBufferingIndicator(mBufferingIndicator);
        mPlayerView.requestFocus();
        mPlayerView.setOnInfoListener(onInfoListener);
        mPlayerView.setOnSeekCompleteListener(onSeekCompleteListener);
        mPlayerView.setOnCompletionListener(onCompletionListener);
        mPlayerView.setOnControllerEventsListener(onControllerEventsListener);
        //设置弹幕开关监听
        mMediaController.setDanmakuSwitchListener(this);
        //设置返回键监听
        mMediaController.setVideoBackEvent(this);

        //配置弹幕库
        mDanmakuView.enableDanmakuDrawingCache(true);
        //设置最大显示行数
        HashMap<Integer,Integer> maxLinesPair = new HashMap<>();
        //滚动弹幕最大显示5行
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5);
        //设置是否禁止重叠
        HashMap<Integer,Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        //设置弹幕样式
        danmakuContext = DanmakuContext.create();
        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(0.8f)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        loadData();
    }

    /**
     * 获取视频数据以及解析弹幕
     */
    @Override
    public void loadData()
    {
        try {
            URL url=new URL(RequestURLs.MAIN_URL+banggume.getBangumeurl());
            mPlayerView.setVideoURI(Uri.parse(url.toString()));
            mPlayerView.setOnPreparedListener(mp -> {
                mLoadingAnim.stop();
                startText = startText + "【完成】\n视频缓冲中...";
                mPrepareText.setText(startText);
                mVideoPrepareLayout.setVisibility(View.GONE);
            });
            mPlayerView.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化加载动画
     */
    private void initAnimation()
    {

        mVideoPrepareLayout.setVisibility(View.VISIBLE);
        startText = startText + "【完成】\n小源正在解析视频地址...【完成】\n弹幕填装中...";
        mPrepareText.setText(startText);
        mLoadingAnim = (AnimationDrawable) mAnimImageView.getBackground();
        mLoadingAnim.start();
    }

    @Override
    public void initToolBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    /**
     * 视频缓冲事件回调
     */
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener()
    {

        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra)
        {

            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START)
            {
                if (mDanmakuView != null && mDanmakuView.isPrepared())
                {
                    mDanmakuView.pause();
                    if (mBufferingIndicator != null)
                        mBufferingIndicator.setVisibility(View.VISIBLE);
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END)
            {
                if (mDanmakuView != null && mDanmakuView.isPaused())
                {
                    mDanmakuView.resume();
                }
                if (mBufferingIndicator != null)
                    mBufferingIndicator.setVisibility(View.GONE);
            }
            return true;
        }
    };

    /**
     * 视频跳转事件回调
     */
    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener()
    {

        @Override
        public void onSeekComplete(IMediaPlayer mp)
        {

            if (mDanmakuView != null && mDanmakuView.isPrepared())
            {
                mDanmakuView.seekTo(mp.getCurrentPosition());
            }
        }
    };

    /**
     * 视频播放完成事件回调
     */
    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener()
    {

        @Override
        public void onCompletion(IMediaPlayer mp)
        {

            if (mDanmakuView != null && mDanmakuView.isPrepared())
            {
                mDanmakuView.seekTo((long) 0);
                mDanmakuView.pause();
            }
            mPlayerView.pause();
        }
    };

    /**
     * 控制条控制状态事件回调
     */
    private VideoPlayerView.OnControllerEventsListener onControllerEventsListener = new VideoPlayerView.OnControllerEventsListener()
    {

        @Override
        public void onVideoPause()
        {

            if (mDanmakuView != null && mDanmakuView.isPrepared())
            {
                mDanmakuView.pause();
            }
        }

        @Override
        public void OnVideoResume()
        {

            if (mDanmakuView != null && mDanmakuView.isPaused())
            {
                mDanmakuView.resume();
            }
        }
    };

    @Override
    protected void onResume()
    {

        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused())
        {
            mDanmakuView.seekTo((long) LastPosition);
        }
        if (mPlayerView != null && !mPlayerView.isPlaying())
        {
            mPlayerView.seekTo(LastPosition);
        }
    }

    @Override
    protected void onPause()
    {

        super.onPause();
        if (mPlayerView != null)
        {
            LastPosition = mPlayerView.getCurrentPosition();
            mPlayerView.pause();
        }

        if (mDanmakuView != null && mDanmakuView.isPrepared())
        {
            mDanmakuView.pause();
        }
    }

    @Override
    public void onBackPressed()
    {

        super.onBackPressed();
        if (mDanmakuView != null)
        {
            mDanmakuView.release();
            mDanmakuView = null;
        }
        if (mLoadingAnim != null)
        {
            mLoadingAnim.stop();
            mLoadingAnim = null;
        }
    }

    @Override
    protected void onDestroy()
    {

        super.onDestroy();
        if (mPlayerView != null && mPlayerView.isDrawingCacheEnabled())
        {
            mPlayerView.destroyDrawingCache();
        }
        if (mDanmakuView != null && mDanmakuView.isPaused())
        {
            mDanmakuView.release();
            mDanmakuView = null;
        }
        if (mLoadingAnim != null)
        {
            mLoadingAnim.stop();
            mLoadingAnim = null;
        }
    }

    /**
     * 弹幕开关回调
     *
     * @param isShow
     */
    @Override
    public void setDanmakushow(boolean isShow)
    {

        if (mDanmakuView != null)
        {
            if (isShow)
            {
                mDanmakuView.show();
            } else
            {
                mDanmakuView.hide();
            }
        }
    }

    /**
     * 退出界面回调
     */
    @Override
    public void back()
    {

        onBackPressed();
    }

    public static void launch(Activity activity, String cid, String title, Banggume banggume)
    {

        Intent mIntent = new Intent(activity, BanggumiPlayerActivity.class);
        mIntent.putExtra(ConstantUtil.BANGGUMI_ID, cid);
        mIntent.putExtra(ConstantUtil.BANGGUMI_TITLE, title);
        mIntent.putExtra("banggume",banggume);
        activity.startActivity(mIntent);
    }

}
