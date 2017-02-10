package zjut.salu.share.activity.banggumi;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.PhotoPreviewActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.foamtrace.photopicker.intent.PhotoPreviewIntent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import mabbas007.tagsedittext.TagsEditText;
import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.activity.lightstrategy.EditLightStrategy;
import zjut.salu.share.activity.mediapicker.MediaItem;
import zjut.salu.share.activity.mediapicker.MediaOptions;
import zjut.salu.share.activity.mediapicker.activities.MediaPickerActivity;
import zjut.salu.share.adapter.lightstrategy.GridAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.lightstrategy.banggume.BanggumeTag;
import zjut.salu.share.utils.LogUtil;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.CircleProgressView;

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
    @Bind(R.id.circle_progress)CircleProgressView progressView;
    @Bind(R.id.gridView)GridView gridView;

    private WeakReference<Activity> mReference;
    private List<BanggumeTag> chosenTags;
    private List<String> personalEditTagsList;//自定义标签集合
    private String bannggumeURL;//视频uri
    private String coverURL;//封面uri
    private List<BanggumeTag> tags;

    private int columnWidth;
    private ArrayList<String> imagePaths = null;
    private GridAdapter gridAdapter;
    private zjut.salu.share.utils.OkHttpUtils okHttpUtils;
    @Override
    public int getLayoutId() {
        return R.layout.activity_upload_banggume;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        okHttpUtils=new zjut.salu.share.utils.OkHttpUtils();
        mReference=new WeakReference<>(this);
        chooseStatusTV.setVisibility(View.VISIBLE);
        fileNameTV.setVisibility(View.INVISIBLE);
        chooseStatusTV.setText(R.string.no_banggume_selected_text);
        initGridView();
        initTagFlow();
        initTagsEditText();
    }

    /**
     * 初始化封面选择区
     */
    private void initGridView() {
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView.setNumColumns(cols);
        // Item Width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
        columnWidth = (screenWidth - columnSpace * (cols - 1)) / cols;
        // preview
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            PhotoPreviewIntent intent = new PhotoPreviewIntent(mReference.get());
            intent.setCurrentItem(position);
            intent.setPhotoPaths(imagePaths);
            startActivityForResult(intent, EditLightStrategy.REQUEST_PREVIEW_CODE);
        });
    }

    /**
     * 初始化自定义tag视图
     */
    private void initTagsEditText(){
        personalEditTagsList=new ArrayList<>();
        tagsEditText.setHint(R.string.enter_personal_tag_text);
        tagsEditText.setHintTextColor(getResources().getColor(R.color.text_hint));
        tagsEditText.setTagsListener(this);
        tagsEditText.setTagsBackground(R.drawable.square);
        tagsEditText.setTagsWithSpacesEnabled(true);
        //tagsEditText.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,personalEditTagsList));
        tagsEditText.setThreshold(1);

    }

    /**
     * 换一批标签点击事件
     */
    @OnClick(R.id.tv_change_tag)
    public void changeTagClick(View v){
        initTagFlow();
    }

    /**
     * 初始化tagflow区域数据
     */
    private void initTagFlow(){
        Map<String,Object> params=new HashMap<>();
        params.put("num",16+"");
        Observable<String> observable=okHttpUtils.asyncGetRequest(RequestURLs.GET_BANGGUME_TAGS,params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(()->ToastUtils.ShortToast(R.string.server_down_text));
                    }

                    @Override
                    public void onNext(String result) {
                        chosenTags=new ArrayList<>();
                        Gson gson=new Gson();
                        tags=gson.fromJson(result,new TypeToken<List<BanggumeTag>>(){}.getType());
                        setFlowAdapter();
                        flowLayout.setOnSelectListener(selectPosSet -> {
                            if(null!=chosenTags&&chosenTags.size()>0){
                                chosenTags.clear();
                            }
                            chosenTags=new ArrayList<>();
                            Iterator<Integer> iterator=selectPosSet.iterator();
                            while(iterator.hasNext()){
                                chosenTags.add(tags.get(iterator.next()));
                            }
                            LogUtil.d(chosenTags.toString());
                        });
                    }
                });
    }

    private void setFlowAdapter(){
        flowLayout.setAdapter(new TagAdapter<BanggumeTag>(tags){
            @Override
            public View getView(FlowLayout parent, int position, BanggumeTag banggumeTag) {
                TextView mTags= (TextView) LayoutInflater.from(mReference.get())
                        .inflate(R.layout.layout_tags_item,parent,false);
                mTags.setText(banggumeTag.getBanggumetagname());
                return mTags;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case REQUEST_MEDIA:{//选择视频
                    List<MediaItem> mediaItemList=MediaPickerActivity.getMediaItemSelected(data);
                    MediaItem item=mediaItemList.get(0);
                    bannggumeURL=item.getPathOrigin(mReference.get());
                    chooseStatusTV.setText(R.string.has_chosen_video_text);
                    fileNameTV.setVisibility(View.VISIBLE);
                    fileNameTV.setText(bannggumeURL.substring(bannggumeURL.lastIndexOf("/")+1));
                    LogUtil.d(bannggumeURL);//  /storage/emulated/0/ttpod/mv/Rainbow - Sweet Dream - 标清.mp4
                    break;
                }
                case EditLightStrategy.REQUEST_CAMERA_CODE:{//选择封面
                    loadAdapter(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    break;
                }
                // 预览
                case EditLightStrategy.REQUEST_PREVIEW_CODE: {
                    loadAdapter(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
                    break;
                }
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

    /**
     * 选择封面点击事件
     */
    @OnClick(R.id.tv_choose_cover)
    public void chooseCoverClick(View v){
        PhotoPickerIntent intent=new PhotoPickerIntent(mReference.get());
        intent.setShowCarema(true);//是否显示照片
        intent.setMaxTotal(9);
        intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
        intent.setSelectModel(SelectModel.SINGLE);
        startActivityForResult(intent,EditLightStrategy.REQUEST_CAMERA_CODE);
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
            if(null!=personalEditTagsList&&personalEditTagsList.size()>0){personalEditTagsList.clear();}
            personalEditTagsList=new ArrayList<>();
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
        switch (item.getItemId()){
            case R.id.id_upload:{
                if(titleET.getText().toString().equals("")){
                    ToastUtils.ShortToast(R.string.enter_bannggume_title_text);
                }else if(contentET.getText().toString().equals("")){
                    ToastUtils.ShortToast(R.string.enter_bannggume_content_text);
                }else if(personalEditTagsList.size()==0&&chosenTags.size()==0){
                    ToastUtils.ShortToast(R.string.personal_tag_or_default_tags_not_null_text);
                }else if(null==bannggumeURL||bannggumeURL.equals("")){
                    ToastUtils.ShortToast(R.string.enter_video_file_text);
                }else{
                    progressView.setVisibility(View.VISIBLE);
                    progressView.spin();
                    ToastUtils.ShortToast(R.string.waiting_uploading_now_text);
                    PostFormBuilder builder= OkHttpUtils.post();
                    File banngumeFile=new File(bannggumeURL);
                    builder.addFile("banggume", StringUtils.formatDate(new Date(),"yyyyMMddHHmmss")+banngumeFile.getName(),banngumeFile);
                    File coverFile=new File(coverURL);
                    builder.addFile("cover", StringUtils.formatDate(new Date(),"yyyyMMddHHmmss")+coverFile.getName(),coverFile);
                    builder.url(RequestURLs.UPLOAD_BANGGUME_LIGHT_STRATEGY);
                    builder.addParams("userid", PreferenceUtils.getString("userid",""));

                    if(personalEditTagsList.size()>0){
                        StringBuilder sb=new StringBuilder();
                        for(String str:personalEditTagsList){
                            sb.append(str).append("/");
                        }
                        sb.deleteCharAt(sb.length()-1);
                        builder.addParams("personalEditTags",sb.toString());
                    }
                    if(chosenTags.size()>0){
                        Gson gson=new Gson();
                        builder.addParams("tags",gson.toJson(chosenTags));
                    }
                    builder.addParams("title",titleET.getText().toString());
                    builder.addParams("content",contentET.getText().toString());
                    Map<String,String> headers=new HashMap<>();
                    headers.put("Content-Type","multipart/form-data");
                    builder.headers(headers);
                    RequestCall call=builder.build();
                    call.execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtils.ShortToast(R.string.server_down_text);
                            progressView.stopSpinning();
                            progressView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if(response.equals("success")){
                                progressView.setVisibility(View.INVISIBLE);
                                ToastUtils.LongToast(R.string.upload_sucess_text);
                                finish();
                            }
                        }
                    });
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAdapter(ArrayList<String> paths){
        if(imagePaths == null){
            imagePaths = new ArrayList<>();
        }
        imagePaths.clear();
        imagePaths.addAll(paths);//  /storage/emulated/0/Pictures/JPEG_20170204_172214_.jpg
        coverURL=imagePaths.get(0);
        try{
            JSONArray obj = new JSONArray(imagePaths);
            Log.e("--", obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        if(gridAdapter == null){
            gridAdapter = new GridAdapter(imagePaths,mReference.get(),columnWidth);
            gridView.setAdapter(gridAdapter);
        }else {
            gridAdapter.notifyDataSetChanged();
        }
    }
}
