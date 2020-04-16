package com.kinsin.demowebsockets.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.activity.RequestFriendDetailsActivity;
import com.kinsin.demowebsockets.entity.RequestFriend;
import com.kinsin.demowebsockets.util.MyApp;
import com.kinsin.demowebsockets.util.WebSocketFriendUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/4/4.
 * PS: Not easy to write code, please indicate.
 */
public class FriendRequestAdapter extends BaseAdapter {
    private List<RequestFriend> list = null;
    private Context context;
    private ViewHolder viewHolder=null;

    public FriendRequestAdapter(List<RequestFriend> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_friend_request, null);
        viewHolder=new ViewHolder(convertView);

        viewHolder.nickNameForRequestFriendDetail.setText(list.get(position).getNick_name());
        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sends("YES",list.get(position).getUid1());
            }
        });

        viewHolder.refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sends("NO",list.get(position).getUid1());
            }
        });

        return convertView;
    }

    /**
     * 发送消息
     */
    public void sends(String isAcc,String uid1) {
        RequestFriend requestFriend=new RequestFriend();
        requestFriend.setUid1(uid1);//请求发起人，此处肯定是别人
        requestFriend.setUid2(MyApp.currentUser.getAccount());//接收人，肯定是自己
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("header","respond");
            jsonObject.put("isAcc",isAcc);
            jsonObject.put("uid1",requestFriend.getUid1());
            jsonObject.put("uid2",requestFriend.getUid2());
            WebSocketFriendUtil.getInstance().sendDataD(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static
    class ViewHolder {
        public View rootView;
        public TextView nickNameForRequestFriendDetail;
        public Button accept;
        public Button refuse;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.nickNameForRequestFriendDetail = (TextView) rootView.findViewById(R.id.nickNameForRequestFriendDetail);
            this.accept = (Button) rootView.findViewById(R.id.accept);
            this.refuse = (Button) rootView.findViewById(R.id.refuse);
        }

    }
}
