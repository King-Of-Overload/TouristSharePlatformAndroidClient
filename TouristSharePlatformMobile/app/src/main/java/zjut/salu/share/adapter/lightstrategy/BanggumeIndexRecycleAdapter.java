package zjut.salu.share.adapter.lightstrategy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import zjut.salu.share.R;
import zjut.salu.share.adapter.helper.AbsRecyclerViewAdapter;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;

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
