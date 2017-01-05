package zjut.salu.share.fragment.product;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import zjut.salu.share.R;
import zjut.salu.share.utils.ImageLoaderUtils;

/**大图浏览fragment,图片可放大缩小
 * Created by Salu on 2016/11/15.
 */

@SuppressLint("ValidFragment")
public class BigPictureFragment extends Fragment {
    private String imgSrc;
    private ImageLoader imageLoader=null;
    private DisplayImageOptions options=null;

    public BigPictureFragment(String imgSrc){
        this.imgSrc=imgSrc;
        imageLoader=ImageLoader.getInstance();
        options= ImageLoaderUtils.getImageOptions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=LayoutInflater.from(getActivity()).inflate(R.layout.scale_pic_item, null);
        initView(view);
        return view;
    }

    private void initView(View view){
        ImageView imageView=(ImageView) view.findViewById(R.id.scale_pic_item);
        imageLoader.displayImage(imgSrc,imageView,options);
    }
}
