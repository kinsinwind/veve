package com.kinsin.demowebsockets.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.activity.ChatDetailsActivity;
import com.kinsin.demowebsockets.activity.MainActivity;
import com.kinsin.demowebsockets.entity.Msg;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/4/6.
 * PS: Not easy to write code, please indicate.
 */
public class NotificationUtils {
    private static Context context;

    private static NotificationManager manager;
    private static Notification notification;
    private static int ids=0;
    private static int noRead=1;
    private static Map<String,Integer> map=new HashMap<>();

    /**
     * 初始化
     * @param contexts
     */
    public static void init(Context contexts){
        context = contexts;
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
     */
    public static void send(String uid1,String content,String nickName){
        if(map.get(uid1)==null){
            map.put(uid1,++ids);
        }
        String id = "veve1";//为channel服务
        String name="veve消息通知";//为channel服务
        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder=null;
        Intent intent = null;
        int currentId;//记录消息id
        if(map.size()>1){//多个人发消息给你，直接跳回消息列表，即MainActivity
            intent = new Intent(context, MainActivity.class);
            currentId=1;
            notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle("veve")
                    .setContentText("有"+map.size()+"个联系人给你发送了 "+noRead+" 条新消息")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true);
        }else {
            intent=new Intent(context, ChatDetailsActivity.class);//跳转到聊天界面，并传值
            intent.putExtra("uid2",uid1);
            currentId=map.get(uid1);
            notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(nickName+" ("+noRead+"条新消息)")
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//这个版本要设置channel才有用
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notice), null);
            manager.createNotificationChannel(mChannel);
            notificationBuilder.setChannelId(id);
        }


        intent.putExtra("notice","yes");//说明是从通知跳转的
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notification = notificationBuilder.build();

        manager.notify(currentId,notification);
        noRead++;
    }


    public static void reset(){
        ids=0;
        noRead=1;
        map=new HashMap<>();
    }
}
