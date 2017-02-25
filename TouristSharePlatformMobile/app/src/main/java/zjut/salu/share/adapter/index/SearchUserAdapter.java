package zjut.salu.share.adapter.index;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.model.TripUser;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.widget.CommonCircleImageView;

/**搜索界面用户结果adapter
 * Created by Salu on 2017/2/23.
 */

public class SearchUserAdapter extends BaseAdapter {
    private ImageLoader imageLoader;
    private List<TripUser> userList;
    private LayoutInflater inflater;
    private DisplayImageOptions options;

    public SearchUserAdapter(ImageLoader imageLoader, List<TripUser> userList, Context context) {
        this.imageLoader = imageLoader;
        this.userList = userList;
        inflater=LayoutInflater.from(context);
        options= ImageLoaderOptionUtils.getImgOptions();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(null==convertView){
            convertView=inflater.inflate(R.layout.item_search_grid,null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        TripUser user=userList.get(position);
        imageLoader.displayImage(RequestURLs.MAIN_URL+user.getHeaderimage(),holder.coverIV,options);
        holder.nameTV.setText(user.getUsername());
        return convertView;
    }


    class ViewHolder{
        @Bind(R.id.iv_grid_item)CommonCircleImageView coverIV;
        @Bind(R.id.tv_grid_item)TextView nameTV;
        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
