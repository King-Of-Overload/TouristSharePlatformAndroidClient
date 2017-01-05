package zjut.salu.share.adapter.product;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import zjut.salu.share.model.Product;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.RequestURLs;

/**商品列表adapter
 * Created by Alan on 2016/11/9.
 */

public class ProductListAdapter extends BaseAdapter {
    private List<Product> list=null;
    private ListView listView=null;
    private LayoutInflater inflater=null;
    private ImageLoader imageLoader=null;

    public ProductListAdapter(ListView listView, List<Product> list, Context context) {
        this.listView = listView;
        this.list = list;
        inflater=LayoutInflater.from(context);
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
        @Bind(R.id.iv_cover_product)ImageView coverIV;
        @Bind(R.id.tv_name_product)TextView nameTV;
        @Bind(R.id.tv_shop_price_product)TextView shopPriceTV;
        @Bind(R.id.tv_market_price_product)TextView marketPriceTV;
        @Bind(R.id.tv_status_product)TextView statusTV;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_product,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Product product=list.get(position);
        viewHolder.nameTV.setText(product.getPname());
        viewHolder.shopPriceTV.setText(String.valueOf(product.getShopprice().setScale(2, BigDecimal.ROUND_HALF_UP)));
        viewHolder.marketPriceTV.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中间横线
        viewHolder.marketPriceTV.setText(String.valueOf(product.getMarketprice().setScale(2, BigDecimal.ROUND_HALF_UP)));
        viewHolder.statusTV.setText(product.getQualitychoice());
        DisplayImageOptions options= ImageLoaderUtils.getImageOptions();
        imageLoader.displayImage(RequestURLs.MAIN_URL+product.getCoverImage(),viewHolder.coverIV,options);
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,false,true));
        return convertView;
    }
}
