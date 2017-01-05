package zjut.salu.share.utils;

import com.andview.refreshview.XRefreshView;

import zjut.salu.share.fragment.user_order_list_fragment.UserPayedOrderFragment;

/**下拉刷新工具类
 * Created by Alan on 2016/10/29.
 */

public class PullRefreshUtils {
    /**
     * 绑定下拉刷新
     * @param refreshView
     * @param isPullRefresh 是否下拉刷新
     * @param isPullLoad 是否上拉加载
     * @param listener 监听器
     */
    public static void bindPullRefreshView(XRefreshView refreshView, Boolean isPullRefresh, Boolean isPullLoad,
                                           XRefreshView.XRefreshViewListener listener){
        refreshView.setPullRefreshEnable(isPullRefresh);// 设置是否可以下拉刷新
        refreshView.setPullLoadEnable(isPullLoad);// 设置是否可以上拉加载
        refreshView.setXRefreshViewListener(listener);//设置下拉刷新监听器
    }
}
