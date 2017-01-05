package zjut.salu.share.utils;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youth.banner.loader.ImageLoader;

import zjut.salu.share.R;

/**banner图片加载类
 * Created by Alan on 2016/11/2.
 */

public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //Glide.with(context).load(path).into(imageView);
        Glide.with(context)//头像加载
                .load(path)
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.ico_user_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}
