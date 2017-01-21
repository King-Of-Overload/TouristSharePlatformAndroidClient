package zjut.salu.share.adapter.recommend_route;

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
import zjut.salu.share.model.Routes;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;

/**推荐路线列表显示适配器
 * Created by Salu on 2017/1/16.
 */

public class RecommendRouteRecycleAdapter extends AbsRecyclerViewAdapter {
    private List<Routes> routes;
    private ImageLoader imageLoader;

    public RecommendRouteRecycleAdapter(RecyclerView recyclerView,List<Routes> routes,ImageLoader imageLoader) {
        super(recyclerView);
        this.routes=routes;
        this.imageLoader=imageLoader;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_route,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if(holder instanceof  ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            Routes route=routes.get(position);
            DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
            imageLoader.displayImage(RequestURLs.MAIN_URL+route.getRoutecover(),itemViewHolder.imageView,options);
            itemViewHolder.nameTV.setText(route.getRoutename());
        }
        super.onBindViewHolder(holder, position);
    }

    private class ItemViewHolder extends ClickableViewHolder{
        ImageView imageView;
        TextView nameTV;
        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView=$(R.id.iv_recommend_route);
            nameTV=$(R.id.tv_recommend_route);
        }
    }

}
