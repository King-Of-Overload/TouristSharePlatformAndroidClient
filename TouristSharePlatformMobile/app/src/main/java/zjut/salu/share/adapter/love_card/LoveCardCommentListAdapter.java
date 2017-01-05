package zjut.salu.share.adapter.love_card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zjut.salu.share.R;
import zjut.salu.share.model.LovePostCardComment;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.widget.CommonCircleImageView;
import zjut.salu.share.widget.NoScrollListView;

/**明信片评论区适配器
 * Created by Alan on 2016/11/7.
 */

public class LoveCardCommentListAdapter extends BaseAdapter{
    private LayoutInflater inflater=null;
    private NoScrollListView listView=null;
    private List<LovePostCardComment> comments;
    private ImageLoader imageLoader=null;

    public LoveCardCommentListAdapter(Context context,NoScrollListView listView, List<LovePostCardComment> comments) {
        this.listView = listView;
        this.comments = comments;
        inflater=LayoutInflater.from(context);
        imageLoader=ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

     static class ViewHolder{
        @Bind(R.id.cciv_avatar_comment_love_card_detail)CommonCircleImageView avatarIV;
        @Bind(R.id.tv_username_comment_love_card_detail)TextView commentNameTV;
        @Bind(R.id.tv_time_comment_love_card_detail)TextView dateTV;
        @Bind(R.id.tv_content_comment_love_card_detail)TextView contentTV;
        @Bind(R.id.tv_reply_comment_love_card_detail)TextView replyTV;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_comment_love_card_detail,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        LovePostCardComment comment=comments.get(position);
        viewHolder.commentNameTV.setText(comment.getUser().getUsername());
        viewHolder.dateTV.setText(StringUtils.formatDate(comment.getLovedate(),"HH:mm yyyy-MM-dd"));
        viewHolder.contentTV.setText(comment.getLovecommentcontent());
        viewHolder.replyTV.setOnClickListener(new ReplyOnClickListener());
        DisplayImageOptions options= ImageLoaderUtils.getImageOptions();
        imageLoader.displayImage(RequestURLs.MAIN_URL+comment.getUser().getHeaderimage(),viewHolder.avatarIV,options,new ImageLoaderUtils.ImageLoadingListenerImpl());
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,false,true));
        return convertView;
    }

    /**
     * 回复按钮点击事件
     */
    private class ReplyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //TODO:点击回复回复消息
        }
    }


}
