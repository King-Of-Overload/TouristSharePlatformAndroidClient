package zjut.salu.share.adapter.albums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.config.CuteTouristShareConfig;
import zjut.salu.share.model.photo.UserAlbums;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;

/**相册区适配器
 * Created by Salu on 2016/11/30.
 */

public class AlbumListAdapter extends BaseAdapter {
    private List<UserAlbums> list=null;
    private LayoutInflater inflater=null;
    private ImageLoader imageLoader=null;
    public AlbumListAdapter(List<UserAlbums> list, Context context,ImageLoader imageLoader) {
        this.list=list;
        inflater=LayoutInflater.from(context);
        this.imageLoader=imageLoader;
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
        @Bind(R.id.iv_album_item)ImageView coverIV;
        @Bind(R.id.tv_title_album)TextView titleTV;
        @Bind(R.id.tv_author_album)TextView authorTV;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_album,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        UserAlbums albums=list.get(position);
        DisplayImageOptions options=ImageLoaderOptionUtils.getImgOptions();
        imageLoader.displayImage(RequestURLs.MAIN_URL+albums.getCoverImage(),new ImageViewAware(viewHolder.coverIV),options,new ImageSize(120,120),null,null);
        viewHolder.titleTV.setText(albums.getAlbumname());
        viewHolder.authorTV.setText(String.valueOf(CuteTouristShareConfig.mInstance.getString(R.string.up_text)+albums.getTripUser().getUsername()));
        return convertView;
    }
}
