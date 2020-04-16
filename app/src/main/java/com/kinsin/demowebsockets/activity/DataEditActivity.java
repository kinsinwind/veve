package com.kinsin.demowebsockets.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.StatusBarUtil;

public class DataEditActivity extends AppCompatActivity {

    private TextView titles;
    private RelativeLayout headIconForDataEdit;
    private TextView nickNameShowForDataEdit;
    private RelativeLayout nickNameForDataEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_edit);
        initView();
        //沉浸状态栏
        StatusBarUtil.immersion(this);

        titles.setText("编辑资料");

        /**
         * 设置头像监听
         */
        headIconForDataEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                startActivity(new Intent(DataEditActivity.this,SetHeadIconActivity.class));
            }
        });

        /**
         * 设置昵称监听
         */
        nickNameForDataEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DataEditActivity.this,EditNickNameActivity.class));
            }
        });

    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        titles = (TextView) findViewById(R.id.titles);
        headIconForDataEdit = (RelativeLayout) findViewById(R.id.headIconForDataEdit);
        nickNameShowForDataEdit = (TextView) findViewById(R.id.nickNameShowForDataEdit);
        nickNameForDataEdit = (RelativeLayout) findViewById(R.id.nickNameForDataEdit);

        nickNameShowForDataEdit.setText(MyApp.currentUser.getNick_name());
    }

    @Override
    protected void onResume() {
        super.onResume();
        nickNameShowForDataEdit.setText(MyApp.currentUser.getNick_name());
    }
}
