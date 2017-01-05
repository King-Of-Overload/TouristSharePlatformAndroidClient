package zjut.salu.share.widget.ScaleView;

import android.os.Build;
import android.view.View;

/**saluの工具哦
 * Created by Salu on 2016/11/15.
 */

public class Compat {
    private static final int SIXTY_FPS_INTERVAL = 1000 / 60;

    public static void postOnAnimation(View view, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            SDK16.postOnAnimation(view, runnable);
        } else {
            view.postDelayed(runnable, SIXTY_FPS_INTERVAL);
        }
    }
}
