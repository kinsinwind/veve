package com.kinsin.demowebsockets.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kinsin.demowebsockets.activity.ChatDetailsActivity;
import com.kinsin.demowebsockets.activity.MainActivity;
import com.kinsin.demowebsockets.activity.PersonalActivity;
import com.kinsin.demowebsockets.activity.RequestFriendDetailsActivity;
import com.kinsin.demowebsockets.activity.SearchFriendActivity;
import com.kinsin.demowebsockets.entity.Msg;
import com.kinsin.demowebsockets.util.MessageCallBack;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.WebSocketFriendUtil;
import com.kinsin.demowebsockets.util.WebSocketUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/4/1.
 * PS: Not easy to write code, please indicate.
 */
public class SocketService extends Service {
    private String TAG="TAG-service";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //在启动服务时，开启socket连接
        Log.e(TAG, "onCreate: 服务已启动" );
        //region 开启聊天长连接
        WebSocketUtil.getInstance().init(this, MyApp.currentUser.getAccount(), new MessageCallBack() {
            @Override
            public void success(String data) {
                Log.i(TAG, "success: "+data);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = data;

                //此处做简单处理，用于判断信息去向，用发来分发判断
                try {
                    JSONObject jsonObject =new JSONObject(data);
                    Log.i("TAG-service", "uid1:"+jsonObject.getString("uid1"));
                    Log.i("TAG-service", "uid2:"+jsonObject.getString("uid2"));
                    Log.i("TAG-service", "当前窗口："+MyApp.currentFriend.getAccount());
                    if(jsonObject.getString("uid2").equals(MyApp.currentFriend.getAccount())
                            || jsonObject.getString("uid1").equals(MyApp.currentFriend.getAccount())){//只要这条消息与当前窗口有关联，一律递交给该窗口处理
                        if(ChatDetailsActivity.handler!=null){
                            ChatDetailsActivity.handler.sendMessage(msg);
                        }
                        Log.i(TAG, "分发给"+MyApp.currentFriend.getAccount());
                    }else if("心跳".equals(jsonObject.getString("uid2"))){
                        //什么都不做
                        Log.e(TAG, "心跳(聊天)");
                    } else {
                        if(MainActivity.handler!=null){
                            Msg msgs = new Msg();
                            msgs.setUid1(jsonObject.optString("uid1"));
                            msgs.setContent(jsonObject.optString("content"));
                            msg.obj = msgs;//准备发送人数据
                            MainActivity.handler.sendMessage(msg);
                        }
                        Log.i(TAG, "分发给MAIN");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        //endregion

        //region 开启好友请求长连接
        WebSocketFriendUtil.getInstance().init(this, MyApp.currentUser.getAccount(), new MessageCallBack() {
            @Override
            public void success(String data) {
                Log.i(TAG, "success: "+data);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = data;

                //此处做简单处理，用于判断信息去向，用发来分发判断
                try {
                    JSONObject jsonObject =new JSONObject(data);
                    Log.i("TAG-service", "uid1:"+jsonObject.getString("uid1"));
                    Log.i("TAG-service", "uid2:"+jsonObject.getString("uid2"));
                    if(jsonObject.optString("header").equals("request")){//是请求消息
                        if(jsonObject.optString("uid1").equals(MyApp.currentUser.getAccount())){//如果uid1为自己，则表示此消息为好友请求发送成功
                            //将添加按钮设置为已发送
                            if(SearchFriendActivity.handler!=null){
                                SearchFriendActivity.handler.sendEmptyMessage(0);
                            }
                        }else {//正常接收到好友请求
                            if(PersonalActivity.handler!=null){
                                PersonalActivity.handler.sendEmptyMessage(0);
                            }
                        }
                    }else if("心跳".equals(jsonObject.optString("uid2"))){
                        //什么都不做
                        Log.e(TAG, "心跳(好友)");
                    } else {//是回应消息

                        if(jsonObject.optString("uid2").equals(MyApp.currentUser.getAccount())){//是我处理的消息
                            if(PersonalActivity.handler!=null){
                                PersonalActivity.handler.sendEmptyMessage(0);//请求一下好友请求消息数目
                            }
                            if(MainActivity.handler!=null){
                                MainActivity.handler.sendEmptyMessage(0);
                            }
                            if(RequestFriendDetailsActivity.handler!=null){
                                RequestFriendDetailsActivity.handler.sendEmptyMessage(0);//刷新一下好友请求详情列表
                            }
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        //endregion

        //region 心跳机制
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("uid1",MyApp.currentUser.getAccount());
                    jsonObject.put("uid2","心跳");
                    jsonObject.put("content","心跳");
                    jsonObject.put("header","心跳");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WebSocketUtil.getInstance().sendDataD(jsonObject.toString());
                WebSocketFriendUtil.getInstance().sendDataD(jsonObject.toString());
            }
        },5000,60*1000);
        //endregion

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
