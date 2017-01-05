package zjut.salu.share.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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
 * 关于作者activity
 */
public class AboutAuthorActivity extends RxBaseActivity {
    private WeakReference<Activity> mReference;
    @Bind(R.id.tv_top_bar_title)TextView title;
    @Bind(R.id.iv_btn_top_back)ImageButton button;
    @Override
    public int getLayoutId() {
        return R.layout.activity_about_author;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        title.setText(getString(R.string.about_author_text));
        if(mReference==null){mReference=new WeakReference<>(this);}
        MoveTouchBackUtils utils=new MoveTouchBackUtils(button,mReference.get());
        utils.bindClickBackListener();
    }

    @Override
    public void initToolBar() {

    }


}
