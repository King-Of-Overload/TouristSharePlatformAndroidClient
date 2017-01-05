package zjut.salu.share.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import zjut.salu.share.config.CuteTouristShareConfig;

/**さるの帅气的Toast工具集合
 * Created by Alan(さる) on 2016/10/16.
 */

public class ToastUtils {
    public static void showShort(Context context, String text)
    {

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, int resId)
    {

        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String text)
    {

        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, int resId)
    {

        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void LongToast(final String text)
    {

        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(CuteTouristShareConfig.getInstance(), text, Toast.LENGTH_LONG).show());
    }

    public static void LongToast(final int stringId)
    {

        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(CuteTouristShareConfig.getInstance(), stringId, Toast.LENGTH_LONG).show());
    }

    public static void ShortToast(final String text)
    {

        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(CuteTouristShareConfig.getInstance(), text, Toast.LENGTH_SHORT).show());
    }

    public static void ShortToast(final int stringId)
    {

        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(CuteTouristShareConfig.getInstance(), stringId, Toast.LENGTH_SHORT).show());
    }

}
