package zjut.salu.share.model;

import java.io.Serializable;

/**省份
 * Created by Alan on 2016/10/27.
 */

public class Provinces implements Serializable {
    private int provinceid;//主键，省份id
    private String provincename;//省份名
    private String provincekeys;//省份缩写

    public int getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(int provinceid) {
        this.provinceid = provinceid;
    }

    public String getProvincename() {
        return provincename;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }

    public String getProvincekeys() {
        return provincekeys;
    }

    public void setProvincekeys(String provincekeys) {
        this.provincekeys = provincekeys;
    }
}
