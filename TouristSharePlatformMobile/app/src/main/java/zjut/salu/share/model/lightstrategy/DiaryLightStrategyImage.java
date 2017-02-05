package zjut.salu.share.model.lightstrategy;

import java.io.Serializable;

/**旅行日记的图片对象
 * Created by Alan-Mac on 2017/02/05.
 */

public class DiaryLightStrategyImage implements Serializable{
    private String diaryimgid;
    private String diaryimgurl;
    private DiaryLightStrategy diaryLightStrategy;

    public DiaryLightStrategy getDiaryLightStrategy() {
        return diaryLightStrategy;
    }

    public void setDiaryLightStrategy(DiaryLightStrategy diaryLightStrategy) {
        this.diaryLightStrategy = diaryLightStrategy;
    }

    public String getDiaryimgid() {
        return diaryimgid;
    }

    public void setDiaryimgid(String diaryimgid) {
        this.diaryimgid = diaryimgid;
    }

    public String getDiaryimgurl() {
        return diaryimgurl;
    }

    public void setDiaryimgurl(String diaryimgurl) {
        this.diaryimgurl = diaryimgurl;
    }
}
