package zjut.salu.share.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**首页轮播图pojo映射
 * Created by Alan on 2016/10/31.
 */
@Entity(generateConstructors = false)
public class IndexBannerBean implements Serializable {
    private static final long serialVersionUID = 1L; //这个是缺省的
    @Id
    private String id;
    private String coverImage;
    private String name;
    private String p1;
    private String p2;
    private String p3;
    private String p4;
    private String type;//strategy album skillacademy

    public IndexBannerBean() {
    }

    @Keep
    public String getId() {
        return id;
    }
    @Keep
    public void setId(String id) {
        this.id = id;
    }
    @Keep
    public String getCoverImage() {
        return coverImage;
    }
    @Keep
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
    @Keep
    public String getName() {
        return name;
    }
    @Keep
    public void setName(String name) {
        this.name = name;
    }
    @Keep
    public String getP1() {
        return p1;
    }
    @Keep
    public void setP1(String p1) {
        this.p1 = p1;
    }
    @Keep
    public String getP2() {
        return p2;
    }
    @Keep
    public void setP2(String p2) {
        this.p2 = p2;
    }
    @Keep
    public String getP3() {
        return p3;
    }
    @Keep
    public void setP3(String p3) {
        this.p3 = p3;
    }
    @Keep
    public String getP4() {
        return p4;
    }
    @Keep
    public void setP4(String p4) {
        this.p4 = p4;
    }
    @Keep
    public String getType() {
        return type;
    }
    @Keep
    public void setType(String type) {
        this.type = type;
    }
}
