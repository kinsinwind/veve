package com.kinsin.demowebsockets.entity;

public class User {
    private String account;//账号
    private String password;//密码
    private String email;//邮箱
    private String nick_name;//昵称
    private String headicon;//头像
    private int net_status;//在线状态

    public User(String account, String password, String email, String nick_name, int net_status) {
        this.account = account;
        this.password = password;
        this.email = email;
        this.nick_name = nick_name;
        this.net_status = net_status;
    }

    public User() {
    }


    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", headicon='" + headicon + '\'' +
                ", net_status=" + net_status +
                '}';
    }

    public String getHeadicon() {
        return headicon;
    }

    public void setHeadicon(String headicon) {
        this.headicon = headicon;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public int getNet_status() {
        return net_status;
    }

    public void setNet_status(int net_status) {
        this.net_status = net_status;
    }
}
