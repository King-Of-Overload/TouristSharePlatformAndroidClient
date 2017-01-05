package zjut.salu.share.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.OnClick;
import zjut.salu.share.R;
import zjut.salu.share.base.AbsBaseActivity;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.MoveTouchBackUtils;

/**
 * 关于芳草寻源
 */
public class AboutAppActivity extends RxBaseActivity {
    @Bind(R.id.tv_app_introduce)TextView introduceTV;
    @Bind(R.id.tv_open_source)TextView openSourceTV;
    @Bind(R.id.iv_btn_top_back)ImageButton button;
    @Bind(R.id.tv_top_bar_title)TextView title;
    private WeakReference<Activity> mReference;
    @Override
    public int getLayoutId() {
        return R.layout.activity_about_app;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<Activity>(this);
        introduceTV.setText(Html.fromHtml("<h2>芳草寻源介绍</h2><br/><p>&nbsp;&nbsp;“芳草寻源”旅游资源分享平台(以下简称芳草寻源)是一个综合性的旅游资源发布与资源下载平台，自然界是由绿色组成，生命短暂但是我们走出去的脚步从未停下，感受自然，化身芳草，置身花海，享受宁静时光，好的时光与记忆，自然要与大家分享，来到这里，在众多芳草中追寻我们那一份遗失的宁静与美好，芳草寻源，给你宁静，还你活力，“芳草寻源”为你而来。</p>"));
        openSourceTV.setText(Html.fromHtml("<h2>开放源代码许可</h2><br/>\n" +
                "        <p> testCompile 'junit:junit:4.12'</p>\n" +
                "        <p> compile 'com.squareup.okhttp3:okhttp:3.4.1'</p>\n" +
                "        <p>com.github.xxl6097:okhttputils:2.4.1</p>\n" +
                "        <p>com.android.support:design:23.4.0</p>\n" +
                "        <p>com.android.support:cardview-v7:23.4.0</p>\n" +
                "        <p>com.google.code.gson:gson:2.7</p>\n" +
                "        <p>com.squareup.picasso:picasso:2.5.2</p>\n" +
                "        <p>cn.pedant.sweetalert:library:1.3</p>"));
        title.setText(R.string.about_app_title_text);
        MoveTouchBackUtils utils=new MoveTouchBackUtils(button,mReference.get());
        utils.bindClickBackListener();
    }

    @Override
    public void initToolBar() {

    }


}
