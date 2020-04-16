package com.kinsin.demowebsockets.entity;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/4/3.
 * PS: Not easy to write code, please indicate.
 */

/**
 * 好友请求
 */
public class RequestFriend {
    private int id;
    private String uid1;
    private String uid2;
    private String send_time;
    private String nick_name;
    private int iswork;

    public RequestFriend() {
    }

    public RequestFriend(String uid1, String uid2) {
        this.uid1 = uid1;
        this.uid2 = uid2;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public RequestFriend(String uid1, String uid2, String send_time) {
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.send_time = send_time;
    }

    public RequestFriend(int id, String uid1, String uid2, int iswork) {
        this.id = id;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.iswork = iswork;
    }

    @Override
    public String toString() {
        return "RequestFriend{" +
                "id=" + id +
                ", uid1='" + uid1 + '\'' +
                ", uid2='" + uid2 + '\'' +
                ", send_time='" + send_time + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", iswork=" + iswork +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public int getIswork() {
        return iswork;
    }

    public void setIswork(int iswork) {
        this.iswork = iswork;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }
}
