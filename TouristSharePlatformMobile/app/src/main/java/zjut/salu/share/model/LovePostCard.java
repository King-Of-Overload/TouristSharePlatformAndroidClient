package zjut.salu.share.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**明信片pojo类
 * Created by Alan on 2016/11/6.
 */

public class LovePostCard implements Serializable {
    private String loveid;
    private String lovetitle;
    private String lovecontent;
    private String lovepic;
    private int lovenumber;
    private Date lovestarttime;
    private Date lovedeadline;
    private String lovetags;
    private TripUser user;
    private int commentNumber;
    private int joinNumber;
    private List<EventParticipate> participates=new ArrayList<>();
    private List<LovePostCardComment> comments=new ArrayList<>();

    public List<LovePostCardComment> getComments() {
        return comments;
    }

    public void setComments(List<LovePostCardComment> comments) {
        this.comments = comments;
    }

    public List<EventParticipate> getParticipates() {
        return participates;
    }

    public void setParticipates(List<EventParticipate> participates) {
        this.participates = participates;
    }

    public String getLoveid() {
        return loveid;
    }

    public void setLoveid(String loveid) {
        this.loveid = loveid;
    }

    public String getLovetitle() {
        return lovetitle;
    }

    public void setLovetitle(String lovetitle) {
        this.lovetitle = lovetitle;
    }

    public String getLovecontent() {
        return lovecontent;
    }

    public void setLovecontent(String lovecontent) {
        this.lovecontent = lovecontent;
    }

    public String getLovepic() {
        return lovepic;
    }

    public void setLovepic(String lovepic) {
        this.lovepic = lovepic;
    }

    public int getLovenumber() {
        return lovenumber;
    }

    public void setLovenumber(int lovenumber) {
        this.lovenumber = lovenumber;
    }

    public Date getLovestarttime() {
        return lovestarttime;
    }

    public void setLovestarttime(Date lovestarttime) {
        this.lovestarttime = lovestarttime;
    }

    public Date getLovedeadline() {
        return lovedeadline;
    }

    public void setLovedeadline(Date lovedeadline) {
        this.lovedeadline = lovedeadline;
    }

    public String getLovetags() {
        return lovetags;
    }

    public void setLovetags(String lovetags) {
        this.lovetags = lovetags;
    }

    public TripUser getUser() {
        return user;
    }

    public void setUser(TripUser user) {
        this.user = user;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public int getJoinNumber() {
        return joinNumber;
    }

    public void setJoinNumber(int joinNumber) {
        this.joinNumber = joinNumber;
    }
}
