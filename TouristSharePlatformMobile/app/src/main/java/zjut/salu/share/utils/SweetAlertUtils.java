package zjut.salu.share.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**sweet对话框工具类
 * Created by Alan on 2016/10/19.
 */

public class SweetAlertUtils {
    /**
     * @param context 上下文
     * @param title 标题
     * 只显示标题
     */
    public static void showTitleDialog(Context context,String title){
        SweetAlertDialog dialog=new SweetAlertDialog(context);
        dialog.setTitleText(title);
        dialog.show();
    }

    /**
     *
     * @param context 上下文
     * @param title 标题
     * @param content 内容
     */
    public static void showTitleAndContentDialog(Context context,String title,String content){
        SweetAlertDialog dialog=new SweetAlertDialog(context);
        dialog.setTitleText(title);
        dialog.setContentText(content);
        dialog.show();
    }

    /**
     * 带样式配置的对话框
     * @param context 上下文
     * @param title 标题
     * @param content 内容
     * @param type 类型 SweetAlertDialog.ERROR_TYPE(异常) WARNING_TYPE(警告) SUCCESS_TYPE(成功)
     *             CUSTOM_IMAGE_TYPE(自定义头像模式)
     * @param img 自定义头像
     * @param p_text 确认按钮文字
     * @param n_text 取消按钮文字
     * @param p_listener 确认按钮监听
     * @param n_listener 取消按钮监听
     */
    public static void showTitleAndContentDialogWithStyle(Context context, String title, String content, int type, Drawable img,
                                                          String p_text,String n_text,
                                                          SweetAlertDialog.OnSweetClickListener p_listener,
                                                          SweetAlertDialog.OnSweetClickListener n_listener){
        SweetAlertDialog dialog=new SweetAlertDialog(context,type);
        dialog.setTitleText(title);
        dialog.setContentText(content);
        if(null!=img){
            dialog.setCustomImage(img);
        }
        if(null!=p_text&&!("").equals(p_text)&&null!=p_listener){
            dialog.setConfirmText(p_text);
            dialog.setConfirmClickListener(p_listener);
        }
        if(null!=n_text&&!("").equals(n_text)&&null!=n_listener){
            dialog.setCancelText(n_text);
            dialog.setCancelClickListener(n_listener);
        }
        dialog.show();
    }

    /*
    确认后切换对话框样式
    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
    .setTitleText(\"Are you sure?\")
    .setContentText(\"Won't be able to recover this file!\")
    .setConfirmText(\"Yes,delete it!\")
    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
            sDialog
                .setTitleText(\"Deleted!\")
                .setContentText(\"Your imaginary file has been deleted!\")
                .setConfirmText(\"OK\")
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        }
    })
    .show();
     */
}
