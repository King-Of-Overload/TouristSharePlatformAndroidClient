package zjut.salu.share.model.skillacademy;

import java.io.Serializable;

/**热门标签
 * Created by Alan on 2016/11/2.
 */

public class SkillAcademyHotTag implements Serializable {
    private int skillhottagid;
    private String skillhottagname;
    private int clickednum;

    public int getSkillhottagid() {
        return skillhottagid;
    }

    public void setSkillhottagid(int skillhottagid) {
        this.skillhottagid = skillhottagid;
    }

    public String getSkillhottagname() {
        return skillhottagname;
    }

    public void setSkillhottagname(String skillhottagname) {
        this.skillhottagname = skillhottagname;
    }

    public int getClickednum() {
        return clickednum;
    }

    public void setClickednum(int clickednum) {
        this.clickednum = clickednum;
    }
}
