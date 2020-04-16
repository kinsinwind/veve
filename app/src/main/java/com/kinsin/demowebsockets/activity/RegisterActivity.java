package com.kinsin.demowebsockets.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.util.HTTPUtils;
import com.kinsin.demowebsockets.util.InputFilterUtils;
import com.kinsin.demowebsockets.util.MathingJudge;
import com.kinsin.demowebsockets.util.Properties;
import com.kinsin.demowebsockets.util.RandomUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText account;
    private EditText passwrod1;
    private EditText passwrod2;
    private EditText nickName;
    private EditText email;
    private EditText passCode;
    private Button sendPassCodeBtn;
    private Button registerBtn;

    private HTTPUtils httpUtils;
    private boolean isAccount=true;//账号是否可用
    private String passCodeCreate=null;//生成的验证码
    int temTime=29;//倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        httpUtils=new HTTPUtils(this);

        account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){//失焦

                    if(account.getText().toString().length()<5){
                        Toast.makeText(RegisterActivity.this, "账号不小于5位！", Toast.LENGTH_SHORT).show();
                        isAccount=false;
                    }else {
                        httpUtils.getHttpInfoGET(Properties.URL + "/accountJudge?account=" + account.getText().toString(), new HTTPUtils.CallBackVolley() {
                            @Override
                            public void success(String data) {
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    if("F".equals(jsonObject.getString("RESULT"))){
                                        Toast.makeText(RegisterActivity.this, "账号已存在，请修改!", Toast.LENGTH_SHORT).show();
                                        account.setTextColor(Color.parseColor("#FF0000"));
                                        isAccount=false;
                                    }else {
                                        isAccount=true;
                                        account.setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }

                }
            }
        });


    }

    private void initView() {
        account = (EditText) findViewById(R.id.account);
        passwrod1 = (EditText) findViewById(R.id.passwrod1);
        passwrod2 = (EditText) findViewById(R.id.passwrod2);
        nickName = (EditText) findViewById(R.id.nickName);
        email = (EditText) findViewById(R.id.email);
        passCode = (EditText) findViewById(R.id.passCode);
        sendPassCodeBtn = (Button) findViewById(R.id.sendPassCodeBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        sendPassCodeBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        //处理特殊字符
        account.setFilters(new InputFilterUtils[]{new InputFilterUtils(account,15)});
        passwrod1.setFilters(new InputFilterUtils[]{new InputFilterUtils(passwrod1,20)});
        passwrod2.setFilters(new InputFilterUtils[]{new InputFilterUtils(passwrod2,20)});
        nickName.setFilters(new InputFilterUtils[]{new InputFilterUtils(nickName,20)});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendPassCodeBtn:
                final String emailString = email.getText().toString().trim();
                if (TextUtils.isEmpty(emailString)) {
                    Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!MathingJudge.EmailFormat(emailString)){
                    Toast.makeText(this, "邮箱格式有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                httpUtils.getHttpInfoGET(Properties.URL + "/emailJudge?email=" + emailString, new HTTPUtils.CallBackVolley() {
                    @Override
                    public void success(String data) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if("F".equals(jsonObject.getString("RESULT"))){
                                Toast.makeText(RegisterActivity.this, "该邮箱已绑定", Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                doSend(emailString);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                //doSend

                break;
            case R.id.registerBtn:
                submit();
                break;
        }
    }

    /**
     * 发送邮件
     * @param emailString
     */
    private void doSend(String emailString){
        Map<String,String> map = new HashMap<>();
        passCodeCreate = RandomUtil.createPassCode(4);
        map.put("passCode", passCodeCreate);//生成4位验证码
        map.put("email",emailString);
        httpUtils.getHttpInfoPOST(Properties.URL+"/sendPassCode", map, new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if("S".equals(jsonObject.getString("RESULT"))){
                        Toast.makeText(RegisterActivity.this, "发送成功，请查看邮箱", Toast.LENGTH_SHORT).show();

                        //按钮倒计时
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(temTime>0){
                                    sendPassCodeBtn.setClickable(false);
                                    sendPassCodeBtn.setText(temTime+"");
                                    temTime--;
                                }else {
                                    timer.cancel();
                                    temTime=29;
                                    sendPassCodeBtn.setClickable(true);
                                    sendPassCodeBtn.setText("发送验证码");
                                }

                            }
                        },0,1000);

                    }else {
                        Toast.makeText(RegisterActivity.this, "发送失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void submit() {
        // validate
        String accountString = account.getText().toString().trim();
        if (TextUtils.isEmpty(accountString)) {
            Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwrod1String = passwrod1.getText().toString().trim();
        if (TextUtils.isEmpty(passwrod1String)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwrod2String = passwrod2.getText().toString().trim();
        if (TextUtils.isEmpty(passwrod2String)) {
            Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String nickNameString = nickName.getText().toString().trim();
        if (TextUtils.isEmpty(nickNameString)) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailString = email.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)) {
            Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String passCodeString = passCode.getText().toString().trim();
        if (TextUtils.isEmpty(passCodeString)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!passwrod1String.equals(passwrod2String)){
            Toast.makeText(this, "两次密码不一样,请检查", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isAccount){
            Toast.makeText(this, "账号不可用，请修改", Toast.LENGTH_SHORT).show();
            return;
        }

        if(passCodeCreate==null||!passCodeCreate.equals(passCodeString)){
            Toast.makeText(this, "验证码不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something
        Map<String,String> map = new HashMap<>();
        map.put("account",accountString);
        map.put("password",passwrod1String);
        map.put("email",emailString);
        map.put("nick_name",nickNameString);
        httpUtils.getHttpInfoPOST(Properties.URL+"/register", map, new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if("S".equals(jsonObject.getString("RESULT"))){
                        //注册成功
                        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        //注册失败
                        Toast.makeText(RegisterActivity.this, "注册失败!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void back(View view) {
        finish();
    }
}
