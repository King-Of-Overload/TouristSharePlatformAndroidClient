package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.KeyBoardUtil;
import zjut.salu.share.utils.StatusBarUtil;

public class TotalStationSearchActivity extends RxBaseActivity {
    @Bind(R.id.iv_search_loading) ImageView mLoadingView;
    @Bind(R.id.search_layout)LinearLayout mSearchLayout;
    @Bind(R.id.search_edit) EditText mSearchEdit;
    @Bind(R.id.search_img) ImageView mSearchBtn;
    @Bind(R.id.search_text_clear) ImageView mSearchTextClear;
    private AnimationDrawable mAnimationDrawable;
    @Override
    public int getLayoutId()
    {

        return R.layout.activity_search;
    }

    @Override
    public void initToolBar()
    {
        //设置6.0以上StatusBar字体颜色
        StatusBarUtil.from(this)
                .setLightStatusBar(true)
                .process();
    }
    @Override
    public void initViews(Bundle savedInstanceState)
    {
        mLoadingView.setImageResource(R.drawable.anim_search_loading);
        mAnimationDrawable = (AnimationDrawable) mLoadingView.getDrawable();
        showSearchAnim();
        mSearchEdit.clearFocus();
        mSearchEdit.setText("呵呵");
        //getSearchData();
        search();
        setUpEditText();
    }

    public static void launch(Activity activity, String str)
    {

        Intent mIntent = new Intent(activity, TotalStationSearchActivity.class);
        //mIntent.putExtra(ConstantUtil.EXTRA_CONTENT, str);
        activity.startActivity(mIntent);
    }

    private void setUpEditText()
    {

        RxTextView.textChanges(mSearchEdit)
                .compose(this.bindToLifecycle())
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    if (!TextUtils.isEmpty(s))
                        mSearchTextClear.setVisibility(View.VISIBLE);
                    else
                        mSearchTextClear.setVisibility(View.GONE);
                });


        RxView.clicks(mSearchTextClear)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {

                    mSearchEdit.setText("");
                });


        RxTextView.editorActions(mSearchEdit)
                .filter(integer -> !TextUtils.isEmpty(mSearchEdit.getText().toString().trim()))
                .filter(integer -> integer == EditorInfo.IME_ACTION_SEARCH)
                .flatMap(new Func1<Integer,Observable<String>>()
                {

                    @Override
                    public Observable<String> call(Integer integer)
                    {

                        return Observable.just(mSearchEdit.getText().toString().trim());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    KeyBoardUtil.closeKeybord(mSearchEdit,
                            TotalStationSearchActivity.this);
                    showSearchAnim();
//                    clearData();
//                    content = s;
//                    getSearchData();
                });
    }
    private void search()
    {

        RxView.clicks(mSearchBtn)
                .throttleFirst(2, TimeUnit.SECONDS)
                .map(aVoid -> mSearchEdit.getText().toString().trim())
                .filter(s -> !TextUtils.isEmpty(s))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    KeyBoardUtil.closeKeybord(mSearchEdit,
                            TotalStationSearchActivity.this);
                    showSearchAnim();
                    //clearData();
                    //content = s;
                    //getSearchData();
                });
    }


    private void showSearchAnim()
    {

        mLoadingView.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        mAnimationDrawable.start();
    }
}
