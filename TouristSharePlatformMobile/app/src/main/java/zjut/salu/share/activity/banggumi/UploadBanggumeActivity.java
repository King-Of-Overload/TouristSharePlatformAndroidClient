package zjut.salu.share.activity.banggumi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import mabbas007.tagsedittext.TagsEditText;
import zjut.salu.share.R;
import zjut.salu.share.activity.mediapicker.MediaItem;
import zjut.salu.share.activity.mediapicker.MediaOptions;
import zjut.salu.share.activity.mediapicker.activities.MediaPickerActivity;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.LogUtil;

/**
 * 上传视频控制层
 * @author Alan-Mac
 */
public class UploadBanggumeActivity extends RxBaseActivity implements TagsEditText.TagsEditListener{
    public static final int REQUEST_MEDIA = 100;
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.et_bannggume_title)EditText titleET;
    @Bind(R.id.et_bannggume_content)EditText contentET;
    @Bind(R.id.tags_layout)TagFlowLayout flowLayout;
    @Bind(R.id.tv_choose_status)TextView chooseStatusTV;
    @Bind(R.id.tv_file_name)TextView fileNameTV;
    @Bind(R.id.tagsEditText)TagsEditText tagsEditText;

    private WeakReference<Activity> mReference;
    private List<String> personalEditTagsList;//自定义标签集合
    @Override
    public int getLayoutId() {
        return R.layout.activity_upload_banggume;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference=new WeakReference<>(this);
        chooseStatusTV.setVisibility(View.VISIBLE);
        fileNameTV.setVisibility(View.INVISIBLE);
        chooseStatusTV.setText(R.string.no_banggume_selected_text);
        initTagFlow();
        initTagsEditText();
    }

    /**
     * 初始化自定义tag视图
     */
    private void initTagsEditText(){
        tagsEditText.setHint(R.string.enter_personal_tag_text);
        tagsEditText.setHintTextColor(getResources().getColor(R.color.text_hint));
        tagsEditText.setTagsListener(this);
        tagsEditText.setTagsBackground(R.drawable.square);
        tagsEditText.setTagsWithSpacesEnabled(true);
        personalEditTagsList=new ArrayList<>();
        //tagsEditText.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,personalEditTagsList));
        tagsEditText.setThreshold(1);

    }

    /**
     * 初始化tagflow区域数据
     */
    private void initTagFlow(){
        //TODO:需要对数据重新载入
        List<String> tags=new ArrayList<>();
        tags.add("有咩酱");tags.add("有咩酱");tags.add("有咩酱");tags.add("有咩酱");tags.add("有咩酱");tags.add("郑合惠子");
        flowLayout.setAdapter(new TagAdapter<String>(tags) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView mTags= (TextView) LayoutInflater.from(mReference.get())
                        .inflate(R.layout.layout_tags_item,parent,false);
                mTags.setText(s);
                return mTags;
            }
        });
        flowLayout.setOnSelectListener(selectPosSet -> {
            //TODO:将选中的标签添加到集合中
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_MEDIA){
            if(resultCode==RESULT_OK){
                List<MediaItem> mediaItemList=MediaPickerActivity.getMediaItemSelected(data);
                MediaItem item=mediaItemList.get(0);
                String path=item.getPathOrigin(mReference.get());
                LogUtil.d(path);//  /storage/emulated/0/ttpod/mv/Rainbow - Sweet Dream - 标清.mp4
            }
        }
    }

    /**
     * 选择视频点击事件
     */
    @OnClick(R.id.tv_choose_banggume)
    public void chooseBanggumeClick(View v){
        MediaOptions.Builder builder = new MediaOptions.Builder();
        MediaOptions options = builder.selectVideo().canSelectMultiVideo(false).build();
        MediaPickerActivity.open(mReference.get(),REQUEST_MEDIA,options);
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.upload_banggume_text);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }

    @Override
    public void onTagsChanged(Collection<String> collection) {
        Object[] results=collection.toArray();
        personalEditTagsList.clear();
        if(results.length!=0){
            if(null==personalEditTagsList){personalEditTagsList=new ArrayList<>();}
            for (Object s:results){
                LogUtil.d(s.toString());
                personalEditTagsList.add(s.toString());
            }
        }
    }

    @Override
    public void onEditingFinished() {
        LogUtil.d("OnEditing finished");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload_banggume_light_strategy,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
