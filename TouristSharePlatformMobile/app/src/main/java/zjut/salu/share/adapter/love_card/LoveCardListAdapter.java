package zjut.salu.share.adapter.love_card;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.model.lovecard.LovePostCard;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.widget.PullSeparateListView;

/**明信片适配器
 * Created by Alan on 2016/11/6.
 */

public class LoveCardListAdapter extends BaseAdapter{
    private Context context;
    private List<LovePostCard> list;
    private PullSeparateListView listView;
    private LayoutInflater inflater;
    private SimpleDateFormat format=null;
    private ImageLoader imageLoader=null;
    private final DisplayImageOptions options;

    public LoveCardListAdapter(Context context, List<LovePostCard> list, PullSeparateListView listView) {
        this.context = context;
        this.list = list;
        this.listView = listView;
        this.inflater = LayoutInflater.from(this.context);
        format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
        @Bind(R.id.iv_item_love_card)ImageView coverIV;//图片
        @Bind(R.id.tv_title_item_love_card)TextView titleTV;//标题
        @Bind(R.id.tv_tag_item_love_card)TextView tagTV;//标签
        @Bind(R.id.tv_date_item_love_card)TextView timeTV;//截止时间
        @Bind(R.id.tv_name_item_love_card)TextView usernameTV;//发起人
        @Bind(R.id.tv_follower_num_item_love_card)TextView personNumTV;//留言数与参与数
        public ViewHolder(View v){
            ButterKnife.bind(this,v);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.iitem_love_card,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        LovePostCard card=list.get(position);
        viewHolder.titleTV.setText(card.getLovetitle());
        viewHolder.tagTV.setText(card.getLovetags());
        String time=context.getText(R.string.dead_line_text)+format.format(card.getLovedeadline());
        viewHolder.timeTV.setText(time);
        viewHolder.usernameTV.setText(card.getUser().getUsername());
        String personNumber=String.valueOf(card.getComments().size())+context.getText(R.string.comment_num_text)+"|"+
                String.valueOf(card.getParticipates().size())+context.getText(R.string.participate_num_text);
        viewHolder.personNumTV.setText(personNumber);
        imageLoader.displayImage(RequestURLs.MAIN_URL+card.getLovepic(),viewHolder.coverIV,options);
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,true,true));
        return convertView;
    }
}
