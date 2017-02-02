package zjut.salu.share.fragment.banggumi;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.fragment.RxLazyFragment;
import zjut.salu.share.utils.ConstantUtil;

/**小视频评论fragment
 * Created by Salu on 2017/1/29.
 */

public class BanggumiCommentFragment extends RxLazyFragment {
    @Bind(R.id.recycle)RecyclerView mRecyclerView;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_banggumi_comment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        //TODO:获取评论
    }

    public static BanggumiCommentFragment newInstance(String bangumiId)
    {
        BanggumiCommentFragment fragment = new BanggumiCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantUtil.BANGGUMI_ID, bangumiId);
        fragment.setArguments(bundle);
        return fragment;
    }
}
