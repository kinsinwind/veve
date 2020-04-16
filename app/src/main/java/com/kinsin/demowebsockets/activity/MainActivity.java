package com.kinsin.demowebsockets.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.custom.FriendListAdapter;
import com.kinsin.demowebsockets.entity.FriendMap;
import com.kinsin.demowebsockets.entity.Msg;
import com.kinsin.demowebsockets.util.AppRunUtil;
import com.kinsin.demowebsockets.util.CircleTransform;
import com.kinsin.demowebsockets.util.HTTPUtils;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.NotificationUtils;
import com.kinsin.demowebsockets.util.Properties;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance=null;

    private List<FriendMap> list = null;
    private ListView friendListView;
    private TextView showCurrentName;
    private ImageView mainHeadIcon;
    private SwipeRefreshLayout swipereLayout;
    private ImageView addBtn;
    private TextView addFriendBtn;
    private View popShade;
    public static Handler handler = null;
    private boolean isRefresh=false;//是否时通过下拉刷新，刷新有提示，其他没有

    private HTTPUtils httpUtils = null;
    Timer timer = null;
    FriendListAdapter friendListAdapter = null;

    PopupWindow popupWindow=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //沉浸状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        initView();
        getInfoByHttp();
        showCurrentName.setText(MyApp.currentUser.getNick_name());

        handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                //用来和service通信
                //暂时啥都不做，只请求列表信息
                getInfoByHttp();

                /**
                 * 判断是否在后台运行
                 */
                final Msg tmpMsg = (Msg) msg.obj;
                if(AppRunUtil.isBackground(MainActivity.this) && !tmpMsg.getUid1().equals(MyApp.currentUser.getAccount())){
                    httpUtils.getHttpInfoGET(Properties.URL + "/getUserByAccount?account=" + tmpMsg.getUid1(), new HTTPUtils.CallBackVolley() {
                        @Override
                        public void success(String data) {
                            try {
                                JSONObject jsonObject1=new JSONObject(data);
                                NotificationUtils.send(tmpMsg.getUid1(),tmpMsg.getContent(),jsonObject1.optString("nick_name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        };

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(MainActivity.this, ChatDetailsActivity.class);
                intent.putExtra("uid2", list.get(position).getAccount());
                startActivity(intent);
            }
        });

        swipereLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //这里获取数据的逻辑
                isRefresh=true;
                getInfoByHttp();
            }
        });


        /**
         * 头像点击事件
         */
        mainHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PersonalActivity.class));
            }
        });

        /**
         * 打开popWindow
         */
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAsDropDown(addBtn);
                popShade.setVisibility(View.VISIBLE);
            }
        });

        /**
         * 点击遮罩时
         */
        popShade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                popShade.setVisibility(View.GONE);
            }
        });

        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchFriendActivity.class));
                popupWindow.dismiss();
                popShade.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 获得好友列表
     */
    void getInfoByHttp() {
        httpUtils.getHttpInfoGET(Properties.URL + "/getFriends?account=" + MyApp.currentUser.getAccount(), new HTTPUtils.CallBackVolley() {
            @Override
            public void success(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if ("S".equals(jsonObject.getString("RESULT"))) {
                        list.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tmpJson = jsonArray.getJSONObject(i);
                            list.add(new FriendMap(tmpJson.getString("account"), tmpJson.getString("headicon"), tmpJson.getString("nick_name"), tmpJson.getString("last_msg"),
                                    tmpJson.getString("last_time"), Integer.parseInt(tmpJson.getString("noReadNumber"))));
                        }
                        if (friendListAdapter == null) {
                            friendListAdapter = new FriendListAdapter(MainActivity.this, list);
                            friendListView.setAdapter(friendListAdapter);
                            if(isRefresh){//下拉刷新
                                swipereLayout.setRefreshing(false);
                                isRefresh=false;
                                Toast.makeText(MainActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            friendListAdapter.notifyDataSetChanged();
                            if(isRefresh){//下拉刷新
                                swipereLayout.setRefreshing(false);
                                isRefresh=false;
                                Toast.makeText(MainActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void initView() {
        instance=MainActivity.this;
        friendListView = (ListView) findViewById(R.id.friendListView);
        showCurrentName = (TextView) findViewById(R.id.showCurrentName);
        mainHeadIcon=findViewById(R.id.mainHeadIcon);
        addBtn=findViewById(R.id.addBtn);
        popShade=findViewById(R.id.popShade);
        httpUtils = new HTTPUtils(this);
        list = new ArrayList<>();
        timer = new Timer();
        swipereLayout = (SwipeRefreshLayout) findViewById(R.id.swipereLayout);
        createPopWindow();
    }

    /**
     * 弹出组件
     */
    private void createPopWindow(){
        View convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.main_add_popwindow,null);
        addFriendBtn=convertView.findViewById(R.id.addFriendBtn);//绑定加好友
        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(convertView);
        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        popupWindow.setWidth(width*2/5);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparency));
    }

    /**
     * 返回键返回桌面，不退出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfoByHttp();
        //利用Picasso框架将头像设为圆形
        Picasso.with(this).load(MyApp.currentUser.getHeadicon()).transform(new CircleTransform()).into(mainHeadIcon);

        showCurrentName.setText(MyApp.currentUser.getNick_name());

        Intent intent=getIntent();
        if("yes".equals(intent.getStringExtra("notice"))){
            //从通知而来,重置通知
            NotificationUtils.reset();
        }
    }

    public void goAddFriend(View view) {
        startActivity(new Intent(MainActivity.this, SearchFriendActivity.class));
    }

}
