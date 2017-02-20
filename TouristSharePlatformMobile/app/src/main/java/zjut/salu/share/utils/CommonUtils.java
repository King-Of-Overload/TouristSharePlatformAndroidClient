package zjut.salu.share.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import zjut.salu.share.config.CuteTouristShareConfig;

/**さるの可爱自用小工具类库
 * Created by Alan(さる) on 2016/10/16.
 */

public class CommonUtils {



    /**
     * 获取聚合数据汇率app_key
     * @return app_key 聚合数据
     */
    public static String getJuheCurrencyAppkey(){
        Properties pro=new Properties();
        InputStream is;
        try{
            is= CuteTouristShareConfig.getInstance().getAssets().open("juhedata.properties");
            pro.load(is);
        }catch (IOException e){
            e.printStackTrace();
        }
        return pro.getProperty("CURRENCY_APPKEY");
    }
    /**
     * 检查是否有网络
     */
    public static Boolean isNetworkAvailable(Context context){
        NetworkInfo info=getNetworkInfo(context);
        if(info!=null){
            return info.isAvailable();
        }
        return false;
    }
    /**
     * 获取网络信息
     * @param context
     * @return
     */
    private static NetworkInfo getNetworkInfo(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
    /**
     * 检查是否是WIFI
     */
    public static boolean isWifi(Context context)
    {

        NetworkInfo info = getNetworkInfo(context);
        if (info != null)
        {
            if (info.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
        }
        return false;
    }

    /**
     * 检查是否是移动网络
     */
    public static boolean isMobile(Context context)
    {

        NetworkInfo info = getNetworkInfo(context);
        if (info != null)
        {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }


    /**
     * 检查SD卡是否存在
     */
    private static boolean checkSdCard()
    {

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 获取手机SD卡总空间
     *
     * @return
     */
    private static long getSDcardTotalSize()
    {

        if (checkSdCard())
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs mStatFs = new StatFs(path.getPath());
            /*
            long blockSizeLong = mStatFs.getBlockSizeLong();
            long blockCountLong = mStatFs.getBlockCountLong();
             */
            long blockSizeLong =0;
            long blockCountLong=0;
            if(DeviceUtils.hasJELLY_BEAN_MR2()){
                blockSizeLong = mStatFs.getBlockSizeLong();
                blockCountLong = mStatFs.getBlockCountLong();
            }else{
                blockSizeLong=mStatFs.getBlockSize();
                blockCountLong=mStatFs.getBlockCount();
            }
            return blockSizeLong * blockCountLong;
        } else
        {
            return 0;
        }
    }

    /**
     * 获取SDka可用空间
     *
     * @return
     */
    private static long getSDcardAvailableSize()
    {

        if (checkSdCard())
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs mStatFs = new StatFs(path.getPath());
            long blockSizeLong = mStatFs.getBlockSize();
            long availableBlocksLong = mStatFs.getAvailableBlocks();
            return blockSizeLong * availableBlocksLong;
        } else
            return 0;
    }


    /**
     * 获取手机内部存储总空间
     *
     * @return
     */
    public static long getPhoneTotalSize()
    {

        if (!checkSdCard())
        {
            File path = Environment.getDataDirectory();
            StatFs mStatFs = new StatFs(path.getPath());
            long blockSizeLong = mStatFs.getBlockSize();
            long blockCountLong = mStatFs.getBlockCount();
            return blockSizeLong * blockCountLong;
        } else
            return getSDcardTotalSize();
    }


    /**
     * 获取手机内存存储可用空间
     *
     * @return
     */
    public static long getPhoneAvailableSize()
    {

        if (!checkSdCard())
        {
            File path = Environment.getDataDirectory();
            StatFs mStatFs = new StatFs(path.getPath());
            long blockSizeLong = mStatFs.getBlockSize();
            long availableBlocksLong = mStatFs.getAvailableBlocks();
            return blockSizeLong * availableBlocksLong;
        } else
            return getSDcardAvailableSize();
    }
}
