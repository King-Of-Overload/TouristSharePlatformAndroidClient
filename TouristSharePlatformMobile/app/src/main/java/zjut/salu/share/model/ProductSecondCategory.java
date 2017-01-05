package zjut.salu.share.model;

import java.io.Serializable;

/**商品二级分类
 * Created by Alan on 2016/10/27.
 */

public class ProductSecondCategory implements Serializable {
    private int pcsid;
    private String pcsname;
    private ProductCategory category;//多对一对象，一级分类

    public int getPcsid() {
        return pcsid;
    }

    public void setPcsid(int pcsid) {
        this.pcsid = pcsid;
    }

    public String getPcsname() {
        return pcsname;
    }

    public void setPcsname(String pcsname) {
        this.pcsname = pcsname;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }
}
