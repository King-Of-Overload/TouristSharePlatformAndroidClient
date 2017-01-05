package zjut.salu.share.adapter.best;

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
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.widget.CommonCircleImageView;

/**精选数据适配器
 * Created by Salu on 2016/12/13.
 */

public class BestStrategyRecycleAdapter extends AbsRecyclerViewAdapter{
    private List<UserStrategy> list=null;
    private ImageLoader imageLoader=null;
    public BestStrategyRecycleAdapter(RecyclerView recyclerView, List<UserStrategy> list, ImageLoader imageLoader) {
        super(recyclerView);
        this.list=list;
        this.imageLoader=imageLoader;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_best_strategy,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            UserStrategy strategy=list.get(position);
            DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
            imageLoader.displayImage(RequestURLs.MAIN_URL+strategy.getCoverImage(),itemViewHolder.coverIV,options);
            itemViewHolder.titleTV.setText(strategy.getUstrategyname());
            itemViewHolder.timeTV.setText(StringUtils.formatDate(strategy.getUstrategydate(),"yyyy-MM-dd"));
            imageLoader.displayImage(RequestURLs.MAIN_URL+strategy.getTripUser().getHeaderimage(),itemViewHolder.avatarIV,options);
            itemViewHolder.authorTV.setText(strategy.getTripUser().getUsername());
        }
    }

    private class ItemViewHolder extends ClickableViewHolder{
        ImageView coverIV;
        TextView titleTV;
        TextView timeTV;
        CommonCircleImageView avatarIV;
        TextView authorTV;
         ItemViewHolder(View itemView) {
            super(itemView);
             coverIV=$(R.id.iv_cover_best_strategy);
             titleTV=$(R.id.tv_title_best_strategy);
             timeTV=$(R.id.tv_time_best_strategy);
             avatarIV=$(R.id.cciv_avatar_best_strategy);
             authorTV=$(R.id.tv_author_best_strategy);
        }
    }

}
