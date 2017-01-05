package zjut.salu.share.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.Bind;
import butterknife.OnClick;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.AppBarStateChangeEvent;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.SystemBarHelper;
import zjut.salu.share.widget.CommonCircleImageView;

/**
*个人信息界面
 * Alan Lu さる
 */
public class PersonalInfoActivity extends RxBaseActivity {
    @Bind(R.id.user_avatar_view)CommonCircleImageView mAvatarImage;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.app_bar_layout) AppBarLayout mAppBarLayout;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.line)View mLineView;

    @Bind(R.id.tv_secret_message_personal_info)TextView secretMessageTV;//发私信
    @Bind(R.id.tv_focus_personal_info)TextView focusTV;//关注
    @Bind(R.id.tv_btn_personal_info_data)TextView personalDataTV;//个人资料

    @Bind(R.id.user_name)TextView userNameTV;//用户名
    @Bind(R.id.tv_focus_users)TextView focusNumTV;//关注数
    @Bind(R.id.user_sex)ImageView sexIV;//性别
    @Bind(R.id.tv_fans)TextView fansNumTV;//粉丝数
    @Bind(R.id.user_desc)TextView usignatureTV;//签名
    private Boolean isCurrentUser=false;
    private TripUser user=null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_info;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent=getIntent();
        isCurrentUser=intent.getBooleanExtra("isCurrentUser",false);
        if(isCurrentUser){//当前用户
            user=PreferenceUtils.acquireCurrentUser();
        }else{//非当前用户
            user= (TripUser) intent.getSerializableExtra("user");
        }
        changeUserUI();
    }

    /**
     * 改变界面UI
     */
    private void changeUserUI(){
        if(isCurrentUser){
            if(secretMessageTV.getVisibility()==View.VISIBLE){secretMessageTV.setVisibility(View.INVISIBLE);}
            if(focusTV.getVisibility()==View.VISIBLE){focusTV.setVisibility(View.INVISIBLE);}
            if(personalDataTV.getVisibility()==View.GONE){personalDataTV.setVisibility(View.VISIBLE);}
        }else{
            if(secretMessageTV.getVisibility()==View.INVISIBLE){secretMessageTV.setVisibility(View.VISIBLE);}
            if(focusTV.getVisibility()==View.INVISIBLE){focusTV.setVisibility(View.VISIBLE);}
            if(personalDataTV.getVisibility()==View.VISIBLE){personalDataTV.setVisibility(View.GONE);}
        }
        Glide.with(PersonalInfoActivity.this)//头像加载
                .load(RequestURLs.MAIN_URL+user.getHeaderimage())
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.ico_user_default)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mAvatarImage);
        userNameTV.setText(user.getUsername());
        String sex=user.getSex();
        if(sex.equals("未知")||sex.equals("")){
            sexIV.setImageResource(R.drawable.ic_user_gay_border);
        }else if(sex.equals("女")){
            sexIV.setImageResource(R.drawable.ic_user_female);
        }else{
            sexIV.setImageResource(R.drawable.ic_user_male);
        }
        focusNumTV.setText(String.valueOf(user.getFocusNum()));
        fansNumTV.setText(String.valueOf(user.getFollowNum()));
        usignatureTV.setText(user.getUsignature());
    }

    /**
     * 个人资料点击事件
     */
    @OnClick(R.id.tv_btn_personal_info_data)
    public void personalDataClickEvent(View v){
        Intent intent=new Intent(this,UserDataActivity.class);
        intent.putExtra("currentUser",user);
        startActivity(intent);
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("");
//        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);


        //设置StatusBar透明
        SystemBarHelper.immersiveStatusBar(this);
        SystemBarHelper.setHeightAndPadding(this, mToolbar);

        //设置AppBar展开折叠状态监听
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeEvent()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset)
            {

                if (state == State.EXPANDED)
                {
                    //展开状态
                    mCollapsingToolbarLayout.setTitle("");
                    mLineView.setVisibility(View.VISIBLE);
                } else if (state == State.COLLAPSED)
                {
                    //折叠状态
                    mCollapsingToolbarLayout.setTitle("有咩酱");
                    mLineView.setVisibility(View.GONE);
                } else
                {
                    mCollapsingToolbarLayout.setTitle("");
                    mLineView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
