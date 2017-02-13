package zjut.salu.share.adapter.destination;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import zjut.salu.share.R;
import zjut.salu.share.adapter.helper.AbsRecyclerViewAdapter;
import zjut.salu.share.model.city.City;

/**城市列表adapter
 * Created by Salu on 2017/1/19.
 */

public class CityRecycleAdapter extends AbsRecyclerViewAdapter {
    private List<City> list;

    public CityRecycleAdapter(RecyclerView recyclerView,List<City> list) {
        super(recyclerView);
        this.list=list;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_common_single_text,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            City city=list.get(position);
            itemViewHolder.nameTV.setText(city.getCityname());
        }
        super.onBindViewHolder(holder, position);
    }

    private class ItemViewHolder extends ClickableViewHolder{
        TextView nameTV;
        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTV=$(R.id.tv_text_common);
        }
    }
}
