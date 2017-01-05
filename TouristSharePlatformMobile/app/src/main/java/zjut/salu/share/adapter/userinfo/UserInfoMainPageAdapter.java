package zjut.salu.share.adapter.userinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.model.NineImage;
import zjut.salu.share.model.SpaceBean;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.widget.MyPullToRefreshView;
import zjut.salu.share.widget.NineGridLayout;

/**
 * Created by Salu on 2016/11/19.
 */

public class UserInfoMainPageAdapter extends BaseAdapter {
    private List<SpaceBean> list=null;
    private LayoutInflater inflater=null;
    private ImageLoader imageLoader=null;

    public UserInfoMainPageAdapter(List<SpaceBean> list, Context context) {
        this.list = list;
        inflater=LayoutInflater.from(context);
        imageLoader=ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        @Bind(R.id.iv_avatar_main_page)ImageView avatarIV;
        @Bind(R.id.tv_username_main_page)TextView usernameTV;
        @Bind(R.id.tv_time_main_page)TextView timeTV;
        @Bind(R.id.tv_content_main_page)TextView contentTV;
        @Bind(R.id.nl_image_main_page)NineGridLayout nineGridLayout;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_user_info_main_page,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        SpaceBean bean=list.get(position);
        imageLoader.displayImage(RequestURLs.MAIN_URL+bean.getUser().getHeaderimage(),new ImageViewAware(viewHolder.avatarIV), ImageLoaderOptionUtils.getAvatarOptions());
        viewHolder.usernameTV.setText(bean.getUser().getUsername());
        viewHolder.timeTV.setText(StringUtils.getTimeDiff(bean.getTime().getTime()));
        viewHolder.contentTV.setText(bean.getDescription());
        List<NineImage> imgs=new ArrayList<>();
        if(bean.getImageList().size()>0){
            for(int j=0;j<bean.getImageList().size();j++){
                NineImage i=new NineImage();
                i.setUrl(bean.getImageList().get(j));
                i.setPosition(j+"");
                imgs.add(i);
            }
        }
        viewHolder.nineGridLayout.setImagesData(imgs);
        return convertView;
    }
}
