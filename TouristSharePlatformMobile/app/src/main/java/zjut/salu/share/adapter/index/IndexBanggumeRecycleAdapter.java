package zjut.salu.share.adapter.index;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import zjut.salu.share.R;
import zjut.salu.share.adapter.helper.AbsRecyclerViewAdapter;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.NumberUtil;
import zjut.salu.share.utils.RequestURLs;

/**主页轻游记适配器
 * Created by Salu on 2017/2/21.
 */

public class IndexBanggumeRecycleAdapter extends AbsRecyclerViewAdapter {
    private List<Banggume> banggumeList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public IndexBanggumeRecycleAdapter(RecyclerView recyclerView,List<Banggume> banggumeList,ImageLoader imageLoader) {
        super(recyclerView);
        this.imageLoader=imageLoader;
        this.banggumeList=banggumeList;
        options= ImageLoaderOptionUtils.getImgOptions();
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_index_common,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            Banggume banggume=banggumeList.get(position);
            itemViewHolder.iv_watchIV.setVisibility(View.GONE);
            itemViewHolder.item_watchTV.setVisibility(View.GONE);
            imageLoader.displayImage(RequestURLs.MAIN_URL+banggume.getBangumecover(),itemViewHolder.coverIV,options);
            itemViewHolder.titleTV.setText(banggume.getBangumename());
            itemViewHolder.authorTV.setText(banggume.getUser().getUsername());
            itemViewHolder.item_playTV.setText(NumberUtil.converString(banggume.getClicknum()));
            itemViewHolder.clickNumTV.setText(NumberUtil.converString(28));
        }
    }

    @Override
    public int getItemCount() {
        return banggumeList.size();
    }

    private class ItemViewHolder extends ClickableViewHolder{
        ImageView coverIV;
        TextView titleTV;
        TextView authorTV;

        ImageView iv_playIV;
        TextView item_playTV;

        ImageView iv_watchIV;
        TextView item_watchTV;

        TextView clickNumTV;
        ItemViewHolder(View itemView) {
            super(itemView);
            coverIV=$(R.id.item_img);
            titleTV=$(R.id.item_title);
            authorTV=$(R.id.tv_author);
            iv_playIV=$(R.id.iv_play);
            item_playTV=$(R.id.item_play);
            iv_watchIV=$(R.id.iv_watch);
            item_watchTV=$(R.id.item_watch);
            clickNumTV=$(R.id.item_review);
        }
    }
}
