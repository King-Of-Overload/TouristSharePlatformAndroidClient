package zjut.salu.share.model.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**商品一级分类
 * Created by Alan on 2016/10/27.
 */

public class ProductCategory implements Serializable {
    private int pcategoryid;
    private String pcategoryname;
    private List<ProductSecondCategory> secondCategories=new ArrayList<>();

    public List<ProductSecondCategory> getSecondCategories() {
        return secondCategories;
    }

    public void setSecondCategories(List<ProductSecondCategory> secondCategories) {
        this.secondCategories = secondCategories;
    }

    public int getPcategoryid() {
        return pcategoryid;
    }

    public void setPcategoryid(int pcategoryid) {
        this.pcategoryid = pcategoryid;
    }

    public String getPcategoryname() {
        return pcategoryname;
    }

    public void setPcategoryname(String pcategoryname) {
        this.pcategoryname = pcategoryname;
    }
}
