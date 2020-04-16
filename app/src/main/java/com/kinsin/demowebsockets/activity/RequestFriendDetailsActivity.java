package com.kinsin.demowebsockets.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.custom.FriendRequestAdapter;
import com.kinsin.demowebsockets.entity.RequestFriend;
import com.kinsin.demowebsockets.util.HTTPUtils;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestFriendDetailsActivity extends AppCompatActivity {
    public List<RequestFriend> requestFriendList = null;
    private TextView titles;
    private ListView friendRequestListView;
    FriendRequestAdapter friendRequestAdapter=null;
    HTTPUtils httpUtils=null;
    public static Handler handler=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_friend_details);
        initView();
        getInfoByHttp();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(RequestFriendDetailsActivity.this, "已处理完成", Toast.LENGTH_SHORT).show();
                getInfoByHttp();
            }
        };
    }

    /**
     * 获得好友请求列表
     */
    void getInfoByHttp() {
        httpUtils.getHttpInfoGET(Properties.URL + "/getRequestFriendByUid2?uid2=" + MyApp.currentUser.getAccount(), new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                try {
                    Log.e("TAG-RequestFriendD", data);
                    JSONObject jsonObject = new JSONObject(data);
                    if ("S".equals(jsonObject.getString("RESULT"))) {
                        requestFriendList.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tmpJson = jsonArray.getJSONObject(i);
                            RequestFriend requestFriend=new RequestFriend();
                            requestFriend.setNick_name(tmpJson.optString("nick_name"));
                            requestFriend.setUid1(tmpJson.optString("uid1"));
                            requestFriend.setUid2(tmpJson.optString("uid2"));
                            requestFriend.setSend_time(tmpJson.optString("send_time"));
                            requestFriendList.add(requestFriend);
                        }
                        System.out.println(requestFriendList);
                        if(friendRequestAdapter==null){
                            friendRequestAdapter=new FriendRequestAdapter(requestFriendList,RequestFriendDetailsActivity.this);
                            friendRequestListView.setAdapter(friendRequestAdapter);
                        }else {
                            friendRequestAdapter.notifyDataSetChanged();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initView() {
        titles = (TextView) findViewById(R.id.titles);
        friendRequestListView = (ListView) findViewById(R.id.friendRequestListView);

        titles.setText("好友请求");
        httpUtils=new HTTPUtils(this);
        requestFriendList=new ArrayList<>();
    }

    public void back(View view){
        finish();
    }
}
