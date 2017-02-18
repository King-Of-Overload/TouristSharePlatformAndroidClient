package zjut.salu.share.fragment.user.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.activity.banggumi.BanggumiPlayerActivity;
import zjut.salu.share.adapter.user.download.OffLineDownloadCompleteAdapter;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.fragment.RxLazyFragment;
import zjut.salu.share.greendao.BanggumeDao;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.utils.CommonUtils;
import zjut.salu.share.utils.ConstantUtil;
import zjut.salu.share.utils.FileUtil;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.utils.greendao.GreenDaoDBHelper;
import zjut.salu.share.widget.progress.NumberProgressBar;
import zjut.salu.share.widget.view.CustomEmptyView;

/**下载完成
 * Created by Salu on 2017/2/17.
 */

public class OffLineDownloadCompleteFragment extends RxLazyFragment{
    @Bind(R.id.frame_progress)FrameLayout progressFrame;//显示
    @Bind(R.id.releave_btn_group)RelativeLayout btnGroups;//不显示
    @Bind(R.id.progress_bar)NumberProgressBar progressBar;
    @Bind(R.id.cache_size_text)TextView mCacheSize;
    @Bind(R.id.empty_layout)CustomEmptyView emptyView;
    @Bind(R.id.recycle)RecyclerView recyclerView;
    private BanggumeDao banggumeDao;
    private List<Banggume> banggumeList;

    private Context context;
    private OffLineDownloadCompleteAdapter completeAdapter;
    private ImageLoader imageLoader;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_offline_download_complete;
    }

    @Override
    public void finishCreateView(Bundle state) {
        context=getActivity();
        imageLoader=ImageLoader.getInstance();
        progressFrame.setVisibility(View.VISIBLE);
        btnGroups.setVisibility(View.GONE);
        banggumeDao= GreenDaoDBHelper.getDaoSession().getBanggumeDao();
        banggumeList=new ArrayList<>();
        banggumeList=banggumeDao.loadAll();
        long phoneTotalSize = CommonUtils.getPhoneTotalSize();
        long phoneAvailableSize = CommonUtils.getPhoneAvailableSize();
        //转换为G的显示单位
        String totalSizeStr = Formatter.formatFileSize(context, phoneTotalSize);
        String availabSizeStr = Formatter.formatFileSize(context, phoneAvailableSize);
        //计算占用空间的百分比
        int progress = countProgress(phoneTotalSize, phoneAvailableSize);
        progressBar.setProgress(progress);
        mCacheSize.setText(getString(R.string.main_storage_text) + totalSizeStr + "/" + getString(R.string.available_text) + availabSizeStr);
        if(banggumeList.size()==0){
            emptyView.setEmptyImage(R.drawable.img_tips_error_no_downloads);
            emptyView.setEmptyText(getString(R.string.no_cache_text));
        }else{
            finishTask();
        }
    }

    @Override
    protected void finishTask() {
        completeAdapter=new OffLineDownloadCompleteAdapter(recyclerView,banggumeList,imageLoader);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(completeAdapter);
        recyclerView.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
        completeAdapter.setOnItemClickListener((position, holder) -> {//TODO:条目点击事件
            Banggume banggume=banggumeList.get(position);
            if(FileUtil.exists(new File(banggume.getPhonestoragepath()))){
                Intent intent=new Intent(context, BanggumiPlayerActivity.class);
                intent.putExtra("banggume",banggume);
                intent.putExtra(ConstantUtil.BANGGUMI_ID,banggume.getBangumeid());
                intent.putExtra(ConstantUtil.BANGGUMI_TITLE,banggume.getBangumename());
                startActivity(intent);
            }else{
                ToastUtils.ShortToast(R.string.file_not_exist_text);
                banggumeDao.delete(banggume);//删除无用记录
            }
        });
    }

    private int countProgress(long phoneTotalSize, long phoneAvailableSize)
    {

        double totalSize = phoneTotalSize / (1024 * 3);
        double availableSize = phoneAvailableSize / (1024 * 3);
        //取整相减
        int size = (int) (Math.floor(totalSize) - Math.floor(availableSize));
        double v = (size / Math.floor(totalSize)) * 100;
        return (int) Math.floor(v);
    }

}
