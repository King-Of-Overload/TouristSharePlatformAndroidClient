package zjut.salu.share.model;

import java.io.Serializable;

import zjut.salu.share.model.city.City;

/**
 * Created by Alan on 2016/10/21.
 */

public class TripUser implements Serializable {
    private String userid;
    private String username;
    private String userpassword;
    private String useremail;
    private int userstate;
    private String useractivecode;
    private String headerimage;//用户头像
    private String phone;
    private String sex;
    private String usercookievalue;
    private int ismaster;
    private String usignature;
    private String mobilelogincode;
    private int focusNum;//关注数
    private int followNum;//粉丝数
    private City city;
    private String cityString;

    private int focused;//是否关注

    public int getFocused() {
        return focused;
    }

    public void setFocused(int focused) {
        this.focused = focused;
    }

    public String getCityString() {
        return cityString;
    }

    public void setCityString(String cityString) {
        this.cityString = cityString;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getFocusNum() {
        return focusNum;
    }

    public void setFocusNum(int focusNum) {
        this.focusNum = focusNum;
    }

    public int getFollowNum() {
        return followNum;
    }

    public void setFollowNum(int followNum) {
        this.followNum = followNum;
    }

    public String getMobilelogincode() {
        return mobilelogincode;
    }

    public void setMobilelogincode(String mobilelogincode) {
        this.mobilelogincode = mobilelogincode;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public int getUserstate() {
        return userstate;
    }

    public void setUserstate(int userstate) {
        this.userstate = userstate;
    }

    public String getUseractivecode() {
        return useractivecode;
    }

    public void setUseractivecode(String useractivecode) {
        this.useractivecode = useractivecode;
    }

    public String getHeaderimage() {
        return headerimage;
    }

    public void setHeaderimage(String headerimage) {
        this.headerimage = headerimage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUsercookievalue() {
        return usercookievalue;
    }

    public void setUsercookievalue(String usercookievalue) {
        this.usercookievalue = usercookievalue;
    }

    public int getIsmaster() {
        return ismaster;
    }

    public void setIsmaster(int ismaster) {
        this.ismaster = ismaster;
    }

    public String getUsignature() {
        return usignature;
    }

    public void setUsignature(String usignature) {
        this.usignature = usignature;
    }

    public TripUser() {
    }
}
