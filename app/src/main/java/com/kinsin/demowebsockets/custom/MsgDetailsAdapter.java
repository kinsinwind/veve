package com.kinsin.demowebsockets.custom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinsin.demowebsockets.R;
import com.kinsin.demowebsockets.entity.Msg;
import com.kinsin.demowebsockets.util.CircleTransform;
import com.kinsin.demowebsockets.util.MyApp;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Author by kinsin, Email kinsinlin@foxmail.com, Date on 2020/3/30.
 * PS: Not easy to write code, please indicate.
 */
public class MsgDetailsAdapter extends BaseAdapter {
    private Context context;
    private List<Msg> list = null;
    ViewHolder viewHolder;

    public MsgDetailsAdapter(Context context, List<Msg> list) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_list, null);
        viewHolder = new ViewHolder(convertView);
        String data = list.get(position).getContent();
        String belong = list.get(position).getUid1();
        Log.e("TAG-MSGDetails", "MyApp:"+MyApp.currentFriend );
        if (belong.equals(MyApp.currentUser.getAccount())) {//判断谁是发送人，谁是接收人
            viewHolder.meMessage.setVisibility(View.VISIBLE);
            viewHolder.meMessage.setText(data);
            viewHolder.meHeadIcon.setVisibility(View.VISIBLE);
            //利用Picasso框架将头像设为圆形
            Picasso.with(context).load(MyApp.currentUser.getHeadicon()).transform(new CircleTransform()).into(viewHolder.meHeadIcon);
        } else {
            viewHolder.otherMessage.setVisibility(View.VISIBLE);
            viewHolder.otherMessage.setText(data);
            viewHolder.otherHeadIcon.setVisibility(View.VISIBLE);
            //利用Picasso框架将头像设为圆形
            Picasso.with(context).load(MyApp.currentFriend.getHeadicon()).transform(new CircleTransform()).into(viewHolder.otherHeadIcon);
        }

        return convertView;
    }

    public static
    class ViewHolder {
        public View rootView;
        public ImageView otherHeadIcon;
        public ImageView meHeadIcon;
        public TextView meMessage;
        public TextView otherMessage;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.otherHeadIcon = (ImageView) rootView.findViewById(R.id.otherHeadIcon);
            this.meHeadIcon = (ImageView) rootView.findViewById(R.id.meHeadIcon);
            this.meMessage = (TextView) rootView.findViewById(R.id.meMessage);
            this.otherMessage = (TextView) rootView.findViewById(R.id.otherMessage);
        }

    }
}
