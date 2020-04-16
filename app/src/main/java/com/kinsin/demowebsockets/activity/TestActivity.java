package com.kinsin.demowebsockets.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.kinsin.demowebsockets.R;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {
    NotificationManager manager;
    Notification notification;
    private int ids=0;
    private int noRead=1;
    Map<String,Integer> map=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void onclick(View view) {
        send("killa");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void test(View view) {
        send("kinsin");
    }


    /**
     * 实现效果：
     * 类似QQ
     * 当有一个人给你发消息时，显示消息发送人和消息内容，并统计新消息数量，点击可进入该联系人聊天界面
     * 当有多个人给你发消息时，显示有多少人一共发了多少新消息，点击会进入消息列表
     *
     * 实现思路：
     * 通知id，用map来存储，key为发送人账号，这样可以确定该通知id与该用户绑定，
     * 通知id从1开始，当消息发送人超过1时，id固定为1，防止有太多通知显示在通知栏中
     *
     * @param account
     */
    private void send(String account){
        if(map.get(account)==null){
            map.put(account,++ids);
        }
        String id = "veve1";//为channel服务
        String name="veve消息通知";//为channel服务
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder=null;
        int currentId;
        if(map.size()>1){
            currentId=1;
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("veve")
                    .setContentText("有"+map.size()+"个联系人给你发送了 "+noRead+" 条新消息")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true);
        }else {
            currentId=map.get(account);
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(account+"("+noRead+"条新消息)")
                    .setContentText("消息测试"+ids)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setSound(Uri.parse("android.resource://" + TestActivity.this.getPackageName() + "/" + R.raw.notice), null);
            manager.createNotificationChannel(mChannel);
            notificationBuilder.setChannelId(id);
        }

        Intent intent = new Intent(TestActivity.this,TestActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(TestActivity.this, 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);
        notification = notificationBuilder.build();

        manager.notify(currentId,notification);
        noRead++;
    }

    public void test2(View view) {
        send("lucy");
    }
}
