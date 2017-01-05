package zjut.salu.share.utils;

import android.view.View;

import mehdi.sakout.dynamicbox.DynamicBox;
import zjut.salu.share.R;
import zjut.salu.share.fragment.user_order_list_fragment.UserUnpayedOrderFragment;

/**动态状态工具类
 * Created by Alan on 2016/10/29.
 */

public class DynamicBoxUtils {
    /**
     * 绑定动态视图
     * @param dynamicBox db对象
     * @param exceptionTitle 异常标题
     * @param exceptionMessage 异常信息
     * @param loadingMsg 正在加载文字
     * @param exceptionClick 异常点击监听时间
     */
    public static void bindDynamicBox(DynamicBox dynamicBox, String exceptionTitle, String exceptionMessage, String loadingMsg,
                                      View.OnClickListener exceptionClick){
        dynamicBox.setOtherExceptionTitle(exceptionTitle);
        dynamicBox.setOtherExceptionMessage(exceptionMessage);
        dynamicBox.setLoadingMessage(loadingMsg);
        dynamicBox.setClickListener(exceptionClick);
    }
}
