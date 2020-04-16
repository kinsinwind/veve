package com.kinsin.demowebsockets.util;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KINSIN on 2018/8/11.
 */

public class HTTPUtils {
    private Context context;//上下文对象
    private String url;//地址
    private static RequestQueue queue;

    public HTTPUtils(Context context) {
        this.context = context;
        if(queue==null){
            queue = Volley.newRequestQueue(context);
            System.out.println("创建了");
        }
    }

    /**
     * 发送网络请求，获得返回的json数据，字符串形式
     * @param url 需要传入地址
     * @param map 传入map
     * @param callBackVolley 接口的回调
     * @return
     */
    public void getHttpInfoPOST(String url, final Map<String,String> map, final CallBackVolley callBackVolley){
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //这里是网络请求成功后执行的内容
                callBackVolley.success(s);//使用接口回调的方式在网络请求完成之后执行对应方法
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //这里是网络请求失败后执行的内容
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //此处存放参数，这里采用的是post方式请求网络
                return map;
            }
        };
        queue.add(request);
    }

    public void getHttpInfoGET(String url, final CallBackVolley callBackVolley){
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                callBackVolley.success(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(request);
    }

    public interface CallBackVolley{
        void success(String data);
    }

}
