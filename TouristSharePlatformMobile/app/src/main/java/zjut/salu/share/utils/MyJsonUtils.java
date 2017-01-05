package zjut.salu.share.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import zjut.salu.share.model.OrderItem;
import zjut.salu.share.model.Product;

/**
 * Created by Alan on 2016/10/28.
 */

public class MyJsonUtils {
    /**
     * 从订单对象中抽取数据
     * @param array
     * @return
     * @throws JSONException
     */
    public static List<OrderItem> getOrderItemFromJSONArray(JSONArray array) throws JSONException, ParseException {
        List<OrderItem> list=new ArrayList<>();
        JSONObject object=null;
        OrderItem item=null;
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i=0;i<array.length();i++){
            object=array.getJSONObject(i);
            item=new OrderItem();
            item.setOid(object.getString("oid"));
            item.setOprice(new BigDecimal(object.getInt("oprice")));
            item.setOtime(object.getString("otime"));
            item.setOstate(object.getInt("ostate"));
            item.setPaddress(object.getString("paddress"));
            JSONObject productObject=object.getJSONObject("product");
            Log.i("PRODUCTOBJECT",productObject.toString());
            Product product=new Product();
            product.setPid(productObject.getString("pid"));
            product.setMarketprice(new BigDecimal(productObject.getInt("marketprice")));
            product.setPname(productObject.getString("pname"));
            product.setShopprice(new BigDecimal(productObject.getInt("shopprice")));
            product.setCoverImage(object.getString("coverImage"));
            item.setProduct(product);
            list.add(item);
        }
        return list;
    }
}
