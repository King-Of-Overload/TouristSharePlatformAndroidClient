package zjut.salu.share.activity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.OnClick;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.utils.FileUtil;
import zjut.salu.share.utils.ImageUtils;
import zjut.salu.share.widget.ClipImageLayout;

/**
 * 头像裁剪界面
 */
public class ClipImageActivity extends RxBaseActivity{
    public static final String RESULT_PATH = "crop_image";
    private static final String KEY = "path";
    @Bind(R.id.clipImageLayout)ClipImageLayout mClipImageLayout;
    private String TMP_PATH;
    @Override
    public int getLayoutId() {
        return R.layout.activity_clip_image;
    }
    public static void startActivity(Activity activity, String path, int code) {
        Intent intent = new Intent(activity, ClipImageActivity.class);
        intent.putExtra(KEY, path);
        activity.startActivityForResult(intent, code);
    }
    @Override
    public void initViews(Bundle savedInstanceState) {
        String path = getIntent().getStringExtra(KEY);
        TMP_PATH=path;
        // 有的系统返回的图片是旋转了，有的没有旋转，所以处理
        int degree = ImageUtils.readBitmapDegree(path);
        Bitmap bitmap = ImageUtils.createBitmap(path);
        if (bitmap != null) {
            if (degree == 0) {
                mClipImageLayout.setImageBitmap(bitmap);
            } else {
                mClipImageLayout.setImageBitmap(ImageUtils.rotateBitmap(degree, bitmap));
            }
        } else {
            finish();
        }
    }

    /**
     * 确认按钮
     * @param v
     */
    @OnClick(R.id.okBtn)
    public void confirmBtnClick(View v){
        if (v.getId() == R.id.okBtn) {
            Bitmap bitmap = mClipImageLayout.clip();
            FileUtil.saveBitmap(bitmap, TMP_PATH);
            Intent intent = new Intent();
            intent.putExtra(RESULT_PATH, TMP_PATH);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    /**
     * 取消按钮
     * @param v
     */
    @OnClick(R.id.cancleBtn)
    public void cancelBtnClick(View v){
        finish();
    }

    @Override
    public void initToolBar() {

    }
}
