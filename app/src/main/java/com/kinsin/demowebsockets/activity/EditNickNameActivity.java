package com.kinsin.demowebsockets.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.util.HTTPUtils;
import com.kinsin.demowebsockets.util.InputFilterUtils;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.Properties;
import com.kinsin.demowebsockets.util.StatusBarUtil;

import org.json.JSONObject;

public class EditNickNameActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView titles;
    private TextView done;
    private TextView fontNumber;
    private EditText nickNameForEditNickName;
    HTTPUtils httpUtils=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nick_name);
        initView();

        //沉浸状态栏
        StatusBarUtil.immersion(this);

        fontNumber.setText(nickNameForEditNickName.getText().length()+"/20");

        nickNameForEditNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fontNumber.setText(nickNameForEditNickName.getText().length()+"/20");
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initView() {
        titles = (TextView) findViewById(R.id.titles);
        done = (TextView) findViewById(R.id.done);
        fontNumber = (TextView) findViewById(R.id.fontNumber);
        nickNameForEditNickName = (EditText) findViewById(R.id.nickNameForEditNickName);

        done.setOnClickListener(this);

        titles.setText("修改昵称");
        nickNameForEditNickName.setText(MyApp.currentUser.getNick_name());

        nickNameForEditNickName.setFilters(new InputFilterUtils[]{new InputFilterUtils(nickNameForEditNickName,20)});
        httpUtils=new HTTPUtils(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        final String nickNameForEditNickNameString = nickNameForEditNickName.getText().toString().trim();
        if (TextUtils.isEmpty(nickNameForEditNickNameString)) {
            Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO validate success, do something
        httpUtils.getHttpInfoGET(Properties.URL + "/updateNickName?account=" + MyApp.currentUser.getAccount() + "&nickname=" + nickNameForEditNickNameString, new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                JSONObject jsonObject=null;
                try{
                    jsonObject=new JSONObject(data);
                    if(jsonObject.optString("RESULT").equals("S")){
                        MyApp.currentUser.setNick_name(nickNameForEditNickNameString);
                        Toast.makeText(EditNickNameActivity.this, "修改完成", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                    }
                }catch (Exception e){

                }
            }
        });

    }
}
