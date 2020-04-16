package com.kinsin.demowebsockets.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.services.SocketService;
import com.kinsin.demowebsockets.util.HTTPUtils;
import com.kinsin.demowebsockets.util.InputFilterUtils;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.PreferenceUtil;
import com.kinsin.demowebsockets.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText account;
    private EditText password;
    private Button loginBtn;

    private HTTPUtils httpUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        httpUtils=new HTTPUtils(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        account.setText(PreferenceUtil.getString(this,PreferenceUtil.USERNAME));
        password.setText(PreferenceUtil.getString(this,PreferenceUtil.PASSWORD));

        if(!"".equals(PreferenceUtil.getString(this,PreferenceUtil.USERNAME))){//已持久化
            login(account.getText().toString(),password.getText().toString());
        }
    }

    public void goRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void initView() {
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(this);

        //处理特殊字符
        account.setFilters(new InputFilterUtils[]{new InputFilterUtils(account,15)});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        final String accountString = account.getText().toString().trim();
        if (TextUtils.isEmpty(accountString)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            return;
        }

        final String passwordString = password.getText().toString().trim();
        if (TextUtils.isEmpty(passwordString)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something
        login(accountString,passwordString);

    }

    private void login(final String accountString, final String passwordString){
        Map<String,String> map = new HashMap<>();
        map.put("username",accountString);
        map.put("password",passwordString);
        httpUtils.getHttpInfoPOST(Properties.URL + "/login", map, new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                try {
                    JSONObject jsonObject=new JSONObject(data);
                    if("S".equals(jsonObject.getString("RESULT"))){
                        MyApp.currentUser.setAccount(jsonObject.getString("account"));
                        MyApp.currentUser.setEmail(jsonObject.getString("email"));
                        MyApp.currentUser.setNick_name(jsonObject.getString("nick_name"));
                        MyApp.currentUser.setNet_status(jsonObject.getInt("net_status"));
                        MyApp.currentUser.setHeadicon(jsonObject.getString("headicon"));
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                        //启动连接service
                        Log.e("TAG", "success: 正在启动服务" );
                        startService(new Intent(LoginActivity.this, SocketService.class));

                        //登录持久化
                        PreferenceUtil.putString(LoginActivity.this,PreferenceUtil.USERNAME,accountString);
                        PreferenceUtil.putString(LoginActivity.this,PreferenceUtil.PASSWORD,passwordString);
                    }else {
                        Toast.makeText(LoginActivity.this, "账号或密码错误!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
