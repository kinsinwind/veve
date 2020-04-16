package com.kinsin.demowebsockets.entity;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/4/1.
 * PS: Not easy to write code, please indicate.
 */
public class Msg {
    private String uid1;
    private String uid2;
    private String content;
    private int isRead;
    private String send_time;

    public Msg() {
    }

    public Msg(String uid1, String uid2, String content, int isRead, String send_time) {
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.content = content;
        this.isRead = isRead;
        this.send_time = send_time;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "uid1='" + uid1 + '\'' +
                ", uid2='" + uid2 + '\'' +
                ", content='" + content + '\'' +
                ", isRead=" + isRead +
                ", send_time='" + send_time + '\'' +
                '}';
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }
}
