package zjut.salu.share.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by Salu on 2016/12/7.
 */

public class FoldingLayout extends BaseFoldingLayout{
    private final String FOLDING_VIEW_EXCEPTION_MESSAGE = "Folding Layout can only 1 child at "
            + "most";

    private GestureDetector mScrollGestureDetector;

    FoldingLayout that = null;

    private int mTranslation = 0;
    private int mParentPositionY = -1;
    private int mTouchSlop = -1;
    private boolean mDidNotStartScroll = true;

    public FoldingLayout(Context context) {
        super(context);
        init(context, null);
        that = this;
    }

    public FoldingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        that = this;
    }

    public FoldingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        that = this;
    }

    public void init(Context context, AttributeSet attrs) {
        mScrollGestureDetector = new GestureDetector(context,
                new ScrollGestureDetector());
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setAnchorFactor(0);
        super.init(context, attrs);
    }

    @Override
    protected boolean addViewInLayout(View child, int index,
                                      LayoutParams params, boolean preventRequestLayout) {
        throwCustomException(getChildCount());
        boolean returnValue = super.addViewInLayout(child, index, params,
                preventRequestLayout);
        return returnValue;
    }

    /**
     * The custom exception to be thrown so as to limit the number of views in
     * this layout to at most one.
     */
    private class NumberOfFoldingLayoutChildrenException extends
            RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public NumberOfFoldingLayoutChildrenException(String message) {
            super(message);
        }
    }

    /**
     * Throws an exception if the number of views added to this layout exceeds
     * one.
     */
    private void throwCustomException(int numOfChildViews) {
        if (numOfChildViews == 1) {
            throw new NumberOfFoldingLayoutChildrenException(
                    FOLDING_VIEW_EXCEPTION_MESSAGE);
        }
    }

    /** This class uses user touch events to fold and unfold the folding view. */
    private class ScrollGestureDetector extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            mDidNotStartScroll = true;
            return true;
        }

        /**
         * All the logic here is used to determine by what factor the paper view
         * should be folded in response to the user's touch events. The logic
         * here uses vertical scrolling to fold a vertically oriented view and
         * horizontal scrolling to fold a horizontally oriented fold. Depending
         * on where the anchor point of the fold is, movements towards or away
         * from the anchor point will either fold or unfold the paper
         * respectively.
         *
         * The translation logic here also accounts for the touch slop when a
         * new user touch begins, but before a scroll event is first invoked.
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            int touchSlop = 0;
            float factor;
            if (mOrientation == Orientation.VERTICAL) {
                factor = Math.abs((float) (mTranslation)
                        / (float) (that.getHeight()));

                if (e2.getY() - mParentPositionY <= that.getHeight()
                        && e2.getY() - mParentPositionY >= 0) {
                    if ((e2.getY() - mParentPositionY) > that.getHeight()
                            * getAnchorFactor()) {
                        mTranslation -= (int) distanceY;
                        touchSlop = distanceY < 0 ? -mTouchSlop : mTouchSlop;
                    } else {
                        mTranslation += (int) distanceY;
                        touchSlop = distanceY < 0 ? mTouchSlop : -mTouchSlop;
                    }
                    mTranslation = mDidNotStartScroll ? mTranslation
                            + touchSlop : mTranslation;

                    if (mTranslation < -that.getHeight()) {
                        mTranslation = -that.getHeight();
                    }
                }
            } else {
                factor = Math.abs(((float) mTranslation)
                        / ((float) that.getWidth()));

                if (e2.getRawX() > that.getWidth() * getAnchorFactor()) {
                    mTranslation -= (int) distanceX;
                    touchSlop = distanceX < 0 ? -mTouchSlop : mTouchSlop;
                } else {
                    mTranslation += (int) distanceX;
                    touchSlop = distanceX < 0 ? mTouchSlop : -mTouchSlop;
                }
                mTranslation = mDidNotStartScroll ? mTranslation + touchSlop
                        : mTranslation;

                if (mTranslation < -that.getWidth()) {
                    mTranslation = -that.getWidth();
                }
            }

            mDidNotStartScroll = false;

            if (mTranslation > 0) {
                mTranslation = 0;
            }

            that.setFoldFactor(factor);

            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return mScrollGestureDetector.onTouchEvent(me);
    }
}
