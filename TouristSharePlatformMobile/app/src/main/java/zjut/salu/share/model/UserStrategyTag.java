package zjut.salu.share.model;

import java.io.Serializable;

/**用户攻略标签
 * Created by Alan on 2016/10/29.
 */

public class UserStrategyTag implements Serializable{
    private int ustrategytagid;
    private String ustrategytagname;
    private int ustrategyclickednum;

    public int getUstrategytagid() {
        return ustrategytagid;
    }

    public void setUstrategytagid(int ustrategytagid) {
        this.ustrategytagid = ustrategytagid;
    }

    public String getUstrategytagname() {
        return ustrategytagname;
    }

    public void setUstrategytagname(String ustrategytagname) {
        this.ustrategytagname = ustrategytagname;
    }

    public int getUstrategyclickednum() {
        return ustrategyclickednum;
    }

    public void setUstrategyclickednum(int ustrategyclickednum) {
        this.ustrategyclickednum = ustrategyclickednum;
    }
}
