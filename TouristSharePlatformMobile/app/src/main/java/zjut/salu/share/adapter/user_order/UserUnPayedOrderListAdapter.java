package zjut.salu.share.adapter.user_order;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.math.BigDecimal;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.model.OrderItem;
import zjut.salu.share.utils.RequestURLs;

/**用户未付款的订单适配器
 * Created by Alan on 2016/10/28.
 */

public class UserUnPayedOrderListAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater inflater=null;
    private List<OrderItem> dataList=null;
    private ListView listView=null;
    private DisplayImageOptions options=null;
    private ImageLoader imageLoader=null;
    public UserUnPayedOrderListAdapter(Context context, List<OrderItem> dataList, ListView listView) {
        this.context = context;
        inflater=LayoutInflater.from(this.context);
        this.dataList = dataList;
        this.listView=listView;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.feedback_cartoon)
                .showImageOnFail(R.drawable.feedback_cartoon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader =ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        @Bind(R.id.tv_un_payed_oid)TextView oidTV;
        @Bind(R.id.tv_un_payed_odate)TextView o_dateTV;
        @Bind(R.id.iv_un_payed_order_cover)ImageView coverImageIV;
        @Bind(R.id.tv_un_payed_order_pname)TextView p_nameTV;
        @Bind(R.id.tv_un_payed_shop_price)TextView shop_priceTV;
        @Bind(R.id.btn_pay)Button payBtn;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_un_payed_order_list,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        OrderItem item=dataList.get(position);
        viewHolder.oidTV.setText(item.getOid());
        viewHolder.o_dateTV.setText(item.getOtime());
        viewHolder.p_nameTV.setText(item.getProduct().getPname());
        BigDecimal bd=item.getProduct().getShopprice();
        bd=bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        viewHolder.shop_priceTV.setText(bd.toString());
        imageLoader.displayImage(RequestURLs.MAIN_URL+dataList.get(position).getProduct().getCoverImage(),viewHolder.coverImageIV,options);
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,true,false));
        return convertView;
    }
}
