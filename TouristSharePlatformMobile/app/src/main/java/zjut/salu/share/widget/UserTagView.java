package zjut.salu.share.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import zjut.salu.share.R;

/**用户Tag自定义View
 * Created by Salu on 2017/1/29.
 */

public class UserTagView extends FrameLayout{
    private CommonCircleImageView avatarView;

    private TextView userNameText;

    private OnClickListener onClickListener;

    private Activity activity;

    private String name;

    private String avatarUrl;

    public UserTagView(Context context)
    {

        this(context, null);
    }

    public UserTagView(Context context, AttributeSet attrs)
    {

        this(context, attrs, 0);
    }

    public UserTagView(Context context, AttributeSet attrs, int defStyleAttr)
    {

        super(context, attrs, defStyleAttr);
        @SuppressLint("InflateParams")
        LinearLayout cardView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_user_tag_view, null);
        avatarView = (CommonCircleImageView) cardView.findViewById(R.id.user_avatar);
        userNameText = (TextView) cardView.findViewById(R.id.user_name);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.user_tag_view_height));
        this.addView(cardView, lp);

        cardView.setOnClickListener(view -> {

            if (activity != null)
            {
                //TODO:点击事件
                //UserInfoDetailsActivity.launch(activity, name, mid, avatarUrl);
            } else if (onClickListener != null)
            {
                onClickListener.onClick(view);
            }
        });
    }

    public void setAvatar(Bitmap bitmap)
    {

        avatarView.setImageBitmap(bitmap);
    }

    public void setAvatar(Drawable drawable)
    {

        avatarView.setImageDrawable(drawable);
    }

    public void setAvatar(@DrawableRes int id)
    {

        avatarView.setImageResource(id);
    }

    public CommonCircleImageView getAvatarView()
    {

        return this.avatarView;
    }

    public void setUserName(String userName)
    {

        userNameText.setText(userName);
    }

    public TextView getUserNameText()
    {

        return this.userNameText;
    }

    public void setUpWithInfo(Activity activity, String name, String avatarUrl)
    {

        this.activity = activity;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.setUserName(name);

        Glide.with(getContext())
                .load(this.avatarUrl)
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.ico_user_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(avatarView);
    }

    @Override
    public void setOnClickListener(OnClickListener listener)
    {

        this.onClickListener = listener;
    }
}
