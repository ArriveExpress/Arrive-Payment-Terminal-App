package com.example.card_payment.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utils {

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentTime = dateFormat.format(date);
        return currentTime;
    }
    public static String getCurrentTime2() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String currentTime = dateFormat.format(date);
        return currentTime;
    }
    public static String getRandomData() {
        Random random = new Random();
        int randomNumber = random.nextInt(9000000)+1000000;
        return randomNumber+"";
    }

    public static void setToast(Context context,String content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        //绑定视图
        LinearLayout layout = (LinearLayout) toast.getView();
        //设置背景 我这里设置的是纯颜色 可以设置任何资源文件
//        layout.setBackgroundResource(R.color.yellow_red);
        //设置圆角
        layout.setClipToOutline(true);
        layout.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 10);
            }
        });
        //获取Toast默认文字显示ID
        TextView tv = (TextView) layout.getChildAt(0);
        //设置字体大小
        tv.setTextSize(16);
        //设置字体颜色
        tv.setTextColor(Color.RED);
        //Toast显示的位置
        toast.setGravity(Gravity.TOP, 0, 100);
            /* Gravity.CENTER：中间
               Gravity.BOTTOM：下方
               Gravity.TOP：上方
               Gravity.RIGHT：右边
               Gravity.LEFT：左*/
        toast.show();
    }
}
