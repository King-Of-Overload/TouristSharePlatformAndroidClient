package zjut.salu.share.activity.lightstrategy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.NineImage;
import zjut.salu.share.model.lightstrategy.DiaryLightStrategy;
import zjut.salu.share.model.lightstrategy.DiaryLightStrategyImage;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.widget.NineGridLayout;

/**
 * 轻游记日记详情控制层
 * @author Alan-Mac
 */
public class DiaryLightStrategyDetail extends RxBaseActivity {
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.iv_avatar_main_page)ImageView avatarIV;
    @Bind(R.id.tv_username_main_page)TextView userNameTV;
    @Bind(R.id.tv_time_main_page)TextView timeTV;
    @Bind(R.id.tv_content_main_page)TextView contentTV;
    @Bind(R.id.nl_image_main_page)NineGridLayout nineGridLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_diary_light_strategy_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        WeakReference<Activity> mReference = new WeakReference<>(this);
        Intent intent=getIntent();
        DiaryLightStrategy diaryLightStrategy= (DiaryLightStrategy) intent.getSerializableExtra("diary_light_strategy");
        ImageLoaderUtils.loadAvatarWithURL(mReference.get(), RequestURLs.MAIN_URL+diaryLightStrategy.getUser().getHeaderimage()
        , DiskCacheStrategy.NONE,avatarIV);
        timeTV.setText(StringUtils.getTimeDiff(diaryLightStrategy.getDiarytime().getTime()));
        contentTV.setText(diaryLightStrategy.getDiarycontent());
        userNameTV.setText(diaryLightStrategy.getUser().getUsername());
        List<NineImage> imgs=new ArrayList<>();
        for(int i=0;i<diaryLightStrategy.getImages().size();i++){
            NineImage nineImage=new NineImage();
            DiaryLightStrategyImage strategyImage=diaryLightStrategy.getImages().get(i);
            nineImage.setUrl(RequestURLs.MAIN_URL+strategyImage.getDiaryimgurl());
            nineImage.setPosition(i+"");
            imgs.add(nineImage);
        }
        nineGridLayout.setImagesData(imgs);
    }

    @Override
    public void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setTitle(R.string.diary_detail_text);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
