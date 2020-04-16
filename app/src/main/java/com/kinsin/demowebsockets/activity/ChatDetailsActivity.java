package com.kinsin.demowebsockets.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.custom.MsgDetailsAdapter;
import com.kinsin.demowebsockets.entity.Msg;
import com.kinsin.demowebsockets.util.AppRunUtil;
import com.kinsin.demowebsockets.util.HTTPUtils;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.NotificationUtils;
import com.kinsin.demowebsockets.util.Properties;
import com.kinsin.demowebsockets.util.WebSocketUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView msgListView;
    private EditText inputs;
    private Button sendBtn;
    private TextView titles;

    MsgDetailsAdapter msgDetailsAdapter=null;
    List<Msg> msgList=null;

    public static Handler handler=null;
    String uid2="";
    HTTPUtils httpUtils=null;

    private boolean isComingNotice=false;//是否来自通知的跳转，如果是，则要处理返回操作，需要主动跳转到Main

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        initView();

        //沉浸状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        Intent intent=getIntent();
        uid2=intent.getStringExtra("uid2");
        //设置当前窗口为好友账号,用来给service判断
        MyApp.currentFriend.setAccount(uid2);

        //获取网络信息
        getHttpInfo();

        //与Service通信，当检测到该信息为当前窗口的消息时，service将会把信息直接递交到该窗口而不是MainActivity
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.i("TAG-Chat", ""+Properties.URL + "/setIsRead?uid1=" + MyApp.currentUser.getAccount() + "&uid2=" + MyApp.currentFriend.getAccount());

                //将未读消息设置为已读
                httpUtils.getHttpInfoGET(Properties.URL + "/setIsRead?uid1=" + MyApp.currentUser.getAccount() + "&uid2=" + MyApp.currentFriend.getAccount(), new HTTPUtils.CallBackVolley() {
                    @Override
                    public void success(String data) {
                    }
                });
                try {
                    JSONObject jsonObject=new JSONObject(String.valueOf(msg.obj));
                    final String uid1 = jsonObject.getString("uid1");
                    String uid2 = jsonObject.getString("uid2");
                    final String content = jsonObject.getString("content");
                    Msg tmpMsg = new Msg();
                    tmpMsg.setUid1(uid1);
                    tmpMsg.setUid2(uid2);
                    tmpMsg.setContent(content);

                    /**
                     * 判断是否在后台运行，若是则发送通知
                     */
                    if(AppRunUtil.isBackground(ChatDetailsActivity.this) && !tmpMsg.getUid1().equals(MyApp.currentUser.getAccount())){
                        httpUtils.getHttpInfoGET(Properties.URL + "/getUserByAccount?account=" + uid1, new HTTPUtils.CallBackVolley() {
                            @Override
                            public void success(String data) {
                                try {
                                    JSONObject jsonObject1=new JSONObject(data);
                                    NotificationUtils.send(uid1,content,jsonObject1.optString("nick_name"));
                                    isComingNotice=true;//宁杀错不放过
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    msgList.add(tmpMsg);

                    //出现了个问题，如果停在聊天界面，直接返回到桌面，就会导致消息通知正常，但onResume不执行，所以还是无法跳转到MainActivity,要处理一下
//                    Intent intentTmp = getIntent();
//                    if("yes".equals(intentTmp.getStringExtra("notice"))){
//                        isComingNotice=true;
//                        Toast.makeText(ChatDetailsActivity.this, "已处理", Toast.LENGTH_SHORT).show();
//                    }else {
//                        Toast.makeText(ChatDetailsActivity.this, "未处理", Toast.LENGTH_SHORT).show();
//                    }
//                    这样无法解决，因为这个线程处理是实时的，无法等待通知跳转再处理，所以宁杀错不放过，在发出通知时就将isComingNotice设为tue，这样会导致
//                    从运行任务中切换回来和从通知过来的等效了，都会重新主动跳转

                    //更新UI
                    if(msgDetailsAdapter!=null){
                        msgDetailsAdapter.notifyDataSetChanged();
                        msgListView.setSelection(msgList.size()-1);//设置位置
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        /**
         * 监听输入框
         */
        inputs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if("".equals(s.toString())){
                    sendBtn.setClickable(false);
                    sendBtn.setBackgroundResource(R.drawable.btn_1);
                }else {
                    sendBtn.setClickable(true);
                    sendBtn.setBackgroundResource(R.drawable.btn_2);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    void getHttpInfo(){
        httpUtils.getHttpInfoGET(Properties.URL + "/getUserByAccount?account=" + MyApp.currentFriend.getAccount(), new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                JSONObject jsonObject=null;
                try {
                    jsonObject=new JSONObject(data);
                    MyApp.currentFriend.setNick_name(jsonObject.optString("nick_name"));
                    MyApp.currentFriend.setHeadicon(jsonObject.optString("headicon"));
                    MyApp.currentFriend.setNet_status(jsonObject.optInt("net_status"));
                    titles.setText(MyApp.currentFriend.getNick_name());
                }catch (Exception e){

                }
            }
        });

        httpUtils.getHttpInfoGET(Properties.URL + "/getMsgs?uid1=" + MyApp.currentUser.getAccount() + "&uid2=" + uid2, new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                //数据处理
                dataProcess(data);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=getIntent();
        if("yes".equals(intent.getStringExtra("notice"))){
//            Toast.makeText(this, "从通知而来", Toast.LENGTH_SHORT).show();
            NotificationUtils.reset();
            uid2=intent.getStringExtra("uid2");
            //设置当前窗口为好友账号,用来给service判断
            MyApp.currentFriend.setAccount(uid2);
            isComingNotice=true;
        }
    }

    private void initView() {
        inputs = (EditText) findViewById(R.id.inputs);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        titles=findViewById(R.id.titles);
        msgListView=findViewById(R.id.msgListView);

        msgList=new ArrayList<>();

        httpUtils=new HTTPUtils(ChatDetailsActivity.this);

        sendBtn.setOnClickListener(this);
    }

    /**
     * 数据处理
     * @param data
     */
    void dataProcess(String data){
        JSONObject jsonObject=null;
        try {
            jsonObject=new JSONObject(data);
            if("S".equals(jsonObject.getString("RESULT"))){
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Msg msg=new Msg();
                    msg.setContent(jsonObject1.getString("content"));
                    msg.setSend_time(jsonObject1.getString("send_time"));
                    msg.setUid1(jsonObject1.getString("uid1"));//用来判断是谁发的//uid1即消息发送人，uid2为接收人
                    msg.setUid2(jsonObject1.getString("uid2"));
                    msgList.add(msg);
                }
                //处理完成，准备适配
                if(msgDetailsAdapter==null){
                    msgDetailsAdapter=new MsgDetailsAdapter(ChatDetailsActivity.this,msgList);
                    msgListView.setAdapter(msgDetailsAdapter);
                    msgListView.setSelection(msgList.size()-1);
                }else {
                    msgDetailsAdapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size()-1);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        String inputsString = inputs.getText().toString().trim();
        if (TextUtils.isEmpty(inputsString)) {
            Toast.makeText(this, "inputsString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO validate success, do something
        //清空输入栏
        inputs.setText("");
        //发送消息
        sends(inputsString);
    }

    /**
     * 发送消息
     * @param content
     */
    public void sends(String content) {
        Msg msg=new Msg();
        msg.setUid1(MyApp.currentUser.getAccount());
        msg.setUid2(uid2);
        msg.setContent(content);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("uid1",msg.getUid1());
            jsonObject.put("uid2",msg.getUid2());
            jsonObject.put("content",msg.getContent());
            WebSocketUtil.getInstance().sendDataD(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //将未读消息设置为已读
            httpUtils.getHttpInfoGET(Properties.URL + "/setIsRead?uid1=" + MyApp.currentUser.getAccount() + "&uid2=" + MyApp.currentFriend.getAccount(), new HTTPUtils.CallBackVolley() {
                @Override
                public void success(String data) {
                    if(isComingNotice){//通知跳转来的无法返回到消息列表,所以加跳转
                        startActivity(new Intent(ChatDetailsActivity.this,MainActivity.class));
                    }
                    finish();
                }
            });
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出时，将会话窗口标记重置
        MyApp.currentFriend.setAccount("");
    }

    public void back(View view) {
        //将未读消息设置为已读
        httpUtils.getHttpInfoGET(Properties.URL + "/setIsRead?uid1=" + MyApp.currentUser.getAccount() + "&uid2=" + MyApp.currentFriend.getAccount(), new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                if(isComingNotice){//通知跳转来的无法返回到消息列表,所以加跳转
                    startActivity(new Intent(ChatDetailsActivity.this,MainActivity.class));
                }
                finish();
            }
        });
    }
}
