package zjut.salu.share.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import zjut.salu.share.activity.UserInfoActivity;
import zjut.salu.share.activity.UserInfoBigImageActivity;
import zjut.salu.share.model.NineImage;
import zjut.salu.share.utils.DeviceUtils;

/**九宫格图片布局
 * Created by Salu on 2016/11/19.
 */

public class NineGridLayout extends ViewGroup{
    /**
     * 图片之间的间隔
     */
    private int gap = 5;
    private int columns;//
    private int rows;//
    private List listData;
    private int totalWidth;

    public NineGridLayout(Context context) {
        super(context);
    }

    public NineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        totalWidth=DeviceUtils.getScreenWidth(getContext())-DeviceUtils.dip2px(getContext(),80);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
    private void layoutChildrenView(){
        int childrenCount = listData.size();

        int singleWidth = (totalWidth - gap * (3 - 1)) / 3;
        int singleHeight = singleWidth;

        //根据子view数量确定高度
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = singleHeight * rows + gap * (rows - 1);
        setLayoutParams(params);

        for (int i = 0; i < childrenCount; i++) {
            NineCustomImageView childrenView = (NineCustomImageView) getChildAt(i);
            childrenView.setTag(((NineImage)listData.get(i)));
            childrenView.setImageUrl(((NineImage) listData.get(i)).getUrl());
            childrenView.setOnClickListener(new NineImageClickListener());
            int[] position = findPosition(i);
            int left = (singleWidth + gap) * position[1];
            int top = (singleHeight + gap) * position[0];
            int right = left + singleWidth;
            int bottom = top + singleHeight;

            childrenView.layout(left, top, right, bottom);
        }

    }

    /**
     * 每张图片的点击事件
     */
    private class NineImageClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            //跳转到查看大图界面
            Activity activity=UserInfoActivity.getUserInfoReference().get();
            Intent intent = new Intent(activity,UserInfoBigImageActivity.class);
            Bundle bundle=new Bundle();
            NineImage position= (NineImage) v.getTag();
            bundle.putInt("position",Integer.valueOf(position.getPosition()));
            List<NineImage> myList=listData;
            ArrayList<String> result=new ArrayList<>();
            for(NineImage nineImage:myList){
                result.add(nineImage.getUrl());
            }
            bundle.putStringArrayList("nineImgRes",result);
            intent.putExtras(bundle);
            activity.startActivity(intent);
        }
    }


    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i * columns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }


    public void setImagesData(List<NineImage> lists) {
        if (lists == null || lists.isEmpty()) {
            return;
        }
        //初始化布局
        generateChildrenLayout(lists.size());
        //这里做一个重用view的处理
        if (listData == null) {
            int i = 0;
            while (i < lists.size()) {
                NineCustomImageView iv = generateImageView();
                addView(iv,generateDefaultLayoutParams());
                i++;
            }

        } else {
            int oldViewCount = listData.size();
            int newViewCount = lists.size();
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount - 1, oldViewCount - newViewCount);
            } else if (oldViewCount < newViewCount) {
                for (int i = 0; i < newViewCount - oldViewCount; i++) {
                    NineCustomImageView iv = generateImageView();
                    addView(iv,generateDefaultLayoutParams());
                }
            }
        }
        listData = lists;
        layoutChildrenView();
    }


    /**
     * 根据图片个数确定行列数量
     * 对应关系如下
     * num        row        column
     * 1           1        1
     * 2           1        2
     * 3           1        3
     * 4           2        2
     * 5           2        3
     * 6           2        3
     * 7           3        3
     * 8           3        3
     * 9           3        3
     *
     * @param length
     */
    private void generateChildrenLayout(int length) {
        if (length <= 3) {
            rows = 1;
            columns = length;
        } else if (length <= 6) {
            rows = 2;
            columns = 3;
            if (length == 4) {
                columns = 2;
            }
        } else {
            rows = 3;
            columns = 3;
        }
    }

    private NineCustomImageView generateImageView() {
        NineCustomImageView iv = new NineCustomImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        iv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
        return iv;
    }


}
