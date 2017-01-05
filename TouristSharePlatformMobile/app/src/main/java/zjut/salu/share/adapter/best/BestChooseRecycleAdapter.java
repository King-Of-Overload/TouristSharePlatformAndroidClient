package zjut.salu.share.adapter.best;

import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import zjut.salu.share.R;
import zjut.salu.share.adapter.helper.AbsRecyclerViewAdapter;
import zjut.salu.share.model.BestChoose;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;

/**精选数据适配器
 * Created by Salu on 2016/12/13.
 */

public class BestChooseRecycleAdapter extends AbsRecyclerViewAdapter{
    private List<BestChoose> list=null;
    private ImageLoader imageLoader=null;
    public BestChooseRecycleAdapter(RecyclerView recyclerView,List<BestChoose> list, ImageLoader imageLoader) {
        super(recyclerView);
        this.list=list;
        this.imageLoader=imageLoader;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_child_best_choose,parent,false);
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
            BestChoose bestChoose=list.get(position);
            DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
            imageLoader.displayImage(RequestURLs.MAIN_URL+bestChoose.getBestcover(),itemViewHolder.imageView,options);
        }
    }

    private class ItemViewHolder extends ClickableViewHolder{
        ImageView imageView;
         ItemViewHolder(View itemView) {
            super(itemView);
             imageView=$(R.id.iv_best_choose);
        }
    }

}
