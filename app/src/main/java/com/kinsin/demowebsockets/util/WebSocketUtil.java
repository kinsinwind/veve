package com.kinsin.demowebsockets.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.rabtman.wsmanager.WsManager;
import com.rabtman.wsmanager.listener.WsStatusListener;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.ByteString;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/3/28.
 * PS: Not easy to write code, please indicate.
 */
public class WebSocketUtil {
    private String TAG = "TEST";
    private static WsManager wsManager;
    private Context context;
    MessageCallBack messageCallBack;

    //饿汉式
    private static WebSocketUtil instance = new WebSocketUtil();

    private WebSocketUtil() {
    }

    public static WebSocketUtil getInstance() {
        return instance;
    }

    /**
     * 初始化
     * OkHttpClient中间隔30秒发送心跳包
     *
     * @param context
     */
    public void init(Context context,String name, MessageCallBack messageCallBack) {
        this.context = context;
        this.messageCallBack = messageCallBack;
        try {
            wsManager = new WsManager.Builder(context)
                    .client(new OkHttpClient().newBuilder().pingInterval(30, TimeUnit.SECONDS).retryOnConnectionFailure(true).build())
                    .wsUrl(Properties.wsUrl+name)
                    .needReconnect(true)
                    .build();
            wsManager.startConnect();
            wsManager.setWsStatusListener(wsStatusListener);
        } catch (Exception e) {
            Log.e(TAG, "初始化异常-----------" + e.getMessage());
        }
    }

    private WsStatusListener wsStatusListener = new WsStatusListener() {
        @Override
        public void onOpen(Response response) {
            Log.i(TAG, "服务器已连接");
        }

        @Override
        public void onMessage(String text) {
            messageCallBack.success(text);//消息回调接口
        }

        @Override
        public void onMessage(ByteString bytes) {
        }

        @Override
        public void onReconnect() {
            Log.i(TAG, "重连中");
        }

        @Override
        public void onClosing(int code, String reason) {
            Log.i(TAG, "关闭中");
        }

        @Override
        public void onClosed(int code, String reason) {
            Log.i(TAG, "已关闭");
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            Log.i(TAG, "连接失败");
        }
    };

    //发送ws数据
    public void sendDataD(String content) {

        if (wsManager != null && wsManager.isWsConnected()) {
            boolean isSend = wsManager.sendMessage(content);
            if (isSend) {
                Log.e("onOpen sendMessage", "发送成功");
            } else {
                Log.e("onOpen sendMessage", "发送失败");
            }
        } else {
            Toast.makeText(context, "请先连接服务器", Toast.LENGTH_SHORT).show();
        }

    }

    //断开ws
    public void disconnect() {
        if (wsManager != null)
            wsManager.stopConnect();
    }


}
