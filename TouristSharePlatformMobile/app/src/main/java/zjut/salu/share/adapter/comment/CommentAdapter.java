package zjut.salu.share.adapter.comment;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import zjut.salu.share.R;
import zjut.salu.share.model.user.Comments;
import zjut.salu.share.model.user.Reply;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.widget.comment.CommentItemView;

/**评论列表适配器
 * Created by Salu on 2017/2/19.
 */

public class CommentAdapter extends BaseAdapter implements CommentItemView.OnCommentListener{
    private Context context;
    private List<Comments> mData;
    private Map<Integer,CommentItemView> mCachedViews=new HashMap<>();//viewholder
    private View mCommentView;

    public CommentAdapter(List<Comments> mData, Context context,View mCommentView) {
        this.mData = mData;
        this.context = context;
        this.mCommentView=mCommentView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_comment, null, false);
        }
        if(view instanceof CommentItemView){
            Comments data = (Comments) getItem(position);
            ((CommentItemView) view).setData(data);
            ((CommentItemView) view).setPosition(position);
            ((CommentItemView) view).setCommentListener(this);
            cacheView(position, (CommentItemView) view);
        }


        return view;
    }

    @Override
    public void onComment(int position) {
        showCommentView(position);
    }

    private void cacheView(int position, CommentItemView view) {
        Iterator<Map.Entry<Integer, CommentItemView>> entries = mCachedViews.entrySet().iterator();

        while (entries.hasNext()) {

            Map.Entry<Integer, CommentItemView> entry = entries.next();
            if (entry.getValue() == view && entry.getKey() != position) {
                mCachedViews.remove(entry.getKey());
                break;
            }
        }

        mCachedViews.put(position, view);

        Log.d("CommentAdapter", position + ", " + mCachedViews.size());
    }


    private void showCommentView(final int position) {
        mCommentView.setVisibility(View.VISIBLE);

        mCommentView.findViewById(R.id.submit).setOnClickListener(v -> {
            EditText et = (EditText) mCommentView.findViewById(R.id.edit);
            String initContent="@"+mData.get(position).getFromuser().getUsername();
            et.setText(initContent);
            String s = et.getText().toString();
            if (!TextUtils.isEmpty(s)) {
                //TODO:发送回复
                // update model
                Reply reply = new Reply();
                reply.setComment(mData.get(position));
                reply.setReplycontent(s);
                mData.get(position).getReplies().add(reply);
                // update view maybe
                CommentItemView itemView = mCachedViews.get(position);
                if (itemView != null && position == itemView.getPosition()) {
                    itemView.addComment();
                }
                et.setText("");
                mCommentView.setVisibility(View.GONE);
            }
        });
    }
}
