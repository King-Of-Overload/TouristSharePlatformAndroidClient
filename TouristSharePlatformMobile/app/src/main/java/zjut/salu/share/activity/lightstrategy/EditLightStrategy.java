package zjut.salu.share.activity.lightstrategy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.foamtrace.photopicker.ImageCaptureManager;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.PhotoPreviewActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.foamtrace.photopicker.intent.PhotoPreviewIntent;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import zjut.salu.share.R;
import zjut.salu.share.adapter.lightstrategy.GridAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.ToastUtils;

public class EditLightStrategy extends RxBaseActivity {
    public static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.gridView)GridView gridView;

    private WeakReference<Activity> mReference;
    private ImageCaptureManager captureManager=null;
    private ArrayList<String> imagePaths = null;
    private int columnWidth;
    private GridAdapter gridAdapter;
    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_light_strategy;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mReference = new WeakReference<>(this);
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
            startActivityForResult(intent, REQUEST_PREVIEW_CODE);
        });
    }
    /**
     * 选择照片按钮
     */
    @OnClick(R.id.tv_choose_pics)
    public void choosePicsClick(View v){
        PhotoPickerIntent intent=new PhotoPickerIntent(mReference.get());
        intent.setShowCarema(true);//是否显示照片
        intent.setMaxTotal(9);
        intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
        intent.setSelectModel(SelectModel.MULTI);
        startActivityForResult(intent,REQUEST_CAMERA_CODE);
    }

    /**
     * 拍一张
     */
    @OnClick(R.id.tv_take_photo)
    public void takePhotoClick(View v){
        try {
            if(captureManager==null){
                captureManager=new ImageCaptureManager(mReference.get());
            }
            Intent intent=captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent,ImageCaptureManager.REQUEST_TAKE_PHOTO);
        }catch (IOException e){
            ToastUtils.ShortToast(R.string.no_camera_text);
        }
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.write_text_light_strategy_text);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA_CODE: {//选择照片
                    loadAdapter(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    break;
                }
                case ImageCaptureManager.REQUEST_TAKE_PHOTO: {//拍照
                    if (captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();
                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(captureManager.getCurrentPhotoPath());
                        loadAdapter(paths);
                        break;
                    }
                }
                // 预览
                case REQUEST_PREVIEW_CODE: {
                    loadAdapter(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
                    break;
                }
            }
        }
    }

    private void loadAdapter(ArrayList<String> paths){
        if(imagePaths == null){
            imagePaths = new ArrayList<>();
        }
        imagePaths.clear();
        imagePaths.addAll(paths);

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
