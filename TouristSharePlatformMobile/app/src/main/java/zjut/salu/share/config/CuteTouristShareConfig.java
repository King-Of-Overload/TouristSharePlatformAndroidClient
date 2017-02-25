package zjut.salu.share.config;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.baidu.mapapi.SDKInitializer;
import com.bilibili.magicasakura.utils.ThemeUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import okhttp3.OkHttpClient;
import zjut.salu.share.R;
import zjut.salu.share.utils.ThemeHelper;
import zjut.salu.share.utils.greendao.GreenDaoDBHelper;
import zjut.salu.share.utils.huanxindb.Constant;
import zjut.salu.share.utils.huanxindb.EaseUser;
import zjut.salu.share.utils.huanxindb.Myinfo;
import zjut.salu.share.utils.huanxindb.UserDao;

/**芳草寻源的所有业务逻辑配置皆在此哦
 * Created by Alan(しみずまさはる) on 2016/10/16.
 */

public class CuteTouristShareConfig extends MultiDexApplication implements ThemeUtils.switchColor{
    public static CuteTouristShareConfig mInstance;//配置文件类对象
    private UserDao userDao;
    private String username = "";
    private boolean sdkInited = false;
    private Map<String, EaseUser> contactList;
    public static CuteTouristShareConfig getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        // 初始化环信sdk
        init(getApplicationContext());
        //EasyAR.initialize(this, key);
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
        //下载器初始化
        FileDownloader.init(getApplicationContext(), new DownloadMgrInitialParams.InitCustomMaker()
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                        .proxy(Proxy.NO_PROXY) // set proxy
                )));
    }

    /*
	 * 第一步：sdk的一些参数配置 EMOptions 第二步：将配置参数封装类 传入SDK初始化
	 */

    public void init(Context context) {
        // 第一步
        EMOptions options = initChatOptions();
        // 第二步
        boolean success = initSDK(context, options);
        if (success) {
            // 设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMClient.getInstance().setDebugMode(true);
            // 初始化数据库
            initDbDao(context);
        }
    }

    private void initDbDao(Context context) {
        userDao = new UserDao(context);
    }

    private EMOptions initChatOptions() {

        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);
        return options;
    }


    public void setCurrentUserName(String username) {
        this.username = username;
        Myinfo.getInstance(mInstance).setUserInfo(Constant.KEY_USERNAME, username);
    }

    public String getCurrentUserName() {
        if (TextUtils.isEmpty(username)) {
            username = Myinfo.getInstance(mInstance).getUserInfo(Constant.KEY_USERNAME);

        }
        return username;

    }

    public synchronized boolean initSDK(Context context, EMOptions options) {
        if (sdkInited) {
            return true;
        }

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process
        // name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(getApplicationContext().getPackageName())) {

            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }
        if (options == null) {
            EMClient.getInstance().init(context, initChatOptions());
        } else {
            EMClient.getInstance().init(context, options);
        }
        sdkInited = true;
        return true;
    }

    /**
     * check the application process name if process name is not qualified, then
     * we think it is a service process and we will not init SDK
     *
     * @param pID
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {

                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
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

    public void setContactList(Map<String, EaseUser> contactList) {

        this.contactList = contactList;

        userDao.saveContactList(new ArrayList<EaseUser>(contactList.values()));

    }

    public Map<String, EaseUser> getContactList() {

        if (contactList == null) {

            contactList = userDao.getContactList();

        }
        return contactList;

    }


    /**
            * 退出登录
    *
            * @param unbindDeviceToken
    *            是否解绑设备token(使用GCM才有)
    * @param callback
    *            callback
    */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {

        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {

                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {

                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }
}
