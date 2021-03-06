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
import zjut.salu.share.model.skillacademy.SkillAcademy;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.NumberUtil;
import zjut.salu.share.utils.RequestURLs;

/**主页技法学院适配器
 * Created by Salu on 2017/2/21.
 */

public class IndexSkillAcademyRecycleAdapter extends AbsRecyclerViewAdapter{
    private List<SkillAcademy> academyList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public IndexSkillAcademyRecycleAdapter(RecyclerView recyclerView,List<SkillAcademy> academyList,ImageLoader imageLoader) {
        super(recyclerView);
        this.academyList=academyList;
        this.imageLoader=imageLoader;
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
            SkillAcademy academy=academyList.get(position);
            itemViewHolder.iv_playIV.setVisibility(View.GONE);
            itemViewHolder.item_playTV.setVisibility(View.GONE);
            imageLoader.displayImage(RequestURLs.MAIN_URL+academy.getCoverImage(),itemViewHolder.coverIV,options);
            itemViewHolder.titleTV.setText(academy.getSkilltitle());
            itemViewHolder.authorTV.setText(academy.getUser().getUsername());
            itemViewHolder.item_watchTV.setText(NumberUtil.converString(academy.getClickednum()));
            itemViewHolder.clickNumTV.setText(NumberUtil.converString(28));
        }
    }

    @Override
    public int getItemCount() {
        return academyList.size();
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
