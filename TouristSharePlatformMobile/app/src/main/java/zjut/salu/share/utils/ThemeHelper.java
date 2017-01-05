package zjut.salu.share.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import zjut.salu.share.R;
import zjut.salu.share.config.CuteTouristShareConfig;

/**主题切换帮助类
 * Created by Salu on 2016/11/23.
 */

public class ThemeHelper {
    private static final String CURRENT_THEME = "theme_current";

    public static final int CRAD_SAKURA = 0x1;

    public static final int CARD_HOPE = 0x2;

    public static final int CARD_STORM = 0x3;

    public static final int CARD_WOOD = 0x4;

    public static final int CARD_LIGHT = 0x5;

    public static final int CARD_THUNDER = 0x6;

    public static final int CARD_SAND = 0x7;

    public static final int CARD_FIREY = 0x8;

    public static SharedPreferences getSharePreference(Context context)
    {

        return context.getSharedPreferences("multiple_theme", Context.MODE_PRIVATE);
    }

    public static void setTheme(Context context, int themeId)
    {

        getSharePreference(context).edit()
                .putInt(CURRENT_THEME, themeId)
                .apply();
    }

    public static int getTheme(Context context)
    {

        return getSharePreference(context).getInt(CURRENT_THEME, CRAD_SAKURA);
    }

    public static boolean isDefaultTheme(Context context)
    {

        return getTheme(context) == CRAD_SAKURA;
    }

    public static String getName(int currentTheme)
    {

        switch (currentTheme)
        {
            case CRAD_SAKURA:
                return "THE SAKURA";
            case CARD_STORM:
                return "THE STORM";
            case CARD_WOOD:
                return "THE WOOD";
            case CARD_LIGHT:
                return "THE LIGHT";
            case CARD_HOPE:
                return "THE HOPE";
            case CARD_THUNDER:
                return "THE THUNDER";
            case CARD_SAND:
                return "THE SAND";
            case CARD_FIREY:
                return "THE FIREY";
        }
        return "THE RETURN";
    }

    public static List<IDrawerItem> initDrawerContent(){
        List<IDrawerItem> results=new ArrayList<>();
        PrimaryDrawerItem item4=new PrimaryDrawerItem().withIdentifier(3).withName(R.string.fragment_setting_my_collection_text).withIcon(R.drawable.my_collect);
        PrimaryDrawerItem item5=new PrimaryDrawerItem().withIdentifier(4).withName(R.string.history_record_text).withIcon(R.drawable.ic_history_black);
        PrimaryDrawerItem item6=new PrimaryDrawerItem().withIdentifier(5).withName(R.string.focus_text).withIcon(R.drawable.ic_people_black_24dp);
        DividerDrawerItem item7=new DividerDrawerItem();
        PrimaryDrawerItem item8=new PrimaryDrawerItem().withIdentifier(6).withName(R.string.fragment_setting_user_dynamic_state_text).withIcon(R.drawable.ic_shop_black_24dp);
        PrimaryDrawerItem item9=new PrimaryDrawerItem().withIdentifier(7).withName(R.string.currency_text).withIcon(R.drawable.currency_change);
        PrimaryDrawerItem item10=new PrimaryDrawerItem().withIdentifier(8).withName(R.string.fragment_setting_user_order_text).withIcon(R.drawable.user_order);
        DividerDrawerItem item11=new DividerDrawerItem();
        PrimaryDrawerItem item12=new PrimaryDrawerItem().withIdentifier(9).withName(R.string.theme_choose_text).withIcon(R.drawable.theme_change);
        PrimaryDrawerItem item13=new PrimaryDrawerItem().withIdentifier(10).withName(R.string.my_message_text).withIcon(R.drawable.my_message);
        PrimaryDrawerItem item14=new PrimaryDrawerItem().withIdentifier(11).withName(R.string.setting_and_help_text).withIcon(R.drawable.ic_settings_black_24dp);
        results.add(item4);results.add(item5);results.add(item6);results.add(item7);results.add(item8);results.add(item9);
        results.add(item10);results.add(item11);results.add(item12);results.add(item13);results.add(item14);
        return results;
    }
}
