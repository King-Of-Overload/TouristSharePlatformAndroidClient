package zjut.salu.share.adapter.destination;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.model.CityIndexRecommend;
import zjut.salu.share.widget.CommonCircleImageView;

/**城市主页推荐
 * Created by Salu on 2017/1/20.
 */

public class CityIndexRecommendAdapter extends BaseAdapter {
    private List<CityIndexRecommend> list;
    private LayoutInflater inflater;

    public CityIndexRecommendAdapter(Context context,List<CityIndexRecommend> list) {
        this.list = list;
        inflater=LayoutInflater.from(context);
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
        return list.size();
    }

    class ViewHolder{
        @Bind(R.id.tv_name)TextView name;
        @Bind(R.id.cciv_cover)CommonCircleImageView imageView;
        @Bind(R.id.tv_des)TextView des;
        public ViewHolder(View v) {
            ButterKnife.bind(this,v);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_city_index_recommend,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        CityIndexRecommend recommend=list.get(position);
        viewHolder.name.setText(recommend.getCityindexname());
        TextPaint paint=viewHolder.name.getPaint();
        paint.setFakeBoldText(true);
        //TODO:等待修改
        viewHolder.imageView.setImageResource(R.drawable.test_japan);
        viewHolder.des.setText(recommend.getCityindexdes());
        return convertView;
    }
}
