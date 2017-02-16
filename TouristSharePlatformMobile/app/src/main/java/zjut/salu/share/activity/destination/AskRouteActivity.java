package zjut.salu.share.activity.destination;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.activity.common.CommonWebActivity;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.model.local.TourismAttraction;

/**
 * 问路卡控制层
 * @author Alan-Mac
 */
public class AskRouteActivity extends RxBaseActivity{
    @Bind(R.id.tv_name)TextView nameTV;
    @Bind(R.id.tv_address)TextView addressTV;
    @Bind(R.id.tv_foreign_name)TextView foreignTV;

    public static void launch(Activity activity, TourismAttraction attraction){
        Intent mIntent = new Intent(activity, AskRouteActivity.class);
        mIntent.putExtra("attraction",attraction);
        activity.startActivity(mIntent);
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_ask_route;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent=getIntent();
        TourismAttraction attraction= (TourismAttraction) intent.getSerializableExtra("attraction");
        nameTV.setText(attraction.getTourismname());
        addressTV.setText(attraction.getTourismaddress());
        foreignTV.setText(attraction.getTourismforeignname());
    }

    @Override
    public void initToolBar() {

    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }
}
