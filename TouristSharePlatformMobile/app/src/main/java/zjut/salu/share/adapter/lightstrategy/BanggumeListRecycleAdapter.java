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
import zjut.salu.share.config.CuteTouristShareConfig;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.NumberUtil;
import zjut.salu.share.utils.RequestURLs;

/**小视频列表显示适配器
 * Created by Salu on 2017/2/10.
 */

public class BanggumeListRecycleAdapter extends AbsRecyclerViewAdapter{
    private List<Banggume> banggumeList;
    private ImageLoader imageLoader;

    public BanggumeListRecycleAdapter(RecyclerView recyclerView,List<Banggume> banggumeList,ImageLoader imageLoader) {
        super(recyclerView);
        this.banggumeList=banggumeList;
        this.imageLoader=imageLoader;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banggume_list,parent,false);
        return new ItemViewHolder(view);
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
            itemViewHolder.upNameTV.setText(banggume.getUser().getUsername());
            String tag=CuteTouristShareConfig.mInstance.getString(R.string.relate_tags_text)+banggume.getBangumetags();
            itemViewHolder.tagTV.setText(tag);
            itemViewHolder.clickNumTV.setText(NumberUtil.converString(banggume.getClicknum()));
            itemViewHolder.commentTV.setText(NumberUtil.converString(66));
        }
    }


    @Override
    public int getItemCount() {
        return banggumeList.size();
    }



    private class ItemViewHolder extends ClickableViewHolder{
        ImageView coverIV;
        TextView titleTV;
        TextView upNameTV;
        TextView tagTV;
        TextView clickNumTV;
        TextView commentTV;
        ItemViewHolder(View itemView) {
            super(itemView);
            coverIV=$(R.id.iv_banggume);
            titleTV=$(R.id.tv_title);
            upNameTV=$(R.id.tv_up);
            tagTV=$(R.id.tv_tag_banggume);
            clickNumTV=$(R.id.tv_click_num);
            commentTV=$(R.id.tv_comment_num);
        }
    }

}
