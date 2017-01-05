package zjut.salu.share.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

/**colorDialog工具类
 * Created by Alan on 2016/10/19.
 * 简单封装的对话框控件 ColorDialog & PromptDialog
 *ColorDialog支持三种显示形式: Text Mode, Image Mode, Text & Image Mode
 *PromptDialog提供默认五种显示形式: Success, Info, Error, Warning, Help
 *提供默认的进出动画, 支持自定义动画
 *PromptDialog UI来自于Dribbble的设计师 @Diego Faria, Alan(さる)在此表示感谢
 */

public class ColorDialogUtils {
    /**
     * 普通prompt弹窗，带有一个按钮
     *
     * @param context      上下文
     * @param titleText    文字标题
     * @param contentText  提示内容
     * @param positiveText ok按钮文字
     * @param listener     监听器
     * @param dialogType   显示类型
     */
    public static void showCommonDialogWithText(Context context, String titleText, String contentText, String positiveText, PromptDialog.OnPositiveListener listener, int dialogType) {
        PromptDialog dialog = new PromptDialog(context);
        AnimationSet setIn=new AnimationSet(true);
        setIn.addAnimation(AnimationUtils.loadAnimation(context,android.R.anim.fade_in));
        dialog.setAnimationIn(setIn);
        AnimationSet setOut=new AnimationSet(true);
        setOut.addAnimation(AnimationUtils.loadAnimation(context,android.R.anim.fade_out));
        dialog.setAnimationOut(setOut);
        dialog.setDialogType(dialogType);
        dialog.setTitleText(titleText);
        dialog.setContentText(contentText);
        dialog.setPositiveListener(positiveText, listener).show();
    }

    /**
     * colorful对话框，可带有两个以上按钮，可以有配图
     *
     * @param color       标题栏色彩 使用默认传一个  -1
     * @param context     上下文对象
     * @param titleText   标题
     * @param contextText 提示内容
     * @param imageRes    图片
     * @param p_text      确认按钮文字
     * @param n_text      否定按钮文字
     * @param p_listener  监听器
     * @param n_listener  监听器
     */
    public static void showColorfulDialog(Context context, int color, String titleText, String contextText, Drawable imageRes, String p_text, String n_text, ColorDialog.OnPositiveListener p_listener, ColorDialog.OnNegativeListener n_listener) {
        ColorDialog dialog = new ColorDialog(context);
        if (color != -1) {
            dialog.setColor(color);
        }
        dialog.setTitle(titleText);
        dialog.setContentText(contextText);
        dialog.setAnimationEnable(true);
        AnimationSet setIn=new AnimationSet(true);
        setIn.addAnimation(AnimationUtils.loadAnimation(context,android.R.anim.fade_in));
        dialog.setAnimationIn(setIn);
        AnimationSet setOut=new AnimationSet(true);
        setOut.addAnimation(AnimationUtils.loadAnimation(context,android.R.anim.fade_out));
        dialog.setAnimationOut(setOut);
        if (imageRes != null) {
            dialog.setContentImage(imageRes);
        }
        if (p_listener != null && !("").equals(p_text) && null != p_text) {
            dialog.setPositiveListener(p_text, p_listener);
        }
        if (n_listener != null && !("").equals(n_text) && null != n_text) {
            dialog.setNegativeListener(n_text, n_listener).show();
        }
    }

}