package zjut.salu.share.adapter.user.download;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

import zjut.salu.share.R;
import zjut.salu.share.adapter.helper.AbsRecyclerViewAdapter;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.utils.FileUtil;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;

/**缓存完成界面
 * Created by Salu on 2017/2/17.
 */

public class OffLineDownloadCompleteAdapter extends AbsRecyclerViewAdapter {
    private List<Banggume> banggumeList;
    private ImageLoader imageLoader;
    public OffLineDownloadCompleteAdapter(RecyclerView recyclerView,List<Banggume> banggumeList,ImageLoader imageLoader) {
        super(recyclerView);
        this.banggumeList=banggumeList;
        this.imageLoader=imageLoader;
    }


    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offline_download_complete,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return banggumeList.size();
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ItemViewHolder){
            ItemViewHolder viewHolder= (ItemViewHolder) holder;
            Banggume banggume=banggumeList.get(position);
            DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
            imageLoader.displayImage(RequestURLs.MAIN_URL+banggume.getBangumecover(),viewHolder.coverIV,options);
            viewHolder.nameTV.setText(banggume.getBangumename());
            try {
                viewHolder.sizeTV.setText(FileUtil.FormetFileSize(FileUtil.getFileSize(new File(banggume.getPhonestoragepath()))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ItemViewHolder extends ClickableViewHolder{
        ImageView coverIV;
        TextView nameTV;
        TextView sizeTV;
        ItemViewHolder(View itemView) {
            super(itemView);
            coverIV=$(R.id.iv_cover);
            nameTV=$(R.id.tv_name);
            sizeTV=$(R.id.tv_size);
        }
    }
}
