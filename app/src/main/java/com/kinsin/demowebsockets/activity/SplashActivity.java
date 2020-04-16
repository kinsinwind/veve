package com.kinsin.demowebsockets.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.util.NotificationUtils;
import com.wildma.pictureselector.PermissionUtils;

public class SplashActivity extends AppCompatActivity {
    private final int PERMISSION_CODE_FIRST = 0x14;//权限请求码
    private boolean isToast = true;//是否弹吐司，为了保证for循环只弹一次

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //请求应用需要的所有权限
        boolean checkPermissionFirst = PermissionUtils.checkPermissionFirst(this, PERMISSION_CODE_FIRST,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        if (checkPermissionFirst) {
            //todo
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            //初始化通知工具
            NotificationUtils.init(this);

            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissions = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isPermissions = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) { //用户选择了"不再询问"
                    if (isToast) {
                        Toast.makeText(this, "请手动打开该应用需要的权限", Toast.LENGTH_SHORT).show();
                        isToast = false;
                    }
                }
            }
        }
        isToast = true;
        if (isPermissions) {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "允许所有权限");
            //todo
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            finish();
        } else {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "有权限不允许");
            Toast.makeText(this, "请给予必要的权限,否则无法运行(头像等需要读取本地存储)", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
