package zjut.salu.share.adapter.favorite;

import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
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
import zjut.salu.share.model.user.UserFavorite;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;

/**用户收藏适配器
 * Created by Alan-Mac on 2017/02/16.
 */

public class UserFavoriteAdapter extends AbsRecyclerViewAdapter {
    private List<UserFavorite> favorites;
    private ImageLoader imageLoader;
    public UserFavoriteAdapter(RecyclerView recyclerView, List<UserFavorite> favorites, ImageLoader imageLoader) {
        super(recyclerView);
        this.favorites=favorites;
        this.imageLoader=imageLoader;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_favorite,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            UserFavorite favorite=favorites.get(position);
            DisplayImageOptions options=ImageLoaderOptionUtils.getImgOptions();
            imageLoader.displayImage(RequestURLs.MAIN_URL+favorite.getFavcover(),itemViewHolder.coverIV,options);
            itemViewHolder.nameTV.setText(favorite.getFavname());
            TextPaint paint=itemViewHolder.nameTV.getPaint();
            paint.setFakeBoldText(true);
            String type=favorite.getType();
            String category=CuteTouristShareConfig.mInstance.getString(R.string.category_text);
            switch (type){
                case "banggume":{
                    category+=CuteTouristShareConfig.mInstance.getString(R.string.light_strategy_banggume_text);
                    break;
                }
                case "strategy":{
                    category+=CuteTouristShareConfig.mInstance.getString(R.string.strategy_text);
                    break;
                }
                case "album":{
                    category+=CuteTouristShareConfig.mInstance.getString(R.string.trip_album_text);
                    break;
                }
                case "postcard":{
                    category+=CuteTouristShareConfig.mInstance.getString(R.string.love_post_card_text);
                    break;
                }
                case "skillacademy":{
                    category+=CuteTouristShareConfig.mInstance.getString(R.string.index_skills_text);
                    break;
                }
            }
            itemViewHolder.categoryTV.setText(category);
        }
        super.onBindViewHolder(holder, position);
    }

    private class ItemViewHolder extends ClickableViewHolder{
        ImageView coverIV;
        TextView nameTV;
        TextView categoryTV;
        public ItemViewHolder(View itemView) {
            super(itemView);
            coverIV=$(R.id.iv_cover);
            nameTV=$(R.id.tv_name);
            categoryTV=$(R.id.tv_desc);
        }
    }

}
