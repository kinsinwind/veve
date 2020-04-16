package com.kinsin.demowebsockets.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.util.CircleTransform;
import com.kinsin.demowebsockets.util.HTTPUtils;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.PreferenceUtil;
import com.kinsin.demowebsockets.util.Properties;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView dataIcon;
    private TextView dataNickName;
    private TextView dataAccount;
    private Button dataEditBtn;
    private TextView titles;
    private TextView friendRequestTag;
    public static Handler handler = null;
    HTTPUtils httpUtils=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initView();
        //沉浸状态栏
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
                getRequestNum();
            }
        };

    }

    /**
     * 获取请求消息数量
     */
    private void getRequestNum(){
        httpUtils.getHttpInfoGET(Properties.URL + "/getRequestFriendNumber?uid2=" + MyApp.currentUser.getAccount(), new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                JSONObject jsonObject=null;
                try {
                    jsonObject=new JSONObject(data);
                    if("S".equals(jsonObject.optString("RESULT"))){
                        int num = jsonObject.getInt("num");
                        friendRequestTag.setText(""+num);

                        if(friendRequestTag.getText().toString().equals("0")){
                            friendRequestTag.setVisibility(View.GONE);
                        }else {
                            friendRequestTag.setVisibility(View.VISIBLE);
                        }

                    }
                }catch (Exception e){

                }

            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        dataIcon = (ImageView) findViewById(R.id.dataIcon);
        dataNickName = (TextView) findViewById(R.id.dataNickName);
        dataAccount = (TextView) findViewById(R.id.dataAccount);
        friendRequestTag = (TextView) findViewById(R.id.friendRequestTag);
        dataEditBtn = (Button) findViewById(R.id.dataEditBtn);
        titles=findViewById(R.id.titles);

        dataEditBtn.setOnClickListener(this);

        titles.setText("我的资料");

        dataAccount.setText(MyApp.currentUser.getAccount());
        dataNickName.setText(MyApp.currentUser.getNick_name());

        httpUtils=new HTTPUtils(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取请求数量
        getRequestNum();
        //利用Picasso框架将头像设为圆形
        Picasso.with(this).load(MyApp.currentUser.getHeadicon()).transform(new CircleTransform()).into(dataIcon);

        dataNickName.setText(MyApp.currentUser.getNick_name());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dataEditBtn:
                startActivity(new Intent(PersonalActivity.this,DataEditActivity.class));
                break;
        }
    }

    /**
     * 注销账号
     *
     * @param view
     */
    public void powerOff(View view) {

        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("是否要注销账号")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceUtil.putString(PersonalActivity.this, PreferenceUtil.USERNAME, "");
                        PreferenceUtil.putString(PersonalActivity.this, PreferenceUtil.PASSWORD, "");
                        startActivity(new Intent(PersonalActivity.this, LoginActivity.class));
                        MainActivity.instance.finish();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    public void goFriendDetails(View view) {
        startActivity(new Intent(PersonalActivity.this,RequestFriendDetailsActivity.class));
    }
}
