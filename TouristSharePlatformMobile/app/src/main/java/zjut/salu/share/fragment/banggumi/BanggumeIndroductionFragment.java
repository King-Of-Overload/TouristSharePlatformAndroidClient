package zjut.salu.share.fragment.banggumi;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhy.view.flowlayout.TagFlowLayout;
import butterknife.Bind;
import butterknife.OnClick;
import zjut.salu.share.R;
import zjut.salu.share.fragment.RxLazyFragment;
import zjut.salu.share.utils.ConstantUtil;
import zjut.salu.share.utils.NumberUtil;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.widget.UserTagView;

/**视频详情fragment
 * Created by Salu on 2017/1/29.
 */

public class BanggumeIndroductionFragment extends RxLazyFragment{
    @Bind(R.id.tv_title) TextView mTitleText;
    @Bind(R.id.tv_play_time) TextView mPlayTimeText;
    @Bind(R.id.tv_review_count) TextView mReviewCountText;
    @Bind(R.id.tv_description) TextView mDescText;
    @Bind(R.id.author_tag) UserTagView mAuthorTagView;
    @Bind(R.id.share_num) TextView mShareNum;
    @Bind(R.id.fav_num) TextView mFavNum;
    @Bind(R.id.tags_layout) TagFlowLayout mTagFlowLayout;
    @Bind(R.id.recycle) RecyclerView mRecyclerView;
    @Bind(R.id.layout_video_related) RelativeLayout mVideoRelatedLayout;

    private String av;

    public static BanggumeIndroductionFragment newInstance(String aid)
    {

        BanggumeIndroductionFragment fragment = new BanggumeIndroductionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantUtil.BANGGUMI_TITLE, aid);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public int getLayoutResId()
    {

        return R.layout.fragment_banggume_introduction;
    }

    @Override
    public void finishCreateView(Bundle state)
    {

        av = getArguments().getString(ConstantUtil.BANGGUMI_TITLE);
        //TODO: loadData();访问网络
        loadData();
    }


    @Override
    protected void loadData()
    {

        //TODO:
        finishTask();
    }

    @Override
    protected void finishTask()
    {

        //设置视频标题
        mTitleText.setText(av);
        //设置视频播放数量
        mPlayTimeText.setText(NumberUtil.converString(1234));
        //设置视频弹幕数量
        mReviewCountText.setText(NumberUtil.converString(1234));
        //设置Up主信息
        mDescText.setText("一直觉得这首歌很难表现能表达里面的情感，可能每个人的理解都不一样，我自己认为是循序渐进的迷失自我→打开心结→找到自我的过程，所以试着这样去表现了，一开始有点苦瓜脸希望别介意....UP主舞技还有很多不足，与大家一起努力，共勉。");
        mAuthorTagView.setUpWithInfo(getActivity(),
                "有咩酱",
                RequestURLs.MAIN_URL+"images/headerImages/zhenghehuizi.jpg");
        //设置分享 收藏 投币数量
        mShareNum.setText(NumberUtil.converString(66));
        mFavNum.setText(NumberUtil.converString(66));
        //设置视频tags
        setVideoTags();
        //设置视频相关
        setVideoRelated();
    }


    private void setVideoRelated()
    {
        //TODO:加载相关视频 recycleview
    }

    private void setVideoTags()
    {

        //TODO:获取tag信息 List<String> tags = mVideoDetailsInfo.getTags();
//        mTagFlowLayout.setAdapter(new TagAdapter<String>(tags)
//        {
//
//            @Override
//            public View getView(FlowLayout parent, int position, String s)
//            {
//
//                TextView mTags = (TextView) LayoutInflater.from(getActivity())
//                        .inflate(R.layout.layout_tags_item, parent, false);
//                mTags.setText(s);
//
//                return mTags;
//            }
//        });
    }

    @OnClick(R.id.btn_share)
    public void share()
    {
        //TODO:分享按钮
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//        intent.putExtra(Intent.EXTRA_TEXT, "来自「哔哩哔哩」的分享:" + mVideoDetailsInfo.getDesc());
//        startActivity(Intent.createChooser(intent, mVideoDetailsInfo.getTitle()));
    }

}
