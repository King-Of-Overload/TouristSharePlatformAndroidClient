package zjut.salu.share.adapter.tourism;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.config.CuteTouristShareConfig;
import zjut.salu.share.model.local.TourismAttraction;
import zjut.salu.share.utils.ImageLoaderOptionUtils;
import zjut.salu.share.utils.RequestURLs;

/**地点适配器
 * Created by Salu on 2017/2/13.
 */

public class TourismAttractionAdapter extends BaseAdapter{
    private List<TourismAttraction> attractionList;
    private ImageLoader imageLoader;
    private LayoutInflater inflater;
    private BDLocation location;
    public TourismAttractionAdapter(Context context, List<TourismAttraction> attractionList, ImageLoader imageLoader, BDLocation location) {
        this.attractionList = attractionList;
        this.imageLoader=imageLoader;
        inflater=LayoutInflater.from(context);
        this.location=location;
    }

    @Override
    public int getCount() {
        return attractionList.size();
    }

    @Override
    public Object getItem(int position) {
        return attractionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_tourism_attraction,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        TourismAttraction attraction=attractionList.get(position);
        DisplayImageOptions options= ImageLoaderOptionUtils.getImgOptions();
        if(null!=attraction.getTourismCoverses()&&attraction.getTourismCoverses().size()>0){
            imageLoader.displayImage(RequestURLs.MAIN_URL+attraction.getTourismCoverses().get(0).getTourismurl(),viewHolder.coverIV,
                    options);
        }
        viewHolder.nameTV.setText(attraction.getTourismname());
        TextPaint paint=viewHolder.nameTV.getPaint();
        paint.setFakeBoldText(true);
        viewHolder.ratingBar.setMax(5);
        viewHolder.ratingBar.setNumStars(5);
        viewHolder.ratingBar.setRating(4.8f);//TODO:设置评分
        String point=4.8+ CuteTouristShareConfig.mInstance.getString(R.string.point_text);
        viewHolder.pointTV.setText(point);
        String currency=attraction.getTourismprice()+attraction.getCurrencytype();
        viewHolder.moneyTV.setText(currency);
        LatLng currentLatlng=new LatLng(location.getLatitude(),location.getLongitude());
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
         //待转换坐标
        converter.coord(new LatLng(attraction.getLocation().getLatitude().doubleValue(),attraction.getLocation().getLongitude().doubleValue()));
        LatLng desLatLng = converter.convert();
        double distance=DistanceUtil.getDistance(currentLatlng,desLatLng);
        viewHolder.distanceTV.setText(String.valueOf(distance));
        viewHolder.categoryTV.setText(attraction.getTourismCategory().getTmcategoryname());
        return convertView;
    }

    class ViewHolder{
        @Bind(R.id.iv_cover)ImageView coverIV;
        @Bind(R.id.tv_name)TextView nameTV;
        @Bind(R.id.ratingbar)RatingBar ratingBar;
        @Bind(R.id.tv_point)TextView pointTV;
        @Bind(R.id.tv_money)TextView moneyTV;
        @Bind(R.id.tv_category)TextView categoryTV;
        @Bind(R.id.tv_distance)TextView distanceTV;
        ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
