package zjut.salu.share.activity.qrcode;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.activity.qrcode.libzxing.encoding.EncodingUtils;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.PreferenceUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.widget.CommonCircleImageView;

/**
 * 生成二维码控制层
 * @author Salu
 */
public class MyQRCodeActivity extends RxBaseActivity {
    @Bind(R.id.cciv_avatar)CommonCircleImageView avatarIV;
    @Bind(R.id.tv_name)TextView nameTV;
    @Bind(R.id.tv_mail)TextView mailTV;
    @Bind(R.id.iv_qrcode)ImageView qrCodeIV;
    @Bind(R.id.toolbar)Toolbar toolbar;

    public static void launch(Activity activity){
        Intent mIntent = new Intent(activity, MyQRCodeActivity.class);
        activity.startActivity(mIntent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_qrcode;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        WeakReference<Activity> mReference = new WeakReference<>(this);
        TripUser user=PreferenceUtils.acquireCurrentUser();
        ImageLoaderUtils.loadAvatarWithURL(mReference.get(), RequestURLs.MAIN_URL+user.getHeaderimage(), DiskCacheStrategy.NONE,avatarIV);
            Glide.with(mReference.get()).load(RequestURLs.MAIN_URL+user.getHeaderimage()).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    nameTV.setText(user.getUsername());
                    TextPaint paint=nameTV.getPaint();
                    paint.setFakeBoldText(true);
                    Drawable drawable;
                    if(user.getSex().equals("男")){
                        drawable=getResources().getDrawable(R.drawable.ic_user_male);
                    }else if(user.getSex().equals("女")){
                        drawable=getResources().getDrawable(R.drawable.ic_user_female);
                    }else{
                        drawable=getResources().getDrawable(R.drawable.ic_user_gay_border);
                    }
                    nameTV.setCompoundDrawablesWithIntrinsicBounds(null,null, drawable,null);
                    mailTV.setText(user.getUseremail());
                    String userid=user.getUserid();
                    if(!userid.equals("")){
                        Bitmap qrCodeBitmap= EncodingUtils.createQRCode(userid,220,220,resource);
                        qrCodeIV.setImageBitmap(qrCodeBitmap);
                    }
                }
            });
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.my_qr_code_text);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
