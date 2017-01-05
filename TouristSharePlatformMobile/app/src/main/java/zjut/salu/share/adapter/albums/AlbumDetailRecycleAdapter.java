package zjut.salu.share.adapter.albums;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import zjut.salu.share.adapter.helper.AbsRecyclerViewAdapter;
import zjut.salu.share.model.UserPhotos;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;

/**相册详情适配器
 * Created by Salu on 2016/12/1.
 */

public class AlbumDetailRecycleAdapter extends AbsRecyclerViewAdapter {
    private List<UserPhotos> list=null;
    private ImageLoader imageLoader=null;

    public AlbumDetailRecycleAdapter(RecyclerView recyclerView, List<UserPhotos> list, ImageLoader imageLoader) {
        super(recyclerView);
        this.list = list;
        this.imageLoader = imageLoader;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail,parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            UserPhotos photos=list.get(position);
            DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
            imageLoader.displayImage(RequestURLs.MAIN_URL+photos.getPhotourl(),new ImageViewAware(itemViewHolder.imageView),options,new ImageSize(260,260),null,null);
        }
        super.onBindViewHolder(holder, position);
    }

    private class ItemViewHolder extends ClickableViewHolder
    {
        ImageView imageView;
         ItemViewHolder(View itemView)
        {
            super(itemView);
            imageView = $(R.id.iv_item_img);
        }
    }


}
