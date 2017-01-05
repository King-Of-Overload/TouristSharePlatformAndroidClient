package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mingle.widget.LoadingView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import mehdi.sakout.dynamicbox.DynamicBox;
import zjut.salu.share.R;
import zjut.salu.share.adapter.love_card.LoveCardCommentListAdapter;
import zjut.salu.share.base.AbsBaseActivityWithBar;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.fragment.user_strategy_fragment.AllUserStrategyFragment;
import zjut.salu.share.model.EventParticipate;
import zjut.salu.share.model.LovePostCard;
import zjut.salu.share.model.LovePostCardComment;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.DeviceUtils;
import zjut.salu.share.utils.DynamicBoxUtils;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.OkHttpUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CommonCircleImageView;
import zjut.salu.share.widget.NoScrollListView;

/**
 * 爱の明信片详细信息界面
 */
public class LoveCardDetailActivity extends RxBaseActivity {
    @Bind(R.id.toolbar_love_card_detail)Toolbar mToolBar;//工具条
    @Bind(R.id.collapsing_toolbar_love_card_detail)CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.tv_title_love_card_detail)TextView titleTV;
    @Bind(R.id.tv_begin_time_love_card_detail)TextView beginTimeTV;
    @Bind(R.id.tv_dead_time_love_card_detail)TextView endTimeTV;
    @Bind(R.id.tv_tag_love_card_detail)TextView tagTV;
    @Bind(R.id.tv_author_love_card_detail)TextView authorTV;
    @Bind(R.id.tv_person_num_love_card_detail)TextView numTV;
    @Bind(R.id.tv_content_love_card_detail)TextView contentTV;
    @Bind(R.id.cciv_avatar_love_card_detail)CommonCircleImageView avatarCCIV;
    @Bind(R.id.iv_header_cover_love_card_detail)ImageView headerCoverIV;
    @Bind(R.id.nested_scroll_view_love_card_detail)NestedScrollView mParentView;
    @Bind(R.id.no_scroll_view_comment_love_card_detail)NoScrollListView listView;

    @Bind(R.id.loadView_love_postcard_detail)LoadingView loadingView;
    @Bind(R.id.activity_love_card_detail)CoordinatorLayout coordinatorLayout;
    @Bind(R.id.relative_love_card_detail)RelativeLayout mainOut;
    @Bind(R.id.card_view_no_reply_love_card_detail)CardView noReplyCardView;
    @Bind(R.id.card_view_no_scroll_view_love_card_detail)CardView scrollCardView;

    private LovePostCard postCard=null;
    private ImageLoader imageLoader=null;
    private DynamicBox dynamicBox=null;
    private List<LovePostCardComment> comments=null;
    private LoveCardCommentListAdapter adapter=null;
    private WeakReference<Activity> mReference=null;
    private WeakReference<NoScrollListView> listViewReference=null;
    @Override
    public int getLayoutId() {
        return R.layout.activity_love_card_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        coordinatorLayout.setVisibility(View.INVISIBLE);
        loadingView.setVisibility(View.VISIBLE);
        mReference=new WeakReference<>(this);
        listViewReference=new WeakReference<>(listView);
        Boolean isNetworkAvailable = CommonUtils.isNetworkAvailable(this);
        dynamicBox=new DynamicBox(this,mainOut);
        DynamicBoxUtils.bindDynamicBox(dynamicBox,getString(R.string.hint_text),getString(R.string.server_exception_text),
                getString(R.string.loading_now_text),new GetDataFailedClickListener());
        if(isNetworkAvailable) {
            getData();
        }else{
            dynamicBox.showExceptionLayout();
            ToastUtils.ShortToast(R.string.no_network_connection);
        }
    }

    private void getData(){
        dynamicBox.hideAll();
        Intent intent = getIntent();
        postCard = (LovePostCard) intent.getSerializableExtra("love_card");
        new Handler().postDelayed(() -> {
            titleTV.setText(postCard.getLovetitle());
            String beginTime = getText(R.string.begin_text) + StringUtils.formatDate(postCard.getLovestarttime(),"yyyy-MM-dd HH:mm");
            beginTimeTV.setText(beginTime);
            String endTime = getText(R.string.end_text) + StringUtils.formatDate(postCard.getLovedeadline(),"yyyy-MM-dd HH:mm");
            endTimeTV.setText(endTime);
            String tag = getText(R.string.tag_text) + postCard.getLovetags();
            tagTV.setText(tag);
            String username = getText(R.string.advertise_text) + postCard.getUser().getUsername();
            authorTV.setText(username);
            List<EventParticipate> participates = postCard.getParticipates();
            int chosenNum = 0;
            if (!participates.isEmpty()) {
                for (EventParticipate p : participates) {
                    if (p.getIschosen() == 0) {
                        chosenNum += 1;
                    }
                }
            }
            String num = getText(R.string.send_num_text) + String.valueOf(postCard.getLovenumber()) + getText(R.string.page_text) +
                    "(" + getText(R.string.confirm_text) + String.valueOf(chosenNum) + getText(R.string.person_text) + ")";
            numTV.setText(num);
            contentTV.setText(postCard.getLovecontent());
            DisplayImageOptions options = ImageLoaderUtils.getImageOptions();
            imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(RequestURLs.MAIN_URL + postCard.getLovepic(), headerCoverIV, options);
            imageLoader.displayImage(RequestURLs.MAIN_URL + postCard.getUser().getHeaderimage(), avatarCCIV, options);
            comments=postCard.getComments();
            if(!comments.isEmpty()){
                if(noReplyCardView.getVisibility()==View.VISIBLE){noReplyCardView.setVisibility(View.GONE);}
                if(scrollCardView.getVisibility()==View.GONE||listView.getVisibility()==View.INVISIBLE){listView.setVisibility(View.VISIBLE);}
                adapter=new LoveCardCommentListAdapter(mReference.get(),listViewReference.get(),comments);
                listView.setAdapter(adapter);
            }else{
                scrollCardView.setVisibility(View.INVISIBLE);
                noReplyCardView.setVisibility(View.VISIBLE);//显示没有评论
            }
            loadingView.setVisibility(View.GONE);
            if (coordinatorLayout.getVisibility() == View.INVISIBLE) {
                coordinatorLayout.setVisibility(View.VISIBLE);
            }
        }, 3500);
    }

    /**
     * 重试按钮点击事件
     */
    private class GetDataFailedClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            getData();
        }
    }

    @Override
    public void initToolBar() {
        mToolBar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolBar.setNavigationOnClickListener(v -> finish());
        mCollapsingToolbarLayout.setTitle(postCard.getLovetitle());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        adjustParentView();
    }

    private void adjustParentView() { //兼容NestedScrollView
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        Rect outRect = new Rect();  //状态栏高度
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        mParentView.setMinimumHeight(DeviceUtils.getScreenHeight(this) - actionBarHeight - outRect.top);
    }
}
