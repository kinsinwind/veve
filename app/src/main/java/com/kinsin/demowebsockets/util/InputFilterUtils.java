package com.kinsin.demowebsockets.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by VULCAN on 2018/10/14.
 */

public class InputFilterUtils implements InputFilter {
    private int len;//输入框目前字符长度
    private int maxlength;
    private EditText editText;

    public InputFilterUtils(EditText editText,int maxLength) {
        this.len = editText.getText().toString().length();
        this.editText = editText;
        this.maxlength = maxLength;
    }

    /**
     * 过滤掉emoji表情
     */
    Pattern emoji = Pattern . compile ("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]" , Pattern . UNICODE_CASE | Pattern . CASE_INSENSITIVE ) ;
    @Override
    public CharSequence filter(CharSequence source, int start , int end, Spanned spanned, int dstart, int dend) {
        Matcher emojiMatcher = emoji . matcher ( source );
        len = editText.getText().toString().length();
        if ( emojiMatcher . find () ) {//如果匹配到非法字符则不允许输入
            return "" ;
        }else{
            Log.i("SO", "filter1: len:"+len);
            if((len+source.length())>maxlength){//如果超出了字数限制就不允许再输入
                Log.i("SO", "filter2:  "+len);
                return source.toString().substring(0,(maxlength-len));
            }
        }
        return null;
    }
}
