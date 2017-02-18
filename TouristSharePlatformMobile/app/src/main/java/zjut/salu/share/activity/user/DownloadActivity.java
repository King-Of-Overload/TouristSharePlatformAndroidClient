package zjut.salu.share.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.greendao.BanggumeDao;
import zjut.salu.share.model.lightstrategy.banggume.Banggume;
import zjut.salu.share.utils.FileUtil;
import zjut.salu.share.utils.ImageLoaderUtils;
import zjut.salu.share.utils.RequestURLs;
import zjut.salu.share.utils.StringUtils;
import zjut.salu.share.utils.ToastUtils;
import zjut.salu.share.utils.greendao.GreenDaoDBHelper;

/**
 * 临时下载
 */
public class DownloadActivity extends RxBaseActivity{
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.ivIcon)ImageView coverIV;
    @Bind(R.id.tvFileName)TextView nameTV;
    @Bind(R.id.pbProgress)ProgressBar progressBar;
    @Bind(R.id.tvText)TextView speedTV;
    @Bind(R.id.tvDownloadSize)TextView downloadSizeTV;
    @Bind(R.id.tvTotalSize)TextView totalSizeTV;
    @Bind(R.id.tvPercent)TextView percentTV;

    private Banggume banggume;
    private String path;
    private BanggumeDao banggumeDao;
    @Override
    public int getLayoutId() {
        return R.layout.activity_download;
    }

    public static void launch(Activity activity, Banggume banggume){
        Intent intent=new Intent(activity,DownloadActivity.class);
        intent.putExtra("banggume",banggume);
        activity.startActivity(intent);
    }
    @Override
    public void initViews(Bundle savedInstanceState) {
        banggumeDao= GreenDaoDBHelper.getDaoSession().getBanggumeDao();
        Intent intent=getIntent();
        banggume= (Banggume) intent.getSerializableExtra("banggume");
        path=Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "fangcaoxunyuan"+File.separator+"banggumes"+File.separator+banggume.getBangumeurl().substring(banggume.getBangumecover().lastIndexOf('/')+1);
        ImageLoaderUtils.loadAvatarWithURL(this, RequestURLs.MAIN_URL+banggume.getBangumecover(), DiskCacheStrategy.ALL,coverIV);
        nameTV.setText(banggume.getBangumename());
        FileDownloader.getImpl().create(RequestURLs.MAIN_URL+banggume.getBangumeurl())
                .setPath(path,false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        ToastUtils.ShortToast(R.string.start_cache_text);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        banggume.setSize(totalBytes);
                        updateProgress(soFarBytes,totalBytes,task.getSpeed());
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        ToastUtils.ShortToast(R.string.cache_complete_text);
                        banggume.setPhonestoragepath(path);
                        List list = banggumeDao.queryBuilder()
                                .where(BanggumeDao.Properties.Bangumeid.eq(banggume.getBangumeid()))
                                .list();
                        if(list.size()==0){
                            banggumeDao.insert(banggume);
                        }else{
                         ToastUtils.ShortToast(R.string.cache_has_text);
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        ToastUtils.ShortToast(R.string.server_down_text);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();

    }

    private void updateSpeed(int speed) {
        speedTV.setText(String.format("%dKB/s", speed));
    }

    public void updateProgress(final int sofar, final int total, final int speed) {
        if (total == -1) {
            // chunked transfer encoding data
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setMax(total);
            progressBar.setProgress(sofar);
        }

        updateSpeed(speed);
        downloadSizeTV.setText(FileUtil.FormetFileSize(sofar));
        totalSizeTV.setText(FileUtil.FormetFileSize(total));
        percentTV.setText((sofar/total)*100+"%");
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.offline_download_text);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
