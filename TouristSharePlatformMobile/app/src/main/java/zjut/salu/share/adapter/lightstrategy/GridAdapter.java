package zjut.salu.share.adapter.lightstrategy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import zjut.salu.share.R;

/**预览适配器
 * Created by Salu on 2017/2/2.
 */

public class GridAdapter extends BaseAdapter {
    private ArrayList<String> listUrls;
    private LayoutInflater inflater;
    private int columnWidth;
    private Context context;

    public GridAdapter(ArrayList<String> listUrls,Context context,int columnWidth) {
        this.listUrls = listUrls;
        this.inflater=LayoutInflater.from(context);
        this.columnWidth=columnWidth;
        this.context=context;
    }

    @Override
    public int getCount() {
        return listUrls.size();
    }

    @Override
    public String getItem(int position) {
        return listUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_image_preview, null);
            imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(imageView);
            // 重置ImageView宽高
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, columnWidth);
            imageView.setLayoutParams(params);
        }else {
            imageView = (ImageView) convertView.getTag();
        }
        Glide.with(context)
                .load(new File(getItem(position)))
                .placeholder(R.mipmap.default_error)
                .error(R.mipmap.default_error)
                .centerCrop()
                .crossFade()
                .into(imageView);
        return convertView;
    }


}
