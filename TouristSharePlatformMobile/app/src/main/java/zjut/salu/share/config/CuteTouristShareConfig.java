package zjut.salu.share.config;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.baidu.mapapi.SDKInitializer;
import com.bilibili.magicasakura.utils.ThemeUtils;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import okhttp3.OkHttpClient;
import zjut.salu.share.R;
import zjut.salu.share.utils.ThemeHelper;
import zjut.salu.share.utils.greendao.GreenDaoDBHelper;

/**芳草寻源的所有业务逻辑配置皆在此哦
 * Created by Alan(さる) on 2016/10/16.
 */

public class CuteTouristShareConfig extends MultiDexApplication implements ThemeUtils.switchColor{
    public static CuteTouristShareConfig mInstance;//配置文件类对象

    public static CuteTouristShareConfig getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        // 初始化主题切换
        ThemeUtils.setSwitchColor(this);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);//总内存
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this)
                .writeDebugLogs()
                .memoryCache(new LruMemoryCache(maxMemory / 6))//可以通过自己的内存缓存实现
                .memoryCacheSize(4 * 1024 * 1024)//内存缓存的最大值
                //.discCacheExtraOptions(300,150,null)
                .memoryCacheSizePercentage(13)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);//初始化图片加载
        GreenDaoDBHelper.initDatabase();//初始化greenDao
        Properties pro = new Properties();
        InputStream is = null;
        try {
            is = getAssets().open("bomb.properties");
            pro.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bmob.initialize(this,pro.getProperty("applicationid"));
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
    }

    /**
     * 获得屏幕宽度
     * @param context
    * @return
     */
    public static int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getActionBarHeight(Activity activity){
        TypedValue tv = new TypedValue ();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            return  TypedValue.complexToDimensionPixelSize(tv.data,activity.getResources().getDisplayMetrics());
        }
        return -1;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context)
    {

        int statusHeight = -1;
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return statusHeight;
    }


    @Override
    public int replaceColorById(Context context, @ColorRes int colorId) {
        if (ThemeHelper.isDefaultTheme(context))
        {
            return context.getResources().getColor(colorId);
        }
        String theme = getTheme(context);
        if (theme != null)
        {
            colorId = getThemeColorId(context, colorId, theme);
        }
        return context.getResources().getColor(colorId);
    }

    @Override
    public int replaceColor(Context context, @ColorInt int color) {
        if (ThemeHelper.isDefaultTheme(context))
        {
            return color;
        }
        String theme = getTheme(context);
        int colorId = -1;

        if (theme != null)
        {
            colorId = getThemeColor(context, color, theme);
        }
        return colorId != -1 ? getResources().getColor(colorId) : color;
    }

    private String getTheme(Context context)
    {

        if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_STORM)
        {
            return "blue";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_HOPE)
        {
            return "purple";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_WOOD)
        {
            return "green";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_LIGHT)
        {
            return "green_light";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_THUNDER)
        {
            return "yellow";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_SAND)
        {
            return "orange";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_FIREY)
        {
            return "red";
        }
        return null;
    }

    private
    @ColorRes
    int getThemeColorId(Context context, int colorId, String theme)
    {

        switch (colorId)
        {
            case R.color.theme_color_primary:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case R.color.theme_color_primary_dark:
                return context.getResources().getIdentifier(theme + "_dark", "color", getPackageName());
            case R.color.theme_color_primary_trans:
                return context.getResources().getIdentifier(theme + "_trans", "color", getPackageName());
        }
        return colorId;
    }

    private
    @ColorRes
    int getThemeColor(Context context, int color, String theme)
    {

        switch (color)
        {
            case 0xfffb7299:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case 0xffb85671:
                return context.getResources().getIdentifier(theme + "_dark", "color", getPackageName());
            case 0x99f0486c:
                return context.getResources().getIdentifier(theme + "_trans", "color", getPackageName());
        }
        return -1;
    }
}
