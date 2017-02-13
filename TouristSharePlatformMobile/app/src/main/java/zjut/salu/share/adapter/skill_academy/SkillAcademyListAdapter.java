package zjut.salu.share.adapter.skill_academy;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.model.skillacademy.SkillAcademy;
import zjut.salu.share.utils.RequestURLs;

/**行摄攻略列表显示适配器
 * Created by Alan on 2016/11/2.
 */

public class SkillAcademyListAdapter extends BaseAdapter {
    private Context context;
    private List<SkillAcademy> list;
    private LayoutInflater inflater;
    private ListView listView;
    private SimpleDateFormat format=null;
    public SkillAcademyListAdapter(Context context, List<SkillAcademy> list, ListView listView){
        this.context=context;
        this.list=list;
        inflater=LayoutInflater.from(this.context);
        this.listView=listView;
        format=new SimpleDateFormat("yyyy年MM月dd日");
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
        @Bind(R.id.iv_cover_skill_academy_item)ImageView coverIV;
        @Bind(R.id.tv_title_skill_academy_item)TextView titleTV;
        @Bind(R.id.tv_date_skill_academy_item)TextView timeTV;
        @Bind(R.id.tv_read_num_skill_academy_item)TextView readNumTV;
         ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_skill_academy,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        SkillAcademy academy=list.get(position);
        viewHolder.titleTV.setText(academy.getSkilltitle());
        viewHolder.timeTV.setText(format.format(academy.getSkilldate()));
        viewHolder.readNumTV.setText(String.valueOf(academy.getClickednum())+context.getText(R.string.read_text));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.feedback_cartoon)
                .showImageOnFail(R.drawable.feedback_cartoon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader imageLoader=ImageLoader.getInstance();
        imageLoader.displayImage(RequestURLs.MAIN_URL+academy.getCoverImage(),viewHolder.coverIV,options);
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,true,true));
        return convertView;
    }
}
