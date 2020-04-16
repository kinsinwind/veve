package com.kinsin.demowebsockets.entity;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/4/1.
 * PS: Not easy to write code, please indicate.
 */
public class FriendMap {
    private String account;//账号
    private String headIconUrl;//头像
    private String nickName;//昵称
    private String lastMsg;//最近消息
    private String lastTime;//最近时间
    private int noReadNum;//未读消息数量

    public FriendMap() {
    }

    public FriendMap(String account, String headIconUrl, String nickName, String lastMsg, String lastTime, int noReadNum) {
        this.account = account;
        this.headIconUrl = headIconUrl;
        this.nickName = nickName;
        this.lastMsg = lastMsg;
        this.lastTime = lastTime;
        this.noReadNum = noReadNum;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getHeadIconUrl() {
        return headIconUrl;
    }

    public void setHeadIconUrl(String headIconUrl) {
        this.headIconUrl = headIconUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
    }

    @Override
    public String toString() {
        return "FriendMap{" +
                "headIconUrl='" + headIconUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", lastMsg='" + lastMsg + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", noReadNum=" + noReadNum +
                '}';
    }
}
