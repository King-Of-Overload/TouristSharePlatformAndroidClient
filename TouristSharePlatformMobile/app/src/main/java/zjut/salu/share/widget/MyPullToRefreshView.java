package zjut.salu.share.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import zjut.salu.share.activity.UserInfoActivity;

/**
 * Created by Salu on 2016/11/16.
 */
public class MyPullToRefreshView extends PullToRefreshListView{
    public MyPullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) { return; }
        // TODO Auto-generated constructor stub
    }

    public MyPullToRefreshView(
            Context context,
            com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode,
            com.handmark.pulltorefresh.library.PullToRefreshBase.AnimationStyle style) {
        super(context, mode, style);

        // TODO Auto-generated constructor stub
    }

    public MyPullToRefreshView(Context context,
                                com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode) {
        super(context, mode);
        // TODO Auto-generated constructor stub
    }

    public MyPullToRefreshView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(onPlvScrollListener != null) {
            onPlvScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    private OnPlvScrollListener onPlvScrollListener;

    public void setOnPlvScrollListener(OnPlvScrollListener onPlvScrollListener) {
        this.onPlvScrollListener = onPlvScrollListener;
    }

    public static interface OnPlvScrollListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
