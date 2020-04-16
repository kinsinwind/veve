package com.kinsin.demowebsockets.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.util.Base64Utils;
import com.kinsin.demowebsockets.util.CircleTransform;
import com.kinsin.demowebsockets.util.HTTPUtils;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.Properties;
import com.kinsin.demowebsockets.util.StatusBarUtil;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.wildma.pictureselector.PictureBean;
import com.wildma.pictureselector.PictureSelector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.squareup.picasso.NetworkPolicy.NO_CACHE;

public class SetHeadIconActivity extends AppCompatActivity {

    private ImageView headIconForSetHeadIcon;
    HTTPUtils httpUtils=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_head_icon);
        initView();

        //沉浸状态栏
        StatusBarUtil.immersion(this);
        headIconForSetHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector
                        .create(SetHeadIconActivity.this, PictureSelector.SELECT_REQUEST_CODE)
                        .selectPicture(true, 200, 200, 1, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*结果回调*/
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                PictureBean pictureBean = data.getParcelableExtra(PictureSelector.PICTURE_RESULT);
                Bitmap bitmap = BitmapFactory.decodeFile(pictureBean.getPath());
                String base64 = Base64Utils.bitmapToString(bitmap);
                Bitmap bitmap1 = Base64Utils.stringToBitmap(base64);
                Log.i("TAG-SetHead", "onActivityResult: "+base64);
                headIconForSetHeadIcon.setImageBitmap(bitmap1);

                //发送给服务器
                Map<String,String> map=new HashMap<>();
                map.put("account", MyApp.currentUser.getAccount());
                map.put("headicon",base64);
                httpUtils.getHttpInfoPOST(Properties.URL + "/updateHeadIcon", map, new HTTPUtils.CallBackVolley() {
                    @Override
                    public void success(String data) {
                        try {
                            JSONObject jsonObject=new JSONObject(data);
                            if("S".equals(jsonObject.optString("RESULT"))){
                                Toast.makeText(SetHeadIconActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                MyApp.currentUser.setHeadicon(jsonObject.optString("url"));
                                //利用Picasso框架将头像设为圆形
                                Picasso.with(SetHeadIconActivity.this).load(MyApp.currentUser.getHeadicon()).transform(new CircleTransform()).memoryPolicy(MemoryPolicy.NO_CACHE).into(headIconForSetHeadIcon);
                            }else {
                                Toast.makeText(SetHeadIconActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //利用Picasso框架将头像设为圆形
        Picasso.with(this).load(MyApp.currentUser.getHeadicon()).transform(new CircleTransform()).memoryPolicy(MemoryPolicy.NO_CACHE).into(headIconForSetHeadIcon);
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        headIconForSetHeadIcon = (ImageView) findViewById(R.id.headIconForSetHeadIcon);

        httpUtils=new HTTPUtils(this);
    }
}
