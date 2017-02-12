package zjut.salu.share.adapter.lightstrategy;

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

/**小视频主页适配器
 * Created by Salu on 2017/2/11.
 */

public class BanggumeIndexRecycleAdapter extends AbsRecyclerViewAdapter {
    private List<Banggume> banggumeList;
    private ImageLoader imageLoader;

    public BanggumeIndexRecycleAdapter(RecyclerView recyclerView,List<Banggume> banggumeList,ImageLoader imageLoader) {
        super(recyclerView);
        this.banggumeList=banggumeList;
        this.imageLoader=imageLoader;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_region_recommend_card_item,parent,false);
        return  new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            Banggume banggume=banggumeList.get(position);
            DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
            imageLoader.displayImage(RequestURLs.MAIN_URL+banggume.getBangumecover(),itemViewHolder.coverIV,options);
            itemViewHolder.titleTV.setText(banggume.getBangumename());
            itemViewHolder.clickNumTV.setText(NumberUtil.converString(banggume.getClicknum()));
            itemViewHolder.commentTV.setText("66");//TODO:评论数，需要修改
        }
    }

    @Override
    public int getItemCount() {
        return banggumeList.size();
    }

    private class ItemViewHolder extends ClickableViewHolder{
        ImageView coverIV;
        TextView titleTV;
        TextView clickNumTV;
        TextView commentTV;
        ItemViewHolder(View itemView) {
            super(itemView);
            coverIV=$(R.id.item_img);
            titleTV=$(R.id.item_title);
            clickNumTV=$(R.id.item_play);
            commentTV=$(R.id.item_review);
        }
    }
}
