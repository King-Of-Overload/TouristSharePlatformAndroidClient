package zjut.salu.share.adapter.destination;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import zjut.salu.share.R;
import zjut.salu.share.adapter.helper.AbsRecyclerViewAdapter;
import zjut.salu.share.model.Provinces;

/**省份适配器
 * Created by Salu on 2017/1/19.
 */

public class ProvincesRecycleAdapter extends AbsRecyclerViewAdapter {
    private List<Provinces> list;

    public ProvincesRecycleAdapter(RecyclerView recyclerView, List<Provinces> list) {
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
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            Provinces province=list.get(position);
            itemViewHolder.nameTV.setText(province.getProvincename());
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ItemViewHolder extends ClickableViewHolder{
        TextView nameTV;
        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTV=$(R.id.tv_text_common);
        }
    }
}
