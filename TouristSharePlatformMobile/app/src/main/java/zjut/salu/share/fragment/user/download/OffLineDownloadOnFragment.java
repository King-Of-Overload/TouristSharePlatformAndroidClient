package zjut.salu.share.fragment.user.download;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.fragment.RxLazyFragment;

/**下载进行中
 * Created by Salu on 2017/2/17.
 */

public class OffLineDownloadOnFragment extends RxLazyFragment{
    @Bind(R.id.frame_progress)FrameLayout progressFrame;//不显示
    @Bind(R.id.releave_btn_group)RelativeLayout btnGroups;//显示
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_offline_download_complete;
    }

    @Override
    public void finishCreateView(Bundle state) {

    }
}
