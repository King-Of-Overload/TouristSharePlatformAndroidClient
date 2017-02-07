package zjut.salu.share.model.lightstrategy.banggume;

import java.io.Serializable;

/**小视频tag
 * Created by Alan-Mac on 2017/02/07.
 */

public class BanggumeTag implements Serializable{
    private int banggumetagid;
    private String banggumetagname;
    private int clicknum;

    public int getBanggumetagid() {
        return banggumetagid;
    }

    public void setBanggumetagid(int banggumetagid) {
        this.banggumetagid = banggumetagid;
    }

    public String getBanggumetagname() {
        return banggumetagname;
    }

    public void setBanggumetagname(String banggumetagname) {
        this.banggumetagname = banggumetagname;
    }

    public int getClicknum() {
        return clicknum;
    }

    public void setClicknum(int clicknum) {
        this.clicknum = clicknum;
    }
}
