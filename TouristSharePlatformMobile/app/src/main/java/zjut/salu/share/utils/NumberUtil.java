package zjut.salu.share.utils;

/**数字工具类
 * Created by Salu on 2017/1/29.
 */

public class NumberUtil  {
    public static String converString(int num)
    {

        if (num < 100000)
        {
            return String.valueOf(num);
        }
        String unit = "万";
        double newNum = num / 10000.0;

        String numStr = String.format("%." + 1 + "f", newNum);
        return numStr + unit;
    }
}
