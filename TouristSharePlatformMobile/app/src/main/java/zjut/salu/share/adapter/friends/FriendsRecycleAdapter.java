package zjut.salu.share.adapter.friends;

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
import zjut.salu.share.model.TripUser;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;

/**朋友列表
 * Created by Salu on 2016/12/8.
 */

public class FriendsRecycleAdapter extends AbsRecyclerViewAdapter {
    private List<TripUser> list;
    private ImageLoader imageLoader;

    public FriendsRecycleAdapter(RecyclerView recyclerView, List<TripUser> list, ImageLoader imageLoader) {
        super(recyclerView);
        this.list=list;
        this.imageLoader=imageLoader;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            TripUser user=list.get(position);
            DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
            imageLoader.displayImage(RequestURLs.MAIN_URL+user.getHeaderimage(),itemViewHolder.imageView,options);
            itemViewHolder.nameTV.setText(user.getUsername());
            if(null==user.getUsignature()||("").equals(user.getUsignature())){
                itemViewHolder.signatureTV.setText(R.string.default_signature_text);
            }else{
                itemViewHolder.signatureTV.setText(user.getUsignature());
            }
            int focused=user.getFocused();
            if(focused==0){//已关注
                itemViewHolder.focusIV.setImageResource(R.drawable.guanzhu_icon);
                itemViewHolder.focusStatusTV.setText(R.string.focused_text);
            }else if(focused==1){//未关注
                itemViewHolder.focusIV.setImageResource(R.drawable.unfocus_icon);
                itemViewHolder.focusStatusTV.setText(R.string.focus_him_text);
            }
            itemViewHolder.focusIV.setOnClickListener(v->{//关注按钮点击事件
                if(itemViewHolder.focusStatusTV.getText().equals(CuteTouristShareConfig.mInstance.getText(R.string.focus_him_text))){
                    //TODO:关注该用户
                    itemViewHolder.focusIV.setImageResource(R.drawable.guanzhu_icon);
                    itemViewHolder.focusStatusTV.setText(R.string.focused_text);
                }else if(itemViewHolder.focusStatusTV.getText().equals(CuteTouristShareConfig.mInstance.getText(R.string.focused_text))){
                    //TODO:取消关注该用户
                    itemViewHolder.focusIV.setImageResource(R.drawable.unfocus_icon);
                    itemViewHolder.focusStatusTV.setText(R.string.focus_him_text);
                }
            });
        }
        super.onBindViewHolder(holder, position);
    }



    private class ItemViewHolder extends ClickableViewHolder{
        ImageView imageView;
        TextView nameTV;
        TextView signatureTV;
        ImageView focusIV;
        TextView focusStatusTV;
        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView=$(R.id.cciv_friend_avatar);//头像
            nameTV=$(R.id.tv_friend_name);//名字
            signatureTV=$(R.id.tv_friend_signature);//个人签名
            focusIV=$(R.id.iv_guanzhu_icon);//关注状态图标
            focusStatusTV=$(R.id.tv_focus_status);//关注状态文字  已关注  未关注
        }
    }

}
