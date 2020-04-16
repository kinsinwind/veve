package com.kinsin.demowebsockets.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.entity.FriendMap;
import com.kinsin.demowebsockets.util.CircleTransform;
import com.kinsin.demowebsockets.util.CurrentTime;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/3/30.
 * PS: Not easy to write code, please indicate.
 */
public class FriendListAdapter extends BaseAdapter {
    private Context context;
    private List<FriendMap> list = null;
    public ViewHolder viewHolder=null;

    public FriendListAdapter(Context context, List<FriendMap> list) {
        this.context = context;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_friend_list, null);
        viewHolder=new ViewHolder(convertView);

        viewHolder.showName.setText(list.get(position).getNickName());

        viewHolder.showLastMsg.setText(list.get(position).getLastMsg());
        //暂时放在客户端，之后应该会转移到服务器处理,增加细节如上午、下午、昨天、前天、本月、上月等
        if(CurrentTime.getTimes().split(" ")[0].equals(list.get(position).getLastTime().split(" ")[0])){//同一天,显示精确时间
            viewHolder.lastTime.setText(list.get(position).getLastTime().split(" ")[1]);
        }else {//不是同一天，显示日期
            viewHolder.lastTime.setText(list.get(position).getLastTime().split(" ")[0]);
        }

        if(list.get(position).getNoReadNum()>0){//有未读消息时显示,否则不显示
            viewHolder.redTip.setVisibility(View.VISIBLE);
            viewHolder.redTip.setText(list.get(position).getNoReadNum()+"");
        }else {
            viewHolder.redTip.setText(0+"");
            viewHolder.redTip.setVisibility(View.GONE);
        }

        //利用Picasso框架将头像设为圆形
        Picasso.with(context).load(list.get(position).getHeadIconUrl()).transform(new CircleTransform()).into(viewHolder.headIcon);
        return convertView;
    }

    public static
    class ViewHolder {
        public View rootView;
        public ImageView headIcon;
        public TextView showName;
        public TextView showLastMsg;
        public TextView net_status;
        public TextView lastTime;
        public TextView redTip;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.headIcon = (ImageView) rootView.findViewById(R.id.headIcon);
            this.showName = (TextView) rootView.findViewById(R.id.showName);
            this.showLastMsg = (TextView) rootView.findViewById(R.id.showLastMsg);
            this.net_status = (TextView) rootView.findViewById(R.id.net_status);
            this.lastTime = (TextView) rootView.findViewById(R.id.lastTime);
            this.redTip = (TextView) rootView.findViewById(R.id.redTip);
        }

    }
}
