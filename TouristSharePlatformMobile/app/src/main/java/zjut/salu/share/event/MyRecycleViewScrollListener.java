package zjut.salu.share.event;

import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**recycleview性能优化后的滑动监听器
 * Created by Salu on 2016/12/9.
 */

public class MyRecycleViewScrollListener extends RecyclerView.OnScrollListener{
    ImageLoader imageLoader=ImageLoader.getInstance();
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        switch (newState){
            case RecyclerView.SCROLL_STATE_DRAGGING:
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
