package zjut.salu.share.model.user;

import java.io.Serializable;

import zjut.salu.share.model.TripUser;

/**用户收藏
 * Created by Alan-Mac on 2017/02/16.
 */

public class UserFavorite implements Serializable {

    private int favid;
    private TripUser user;
    private String favname;
    private String favcover;
    private String type;
    private String entityid;

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public int getFavid() {
        return favid;
    }

    public void setFavid(int favid) {
        this.favid = favid;
    }

    public TripUser getUser() {
        return user;
    }

    public void setUser(TripUser user) {
        this.user = user;
    }

    public String getFavname() {
        return favname;
    }

    public void setFavname(String favname) {
        this.favname = favname;
    }

    public String getFavcover() {
        return favcover;
    }

    public void setFavcover(String favcover) {
        this.favcover = favcover;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
