package zjut.salu.share.model.lightstrategy;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import zjut.salu.share.model.TripUser;

/**轻游记-----旅行日记
 * Created by Alan-Mac on 2017/02/05.
 */

public class DiaryLightStrategy implements Serializable{
    private String diaryid;
    private String diarycontent;
    private TripUser user;
    private List<DiaryLightStrategyImage> images;
    private Date diarytime;
    private int clicknum;
    private int isesence;//是否热门

    public String getDiaryid() {
        return diaryid;
    }

    public void setDiaryid(String diaryid) {
        this.diaryid = diaryid;
    }

    public String getDiarycontent() {
        return diarycontent;
    }

    public void setDiarycontent(String diarycontent) {
        this.diarycontent = diarycontent;
    }

    public TripUser getUser() {
        return user;
    }

    public void setUser(TripUser user) {
        this.user = user;
    }

    public List<DiaryLightStrategyImage> getImages() {
        return images;
    }

    public void setImages(List<DiaryLightStrategyImage> images) {
        this.images = images;
    }

    public Date getDiarytime() {
        return diarytime;
    }

    public void setDiarytime(Date diarytime) {
        this.diarytime = diarytime;
    }

    public int getClicknum() {
        return clicknum;
    }

    public void setClicknum(int clicknum) {
        this.clicknum = clicknum;
    }

    public int getIsesence() {
        return isesence;
    }

    public void setIsesence(int isesence) {
        this.isesence = isesence;
    }
}
