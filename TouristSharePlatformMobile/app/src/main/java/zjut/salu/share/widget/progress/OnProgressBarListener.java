package zjut.salu.share.widget.progress;

/**进度条接口
 * Created by Salu on 2017/2/17.
 */

public interface OnProgressBarListener {
    /**
     * 数字进度条进度回调接口
     * @param current
     * @param max
     */

    void onProgressChange(int current, int max);
}
