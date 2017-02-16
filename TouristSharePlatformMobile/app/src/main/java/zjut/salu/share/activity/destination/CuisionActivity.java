package zjut.salu.share.activity.destination;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import zjut.salu.share.R;
import zjut.salu.share.adapter.destination.CuisionRecycleAdapter;
import zjut.salu.share.base.RxBaseActivity;
import zjut.salu.share.event.MyRecycleViewScrollListener;
import zjut.salu.share.model.local.Cuision;

/**
 * 菜品列表控制层
 */
public class CuisionActivity extends RxBaseActivity{

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.recycle)RecyclerView recyclerView;

    public static void launch(Activity activity, List<Cuision> cuisions){
        Intent mIntent = new Intent(activity, CuisionActivity.class);
        mIntent.putExtra("cuisions",(Serializable)cuisions);
        activity.startActivity(mIntent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cuision;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        WeakReference<Activity> reference = new WeakReference<>(this);
        ImageLoader imageLoader = ImageLoader.getInstance();
        Intent intent=getIntent();
        List<Cuision> cuisions = (List<Cuision>) intent.getSerializableExtra("cuisions");
        recyclerView.setLayoutManager(new LinearLayoutManager(reference.get()));
        CuisionRecycleAdapter adapter = new CuisionRecycleAdapter(recyclerView, cuisions, imageLoader);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new MyRecycleViewScrollListener(null,null));
    }

    @Override
    public void initToolBar() {
        toolbar.setTitle(R.string.food_recommend_text);
        toolbar.setNavigationIcon(R.drawable.action_button_back_pressed_light);
        toolbar.setNavigationOnClickListener(v->finish());
    }
}
