package zjut.salu.share.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import zjut.salu.share.model.TripUser;

/**订单pojo
 * Created by Alan on 2016/10/27.
 */

public class OrderItem implements Serializable{
    private String oid;
    private BigDecimal oprice;
    private String otime;
    private int ostate;
    private TripUser user;
    private Product product;
    private String paddress;

    public String getOtime() {
        return otime;
    }

    public void setOtime(String otime) {
        this.otime = otime;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public BigDecimal getOprice() {
        return oprice;
    }

    public void setOprice(BigDecimal oprice) {
        this.oprice = oprice;
    }



    public int getOstate() {
        return ostate;
    }

    public void setOstate(int ostate) {
        this.ostate = ostate;
    }

    public TripUser getUser() {
        return user;
    }

    public void setUser(TripUser user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getPaddress() {
        return paddress;
    }

    public void setPaddress(String paddress) {
        this.paddress = paddress;
    }
}
