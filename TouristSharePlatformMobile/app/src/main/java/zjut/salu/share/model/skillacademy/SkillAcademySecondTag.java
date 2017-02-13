package zjut.salu.share.model.skillacademy;

import java.io.Serializable;

/**技法学院二级分类
 * Created by Alan on 2016/11/2.
 */

public class SkillAcademySecondTag implements Serializable {
    private int skillsecondid;
    private String skillsecondname;
    private int skillsecondclickednum;
    private SkillAcademyHotTag hotTag;//多对一对象

    public int getSkillsecondid() {
        return skillsecondid;
    }

    public void setSkillsecondid(int skillsecondid) {
        this.skillsecondid = skillsecondid;
    }

    public String getSkillsecondname() {
        return skillsecondname;
    }

    public void setSkillsecondname(String skillsecondname) {
        this.skillsecondname = skillsecondname;
    }

    public int getSkillsecondclickednum() {
        return skillsecondclickednum;
    }

    public void setSkillsecondclickednum(int skillsecondclickednum) {
        this.skillsecondclickednum = skillsecondclickednum;
    }

    public SkillAcademyHotTag getHotTag() {
        return hotTag;
    }

    public void setHotTag(SkillAcademyHotTag hotTag) {
        this.hotTag = hotTag;
    }
}
