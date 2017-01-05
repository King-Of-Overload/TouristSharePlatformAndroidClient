package zjut.salu.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.utils.BroadcastReceiverUtil;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.FileUtil;
import zjut.salu.share.utils.ImageUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.widget.BottomIOSMenuDialog;
import zjut.salu.share.widget.ScrollerNumberPicker;

import static zjut.salu.share.R.array.url;

public class UserDataActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar mToolBar;

    @Bind(R.id.iv_user_avatar_user_data)ImageView avatarIV;//头像
    @Bind(R.id.tv_user_data_nick_name)TextView nickNameTV;//昵称
    @Bind(R.id.tv_user_data_sex)TextView sexTV;//性别
    @Bind(R.id.tv_user_data_mail)TextView mailTV;//邮箱
    @Bind(R.id.tv_user_data_cell_phone)TextView phoneTV;//电话
    @Bind(R.id.tv_user_data_city)TextView cityTV;//城市
    @Bind(R.id.tv_user_data_signature)TextView signatureTV;//签名

    private BottomIOSMenuDialog menuDialog=null;
    private WeakReference<Activity> mReferrence=null;
    private TripUser user=null;
    private Boolean isNetWorkAvailable=false;
    private zjut.salu.share.utils.OkHttpUtils httpUtils=null;
    private Uri TMP_PATH=null;
    private static final String TAG="UserDataActivity";

    private static final int UPDATE_NICKNAME=0;
    private static final int UPDATE_EMAIL=1;
    private static final int UPDATE_SIGNATURE = 2;
    private static final int CAMERA_WITH_DATA = 3;
    private static final int CROP_RESULT_CODE = 4;
    private static final int ALBUM_WITH_DATA=5;
    private static final int UPDATE_PHONE =6;
    @Override
    public int getLayoutId() {
        return R.layout.activity_user_data;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        httpUtils=new zjut.salu.share.utils.OkHttpUtils();
        mReferrence=new WeakReference<>(this);
        Intent intent=getIntent();
        user= (TripUser) intent.getSerializableExtra("currentUser");
        setUIInfo();
    }

    /**
     * 修改界面UI信息
     */
    private void setUIInfo(){
        Glide.with(mReferrence.get())//头像加载
                .load(RequestURLs.MAIN_URL+user.getHeaderimage())
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.ico_user_default)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(avatarIV);//加载头像
        nickNameTV.setText(user.getUsername());
        if(user.getSex().equals("未知")){
            sexTV.setText(R.string.keep_secret_text);
        }else{
            sexTV.setText(user.getSex());
        }
        mailTV.setText(user.getUseremail());
        if(("").equals(user.getPhone())||null==user.getPhone()){
            phoneTV.setText(R.string.un_set_text);
        }else{
            phoneTV.setText(user.getPhone());
        }
        cityTV.setText(user.getCityString());
        signatureTV.setText(user.getUsignature());
    }
    /**
     * 更换头像
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_user_data_avatar)
    public void updateAvatarClick(View v){
        menuDialog = new BottomIOSMenuDialog.Builder(mReferrence.get())
                .setTitle(getString(R.string.change_avatar_text))
                .addMenu(getString(R.string.choose_from_phone_text), v1 -> {
                    menuDialog.dismiss();
                    startAlbum();
                }).addMenu(getString(R.string.take_one_photo_text), v2 -> {
                    menuDialog.dismiss();
                    startCapture();

                }).create();
        menuDialog.show();
    }

    /**
     * 调用摄像头
     */
    private void startCapture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        TMP_PATH=ImageUtils.createImageUri(mReferrence.get());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, TMP_PATH);
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }

    /**
     * 调用系统相册
     */
    private void startAlbum(){
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, ALBUM_WITH_DATA);
    }




    /**
     * 修改昵称
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_user_data_nick_name)
    public void changeNickNameClick(View v){
        Intent intent=new Intent(this,ReplaceNickNameActivity.class);
        intent.putExtra("nickname",nickNameTV.getText().toString());
        intent.putExtra("updateType","nickname");
        startActivityForResult(intent,UPDATE_NICKNAME);
    }

    @OnClick(R.id.rl_layout_btn_user_data_sex)
    public void changeSexClick(View v){
        menuDialog = new BottomIOSMenuDialog.Builder(mReferrence.get())
                .setTitle(getString(R.string.change_sex_text))
                .addMenu(getString(R.string.male_text), v1 -> updateSexInfo("男")).addMenu(getString(R.string.female_text), v2 -> updateSexInfo("女")).addMenu(getString(R.string.keep_secret_text), v2 -> updateSexInfo("未知"))
                .create();
        menuDialog.show();
    }

    /**
     * 修改性别信息
     */
    private void updateSexInfo(String sex){
        isNetWorkAvailable= CommonUtils.isNetworkAvailable(this);
        if(isNetWorkAvailable){
            List<Map<String,Object>> params=new ArrayList<>();
            Map<String,Object> map=new HashMap<>();
            map.put("updateType","sexInfo");
            map.put("userid", PreferenceUtils.getString("userid",null));
            map.put("newSex",sex);
            params.add(map);
            Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.UPDATE_USER_INFO_URL);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            Log.i(UserDataActivity.class.getSimpleName(),"success");
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.ShortToast(R.string.server_down_text);
                            menuDialog.dismiss();
                        }

                        @Override
                        public void onNext(String s) {
                            if(s.equals("未知")){
                                sexTV.setText("保密");
                                PreferenceUtils.put("sex","保密");
                            }else{
                                sexTV.setText(s);
                                PreferenceUtils.put("sex",s);
                            }
                            ToastUtils.ShortToast(R.string.update_success_text);
                            menuDialog.dismiss();
                        }
                    });
        }else{
            ToastUtils.ShortToast(R.string.no_network_connection);
            menuDialog.dismiss();
        }
    }

    @OnClick(R.id.rl_layout_btn_user_data_mail)
    public void updateEmail(View v){
        Intent intent=new Intent(this,ReplaceNickNameActivity.class);
        intent.putExtra("email",mailTV.getText().toString());
        intent.putExtra("updateType","email");
        startActivityForResult(intent,UPDATE_EMAIL);
    }

    /**
     * 省市修改点击事件
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_user_data_city)
    public void updateCityClick(View v){
         AlertDialog.Builder builder=new AlertDialog.Builder(mReferrence.get());
                View view = LayoutInflater.from(mReferrence.get()).inflate(R.layout.layout_address_dialog, null);
                builder.setView(view);
        LinearLayout addressdialog_linearlayout = (LinearLayout)view.findViewById(R.id.addressdialog_linearlayout);
                final ScrollerNumberPicker provincePicker = (ScrollerNumberPicker)view.findViewById(R.id.province);
                final ScrollerNumberPicker cityPicker = (ScrollerNumberPicker)view.findViewById(R.id.city);
               //final ScrollerNumberPicker counyPicker = (ScrollerNumberPicker)view.findViewById(R.id.couny);
                final AlertDialog dialog = builder.show();
        addressdialog_linearlayout.setOnClickListener(v1->{//确定按钮点击事件
            isNetWorkAvailable=CommonUtils.isNetworkAvailable(mReferrence.get());
            if(isNetWorkAvailable){
                List<Map<String,Object>> params=new ArrayList<>();
                Map<String,Object> map=new HashMap<>();
                map.put("updateType","cityInfo");
                map.put("userid", PreferenceUtils.getString("userid",null));
                map.put("city",cityPicker.getSelectedText());
                map.put("province",provincePicker.getSelectedText());
                params.add(map);
                Observable<String> observable=httpUtils.asyncPostRequest(params, RequestURLs.UPDATE_USER_INFO_URL);
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                                Log.i(UserDataActivity.class.getSimpleName(),"success");
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtils.ShortToast(R.string.server_down_text);
                                dialog.dismiss();
                            }

                            @Override
                            public void onNext(String s) {
                                String newCityStr=provincePicker.getSelectedText()+cityPicker.getSelectedText();
                                cityTV.setText(newCityStr);
                                PreferenceUtils.put("city",newCityStr);
                                ToastUtils.ShortToast(R.string.update_success_text);
                                dialog.dismiss();
                            }
                        });
            }else{
                ToastUtils.ShortToast(R.string.no_network_connection);
                dialog.dismiss();
            }
        });
    }

    /**
     * 个人签名
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_signature)
    public void updateSignature(View v){
        Intent intent=new Intent(mReferrence.get(),ReplaceNickNameActivity.class);
        intent.putExtra("signature",signatureTV.getText().toString());
        intent.putExtra("updateType","signature");
        startActivityForResult(intent,UPDATE_SIGNATURE);
    }

    /**
     * 修改手机号码
     */
    @OnClick(R.id.rl_layout_btn_user_data_phone)
    public void updatePhoneClick(View v){
        Intent intent=new Intent(mReferrence.get(),UpdatePhoneActivity.class);
        startActivityForResult(intent,UPDATE_PHONE);
    }

    /**
     * 更新密码
     * @param v
     */
    @OnClick(R.id.rl_layout_btn_pwd_update)
    public void updatePassword(View v){
        Intent intent=new Intent(mReferrence.get(),UpdatePasswordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK){
            return;
        }
            switch (requestCode){
                case UPDATE_NICKNAME:{//修改昵称
                    String newNickname=data.getStringExtra("newNickname");
                    Intent intent=new Intent();
                    intent.setAction(BroadcastReceiverUtil.HOME_DRAWER_RECEIVER);
                    sendBroadcast(intent);
                    if(null!=newNickname&&!("").equals(newNickname)){
                        nickNameTV.setText(newNickname);
                    }
                    break;
                }
                case UPDATE_EMAIL:{
                    String newMail=data.getStringExtra("newMail");
                    Intent intent=new Intent();
                    intent.setAction(BroadcastReceiverUtil.HOME_DRAWER_RECEIVER);
                    sendBroadcast(intent);
                    if(null!=newMail&&!("").equals(newMail)){
                        mailTV.setText(newMail);
                    }
                    break;
                }
                case UPDATE_SIGNATURE:{
                    String newSignature=data.getStringExtra("newSignature");
                    Intent intent=new Intent();
                    intent.setAction(BroadcastReceiverUtil.HOME_DRAWER_RECEIVER);
                    sendBroadcast(intent);
                    if(null!=newSignature&&!("").equals(newSignature)){
                        signatureTV.setText(newSignature);
                    }
                    break;
                }
                case CAMERA_WITH_DATA:{
                    // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                    startCropImageActivity(FileUtil.getRealFilePath(mReferrence.get(),TMP_PATH));

                    break;
                }
                case CROP_RESULT_CODE:{//裁剪图片完成之后回调
                    isNetWorkAvailable=CommonUtils.isNetworkAvailable(mReferrence.get());
                    if(isNetWorkAvailable){
                    String path = data.getStringExtra(ClipImageActivity.RESULT_PATH);
                    List<Map<String,Object>> params=new ArrayList<>();
                    Map<String,Object> map=new HashMap<>();
                    map.put("userid",user.getUserid());
                    params.add(map);
                    Observable<String> observable=httpUtils.uploadMutipleImages(params,new File(path),RequestURLs.UPLOAD_HEADER_IMAGE_MOBILE,"avatar_file");
                    observable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onCompleted() {
                                    Log.i(TAG,"上传头像方法执行完毕");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    ToastUtils.ShortToast(R.string.server_down_text);
                                }

                                @Override
                                public void onNext(String s) {
                                    PreferenceUtils.put("headerImage",s);
                                        //修改头像后传递广播修改homeUI
                                        Intent intent=new Intent();
                                        intent.setAction(BroadcastReceiverUtil.HOME_DRAWER_RECEIVER);
                                        sendBroadcast(intent);
                                        ToastUtils.ShortToast(R.string.upload_image_text);
                                }
                            });
                }else{
                        ToastUtils.ShortToast(R.string.no_network_connection);
                    }
                    break;
        }
                case ALBUM_WITH_DATA:{
                    TMP_PATH=data.getData();
                    startCropImageActivity(FileUtil.getRealFilePath(mReferrence.get(),TMP_PATH));// 开始对图片进行裁剪处理
                    break;
                }
                case UPDATE_PHONE:{
                    String newPhone=data.getStringExtra("newPhone");
                    phoneTV.setText(newPhone);
                    break;
                }
    }
    }



    // 裁剪图片的Activity
    private void startCropImageActivity(String path) {
        ClipImageActivity.startActivity(this, path, CROP_RESULT_CODE);
    }

    @Override
    public void initToolBar() {
        mToolBar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        mToolBar.setTitle(getText(R.string.personal_doc_text));
        mToolBar.setNavigationOnClickListener(v ->finish());
    }
}
