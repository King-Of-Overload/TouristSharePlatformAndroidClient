package zjut.salu.share.event;

import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import zjut.salu.share.utils.DisplayUtil;
import zjut.salu.share.widget.scrollview.RecycleScrollView;

/**recycleview性能优化后的滑动监听器
 * Created by Salu on 2016/12/9.
 */

public class MyRecycleViewScrollListener extends RecyclerView.OnScrollListener{
    ImageLoader imageLoader=ImageLoader.getInstance();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecycleScrollView scrollView;
    public MyRecycleViewScrollListener(@Nullable SwipeRefreshLayout swipeRefreshLayout, @Nullable RecycleScrollView scrollView) {
        this.swipeRefreshLayout=swipeRefreshLayout;
        this.scrollView=scrollView;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        switch (newState){
            case RecyclerView.SCROLL_STATE_DRAGGING:
                if(null!=swipeRefreshLayout){
                    if(null!=scrollView){
                        if(DisplayUtil.canChildScrollUp(scrollView)){
                            swipeRefreshLayout.setEnabled(false);
                        }else{
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    }
                }
                //正在滑动
                imageLoader.pause();
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                //滑动停止
                imageLoader.resume();
                break;
        }
    }

}
