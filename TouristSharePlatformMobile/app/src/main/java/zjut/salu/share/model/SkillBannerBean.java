package zjut.salu.share.model;

import java.io.Serializable;

/**行摄攻略轮播图pojo
 * Created by Alan on 2016/11/2.
 */

public class SkillBannerBean implements Serializable {
    private String skillId;
    private String bannerTitle;
    private String bannerDescription;
    private String bannerImage;

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getBannerTitle() {
        return bannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        this.bannerTitle = bannerTitle;
    }

    public String getBannerDescription() {
        return bannerDescription;
    }

    public void setBannerDescription(String bannerDescription) {
        this.bannerDescription = bannerDescription;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }
}
