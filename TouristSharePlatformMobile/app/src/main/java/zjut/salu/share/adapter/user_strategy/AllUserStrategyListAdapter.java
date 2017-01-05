package zjut.salu.share.adapter.user_strategy;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.model.UserStrategy;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.widget.CommonCircleImageView;

/**所有用户攻略的列表适配器
 * Created by Alan on 2016/10/29.
 */

public class AllUserStrategyListAdapter extends BaseAdapter{
    private Context context=null;
    private List<UserStrategy> list=null;
    private LayoutInflater inflater=null;
    private SimpleDateFormat format=null;
    private ListView listView=null;
    private DisplayImageOptions options=null;
    private ImageLoader imageLoader=null;
    public AllUserStrategyListAdapter(ListView listView,Context context, List<UserStrategy> list) {
        this.context=context;
        this.list=list;
        inflater=LayoutInflater.from(this.context);
        format=new SimpleDateFormat("yyyy-MM-dd");
        this.listView=listView;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.feedback_cartoon)
                .showImageOnFail(R.drawable.feedback_cartoon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader=ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    static class ViewHolder{
        @Bind(R.id.tv_hint_boutique_strategy)TextView boutiqueTV;//精品用户标签
        @Bind(R.id.tv_hint_high_quality_strategy)TextView highQualityTV;//优质精华标签
        @Bind(R.id.iv_bac_cover_user_strategy_list)ImageView coverIV;//封面
        @Bind(R.id.cciv_user_avatar_user_strategy_list)CommonCircleImageView userAvatarIV;//头像
        @Bind(R.id.tv_user_avatar_user_strategy_list)TextView strategyTitleTV;//标题
        @Bind(R.id.tv_watched_user_strategy_list)TextView watchedTV;//阅读人数
        @Bind(R.id.tv_time_user_strategy_list)TextView timeTV;//发表时间
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_user_strategy,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        UserStrategy strategy=list.get(position);
        TripUser user=strategy.getTripUser();
        if(user.getIsmaster()==1){//如果不是精品用户
            viewHolder.boutiqueTV.setVisibility(View.GONE);
        }else{
            if(viewHolder.boutiqueTV.getVisibility()==View.GONE){viewHolder.boutiqueTV.setVisibility(View.VISIBLE);}
        }
        if(strategy.getIsesense()==1){//如果不是优质精华
            viewHolder.highQualityTV.setVisibility(View.GONE);
        }else{
            if(viewHolder.highQualityTV.getVisibility()==View.GONE){viewHolder.highQualityTV.setVisibility(View.VISIBLE);}
        }
        viewHolder.strategyTitleTV.setText(strategy.getUstrategyname());
        viewHolder.watchedTV.setText(String.valueOf(strategy.getUclickednum()));
        viewHolder.timeTV.setText(format.format(strategy.getUstrategydate()));
        imageLoader.displayImage(RequestURLs.MAIN_URL+strategy.getCoverImage(),viewHolder.coverIV,options,new ImageLoaderUtils.ImageLoadingListenerImpl());
        imageLoader.displayImage(RequestURLs.MAIN_URL+user.getHeaderimage(),viewHolder.userAvatarIV,options,new ImageLoaderUtils.ImageLoadingListenerImpl());
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,false,true));
        return convertView;
    }

}
