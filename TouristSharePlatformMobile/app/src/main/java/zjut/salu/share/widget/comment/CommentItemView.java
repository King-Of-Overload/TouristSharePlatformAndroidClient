package zjut.salu.share.widget.comment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import zjut.salu.share.R;
import zjut.salu.share.config.CuteTouristShareConfig;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.model.user.Comments;
import zjut.salu.share.model.user.Reply;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.ImageUtils;
import zjut.salu.share.utils.NumberUtil;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.widget.CommonCircleImageView;

/**评论自定义布局
 * Created by Alan
 */
public class CommentItemView extends LinearLayout implements View.OnClickListener {

    private int mPosition;
    private Comments mData;

    private CommonCircleImageView mPortraitView;
    private TextView mUserNameView;
    private TextView mContentView;
    private TextView mCreatedAtView;//时间
    private LinearLayout mCommentLayout;//回复区
    private TextView commentNumTV;
    private ImageView sexIV;//性别
    private View mMoreView;

    private ImageLoader imageLoader;


    private OnCommentListener mCommentListener;

    public CommentItemView(Context context) {
        super(context);
    }

    public CommentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        imageLoader=ImageLoader.getInstance();
    }

    public interface OnCommentListener {
        void onComment(int position);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPortraitView= (CommonCircleImageView) findViewById(R.id.item_user_avatar);
        mUserNameView= (TextView) findViewById(R.id.item_user_name);
        mContentView= (TextView) findViewById(R.id.item_comment_content);
        mCreatedAtView= (TextView) findViewById(R.id.item_comment_time);
        mCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
        sexIV= (ImageView) findViewById(R.id.item_user_sex);
        commentNumTV= (TextView) findViewById(R.id.item_comment_num);
        mMoreView=findViewById(R.id.item_comment_more);
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public void setCommentListener(OnCommentListener l) {
        this.mCommentListener = l;
    }

    public void setData(Comments data) {
        TripUser user=data.getFromuser();
        mData = data;
        DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
        imageLoader.displayImage(RequestURLs.MAIN_URL+user.getHeaderimage(),mPortraitView,options);
        mUserNameView.setText(user.getUsername());
        mContentView.setText(data.getCommentcontent());
        mCreatedAtView.setText(StringUtils.getTimeDiff(data.getCommentdate().getTime()));
        if(user.getSex().equals("男")){
            sexIV.setImageResource(R.drawable.ic_user_male);
        }else if(user.getSex().equals("女")){
            sexIV.setImageResource(R.drawable.ic_user_female);
        }else {
            sexIV.setImageResource(R.drawable.ic_user_gay_border);
        }
        updateComment();
        mMoreView.setOnClickListener(this);
    }


    private void updateComment() {
        if (mData.hasComment()) {
           // mCreatedAtView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.bg_top);
            commentNumTV.setText(NumberUtil.converString(mData.getReplies().size()));
            mCommentLayout.removeAllViews();
            mCommentLayout.setVisibility(View.VISIBLE);

            for (Reply r : mData.getReplies()) {
                TextView t = new TextView(getContext());
                t.setLayoutParams(new LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
                t.setTextSize(16);
                t.setPadding(5, 2, 0, 3);
                t.setLineSpacing(3, (float) 1.5);
                t.setText(r.getReplycontent());
                mCommentLayout.addView(t);
            }
        } else {
            commentNumTV.setText(NumberUtil.converString(0));
            //mCreatedAtView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mCommentLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.item_comment_more) {
            if (mCommentListener != null) {
                mCommentListener.onComment(mPosition);
            }
        }
    }

    public int getPosition() {
        return mPosition;
    }

    public void addComment() {
        updateComment();
    }
}
