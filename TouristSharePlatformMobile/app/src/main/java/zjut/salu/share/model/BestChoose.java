package zjut.salu.share.model;

import java.io.Serializable;
import java.util.Date;

/**精选数据pojo
 * Created by Salu on 2016/12/13.
 */

public class BestChoose implements Serializable {
    private String bestid;
    private String bestname;
    private String bestcontent;
    private String bestplaintext;
    private Date bestdate;
    private int bestishot;
    private String bestcover;
    private int bestclickcount;

    public String getBestid() {
        return bestid;
    }

    public void setBestid(String bestid) {
        this.bestid = bestid;
    }

    public String getBestname() {
        return bestname;
    }

    public void setBestname(String bestname) {
        this.bestname = bestname;
    }

    public String getBestcontent() {
        return bestcontent;
    }

    public void setBestcontent(String bestcontent) {
        this.bestcontent = bestcontent;
    }

    public String getBestplaintext() {
        return bestplaintext;
    }

    public void setBestplaintext(String bestplaintext) {
        this.bestplaintext = bestplaintext;
    }

    public Date getBestdate() {
        return bestdate;
    }

    public void setBestdate(Date bestdate) {
        this.bestdate = bestdate;
    }

    public int getBestishot() {
        return bestishot;
    }

    public void setBestishot(int bestishot) {
        this.bestishot = bestishot;
    }

    public String getBestcover() {
        return bestcover;
    }

    public void setBestcover(String bestcover) {
        this.bestcover = bestcover;
    }

    public int getBestclickcount() {
        return bestclickcount;
    }

    public void setBestclickcount(int bestclickcount) {
        this.bestclickcount = bestclickcount;
    }
}
