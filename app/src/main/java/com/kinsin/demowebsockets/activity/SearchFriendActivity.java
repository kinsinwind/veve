package com.kinsin.demowebsockets.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.entity.RequestFriend;
import com.kinsin.demowebsockets.util.HTTPUtils;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.Properties;
import com.kinsin.demowebsockets.util.WebSocketFriendUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText searchFriendEdit;
    private Button searchFriendBtn;
    private TextView searchNickName;
    private TextView searchAccount;
    private TextView alreadySend;
    private ImageView searchAddBtn;
    private RelativeLayout searchItem;

    HTTPUtils httpUtils=null;

    public static Handler handler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_friend);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //todo
                alreadySend.setVisibility(View.VISIBLE);
                searchAddBtn.setVisibility(View.GONE);
            }
        };

        /**
         * 监听输入框
         */
        searchFriendEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if("".equals(s.toString())){
                    searchFriendBtn.setClickable(false);
                    searchFriendBtn.setBackgroundResource(R.drawable.btn_1);
                }else {
                    searchFriendBtn.setClickable(true);
                    searchFriendBtn.setBackgroundResource(R.drawable.btn_2);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        searchFriendEdit = (EditText) findViewById(R.id.searchFriendEdit);
        searchFriendBtn = (Button) findViewById(R.id.searchFriendBtn);
        searchNickName = (TextView) findViewById(R.id.searchNickName);
        searchAccount = (TextView) findViewById(R.id.searchAccount);
        alreadySend = (TextView) findViewById(R.id.alreadySend);
        searchAddBtn = (ImageView) findViewById(R.id.searchAddBtn);

        httpUtils=new HTTPUtils(this);

        searchFriendBtn.setOnClickListener(this);
        searchAddBtn.setOnClickListener(this);
        searchItem = (RelativeLayout) findViewById(R.id.searchItem);
        searchItem.setOnClickListener(this);
    }

    /**
     * 发送消息
     */
    public void sends() {
        RequestFriend requestFriend=new RequestFriend();
        requestFriend.setUid1(MyApp.currentUser.getAccount());
        requestFriend.setUid2(searchAccount.getText().toString());
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("header","request");
            jsonObject.put("uid1",requestFriend.getUid1());
            jsonObject.put("uid2",requestFriend.getUid2());
            WebSocketFriendUtil.getInstance().sendDataD(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchFriendBtn:
                submit();
                break;
            case R.id.searchAddBtn:
                    sends();
//                String temUrl=Properties.URL + "/addFriend?account1=" + MyApp.currentUser.getAccount() + "&account2=" + searchFriendEdit.getText().toString();
//                Log.i("WHY", "------:"+temUrl);
//                httpUtils.getHttpInfoGET(temUrl, new HTTPUtils.CallBackVolley() {
//                    @Override
//                    public void success(String data) {
//                        try {
//                            JSONObject jsonObject=new JSONObject(data);
//                            if("S".equals(jsonObject.getString("RESULT"))){
//                                Toast.makeText(SearchFriendActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
//                                searchAddBtn.setImageResource(R.drawable.right);
//                                searchAddBtn.setClickable(false);
//                            }else {
//                                Toast.makeText(SearchFriendActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
                break;
        }
    }

    private void submit() {
        // validate
        String searchFriendEditString = searchFriendEdit.getText().toString().trim();
        if (TextUtils.isEmpty(searchFriendEditString)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO validate success, do something
        //网络请求，获取传回信息，判断是否有该用户
        httpUtils.getHttpInfoGET(Properties.URL + "/getUserByAccount?account=" + searchFriendEditString, new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if("S".equals(jsonObject.getString("RESULT"))){
                        searchItem.setVisibility(View.VISIBLE);
                        searchNickName.setText(jsonObject.getString("nick_name"));
                        searchAccount.setText(jsonObject.getString("account"));

                        if(MyApp.currentUser.getAccount().equals(jsonObject.getString("account"))){
                            searchAddBtn.setImageResource(R.drawable.right);
                            searchAddBtn.setClickable(false);
                        }else {
                            /**
                             * 检测是否已经为好友
                             */
                            String temUrl=Properties.URL + "/getFriend?account1=" + MyApp.currentUser.getAccount() + "&account2=" + jsonObject.getString("account");
                            Log.i("WHY", "success: "+temUrl);
                            httpUtils.getHttpInfoGET(temUrl, new HTTPUtils.CallBackVolley() {
                                @Override
                                public void success(String data) {
                                    try {
                                        JSONObject jsonObject1=new JSONObject(data);
                                        if("S".equals(jsonObject1.getString("RESULT"))){//已经是好友，不可使用
                                            searchAddBtn.setImageResource(R.drawable.right);
                                            searchAddBtn.setClickable(false);
                                        }else {//不是好友，可以使用

                                            //判断是否已有此请求
                                            httpUtils.getHttpInfoGET(Properties.URL + "/getRequestFriendByUid1And2?uid1=" + MyApp.currentUser.getAccount() + "&uid2=" + searchAccount.getText().toString(), new HTTPUtils.CallBackVolley() {
                                                @Override
                                                public void success(String data) {
                                                    JSONObject jsonObject2=null;
                                                    try {
                                                        jsonObject2=new JSONObject(data);
                                                        if(jsonObject2.optString("RESULT").equals("S")){//如果有此请求
                                                            //设置UI
                                                            searchAddBtn.setVisibility(View.GONE);
                                                            alreadySend.setVisibility(View.VISIBLE);
                                                        }else {
                                                            searchAddBtn.setVisibility(View.VISIBLE);
                                                            alreadySend.setVisibility(View.GONE);
                                                        }
                                                    }catch (Exception e){

                                                    }

                                                }
                                            });

                                            searchAddBtn.setImageResource(R.drawable.add_use);
                                            searchAddBtn.setClickable(true);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    }else {
                        Toast.makeText(SearchFriendActivity.this, "没有找到该用户", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
