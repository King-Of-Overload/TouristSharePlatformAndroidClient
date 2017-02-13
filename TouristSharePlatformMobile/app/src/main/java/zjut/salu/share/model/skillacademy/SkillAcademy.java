package zjut.salu.share.model.skillacademy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zjut.salu.share.model.TripUser;

/**技法学院pojo
 * Created by Alan on 2016/11/2.
 */

public class SkillAcademy implements Serializable {
    private String skilid;
    private String skilltitle;
    private String skillcontent;
    private Date skilldate;
    private int clickednum;
    private int isessence;
    private String skillplaintext;
    private TripUser user;//多对一对象
    private List<SkillAcademySecondTag> secondTagList=new ArrayList<>();//多对多，文章与相关话题
    private Date skillnumupdatetime;
    private int skilltempclickednum;
    private String coverImage;//额外参数,封面

    public List<SkillAcademySecondTag> getSecondTagList() {
        return secondTagList;
    }

    public void setSecondTagList(List<SkillAcademySecondTag> secondTagList) {
        this.secondTagList = secondTagList;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSkilid() {
        return skilid;
    }

    public void setSkilid(String skilid) {
        this.skilid = skilid;
    }

    public String getSkilltitle() {
        return skilltitle;
    }

    public void setSkilltitle(String skilltitle) {
        this.skilltitle = skilltitle;
    }

    public String getSkillcontent() {
        return skillcontent;
    }

    public void setSkillcontent(String skillcontent) {
        this.skillcontent = skillcontent;
    }

    public Date getSkilldate() {
        return skilldate;
    }

    public void setSkilldate(Date skilldate) {
        this.skilldate = skilldate;
    }

    public int getClickednum() {
        return clickednum;
    }

    public void setClickednum(int clickednum) {
        this.clickednum = clickednum;
    }

    public int getIsessence() {
        return isessence;
    }

    public void setIsessence(int isessence) {
        this.isessence = isessence;
    }

    public String getSkillplaintext() {
        return skillplaintext;
    }

    public void setSkillplaintext(String skillplaintext) {
        this.skillplaintext = skillplaintext;
    }

    public TripUser getUser() {
        return user;
    }

    public void setUser(TripUser user) {
        this.user = user;
    }



    public Date getSkillnumupdatetime() {
        return skillnumupdatetime;
    }

    public void setSkillnumupdatetime(Date skillnumupdatetime) {
        this.skillnumupdatetime = skillnumupdatetime;
    }

    public int getSkilltempclickednum() {
        return skilltempclickednum;
    }

    public void setSkilltempclickednum(int skilltempclickednum) {
        this.skilltempclickednum = skilltempclickednum;
    }
}
