package zjut.salu.share.model;

import java.io.Serializable;

/**大洲名
 * Created by Salu on 2017/1/19.
 */

public class Destination implements Serializable {
    private int desid;
    private String desname;

    public int getDesid() {
        return desid;
    }

    public void setDesid(int desid) {
        this.desid = desid;
    }

    public String getDesname() {
        return desname;
    }

    public void setDesname(String desname) {
        this.desname = desname;
    }
}
