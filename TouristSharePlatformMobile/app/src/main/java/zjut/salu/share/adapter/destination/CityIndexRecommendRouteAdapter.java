package zjut.salu.share.adapter.destination;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.model.Routes;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.widget.HorizontalListView;

/**城市首页推荐路线适配器
 * Created by Salu on 2017/1/21.
 */

public class CityIndexRecommendRouteAdapter extends BaseAdapter {
    private List<Routes> list;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public CityIndexRecommendRouteAdapter(Context context, List<Routes> list, ImageLoader imageLoader) {
        this.list = list;
        inflater=LayoutInflater.from(context);
        this.imageLoader=imageLoader;
        options= ImageLoaderOptionUtils.getImgOptions();
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

    class ViewHolder{
        @Bind(R.id.iv_route_cover)ImageView coverIV;
        @Bind(R.id.tv_route_name)TextView nameTV;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_city_index_recommend_route,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Routes route=list.get(position);
        viewHolder.nameTV.setText(route.getRoutename());
        TextPaint paint=viewHolder.nameTV.getPaint();
        paint.setFakeBoldText(true);
        imageLoader.displayImage(RequestURLs.MAIN_URL+route.getRoutecover(),viewHolder.coverIV,options,new ImageLoaderUtils.ImageLoadingListenerImpl());
        return convertView;
    }
}
