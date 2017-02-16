package zjut.salu.share.adapter.destination;

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
import zjut.salu.share.model.local.Cuision;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;

/**菜品列表适配器
 * Created by Alan-Mac on 2017/02/16.
 */

public class CuisionRecycleAdapter extends AbsRecyclerViewAdapter {
    private List<Cuision> cuisions;
    private ImageLoader imageLoader;
     public CuisionRecycleAdapter(RecyclerView recyclerView,List<Cuision> cuisions,ImageLoader imageLoader) {
        super(recyclerView);
         this.cuisions=cuisions;
         this.imageLoader=imageLoader;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuision,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            Cuision cuision=cuisions.get(position);
            DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
            imageLoader.displayImage(RequestURLs.MAIN_URL+cuision.getCuisionimage(),itemViewHolder.coverIV,options);
            itemViewHolder.nameTV.setText(cuision.getCuisionname());
            String price=cuision.getCuisionprice()+cuision.getCuisionpricecurrency()+"(约"+cuision.getCuisionforeignprice()+
                    cuision.getCuisionforeigncurrency()+")";
            itemViewHolder.priceTV.setText(price);
            itemViewHolder.descTV.setText(cuision.getCuisiondescription());
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return cuisions.size();
    }

    private class ItemViewHolder extends ClickableViewHolder{
        ImageView coverIV;
        TextView nameTV;
        TextView priceTV;
        TextView descTV;
        public ItemViewHolder(View itemView) {
            super(itemView);
            coverIV=$(R.id.iv_cover);
            nameTV=$(R.id.tv_name);
            priceTV=$(R.id.tv_price);
            descTV=$(R.id.tv_desc);
        }
    }
}
