package zjut.salu.share.model;

import java.io.Serializable;

/**货币信息
 * Created by Salu on 2016/12/8.
 */

public class Currency implements Serializable{
    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
