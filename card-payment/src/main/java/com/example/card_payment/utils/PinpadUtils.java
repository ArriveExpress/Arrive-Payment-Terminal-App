package com.example.card_payment.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.nio.ByteBuffer;

public class PinpadUtils {
    public static boolean doCheckSupportRotate() {
        return true;
    }

    public static ByteBuffer doTransformData(Context context, ByteBuffer bf, int valid){
        int size = bf.capacity();
        ByteBuffer buf = ByteBuffer.allocate(size);
        if(size > valid*8) {
            size = valid*8;
        }

        byte[] pos = new byte[2];
        for(int i = 0; i < size; i += 8) {
            Point left = new Point();
            pos[0] = bf.get(i);
            pos[1] = bf.get(i + 1);
            left.x = doBytesToInt(pos);
            pos[0] = bf.get(i + 2);
            pos[1] = bf.get(i + 3);
            left.y = doBytesToInt(pos);
            left = doTransformCoordinate(context, left);

            Point right = new Point();
            pos[0] = bf.get(i + 4);
            pos[1] = bf.get(i + 5);
            right.x = doBytesToInt(pos);
            pos[0] = bf.get(i + 6);
            pos[1] = bf.get(i + 7);
            right.y = doBytesToInt(pos);
            right = doTransformCoordinate(context, right);

            byte[] tmp = doIntToBytes(left.x);
            byte[] tmp1 = doIntToBytes(left.y);
            byte[] tmp2 = doIntToBytes(right.x);
            byte[] tmp3 = doIntToBytes(right.y);
            byte[] temp = new byte[8];
            temp[0] = tmp[2];
            temp[1] = tmp[3];
            temp[2] = tmp1[2];
            temp[3] = tmp1[3];
            temp[4] = tmp2[2];
            temp[5] = tmp2[3];
            temp[6] = tmp3[2];
            temp[7] = tmp3[3];
            buf.put(temp);
        }
        return buf;
    }

    public static Point doTransformCoordinate(Context context, Point point) {
        int WIDTH = doGetScreenWidthPoint(context);
        int HEIGHT = doGetScreenHeightPoint(context);
        int ROTATIOON = doGetScreenOrientation(context);

        int temp = -1;
        switch (ROTATIOON) {
            case Surface.ROTATION_0:
                break;
            case Surface.ROTATION_90:
                temp = point.x;
                point.x = HEIGHT - point.y;
                point.y = temp;
                break;
            case Surface.ROTATION_180:
                point.x = WIDTH - point.x;
                point.y = HEIGHT - point.y;
                break;
            case Surface.ROTATION_270:
                temp = point.x;
                point.x = point.y;
                point.y = WIDTH - temp;
                break;
            default:
                break;
        }
        return point;
    }

    public static int doGetScreenWidthPoint(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    public static int doGetScreenHeightPoint(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    public static int doGetScreenOrientation(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display dp = wm.getDefaultDisplay();
        return dp.getRotation();
    }

    public static byte[] doIntToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    public static int doBytesToInt(byte[] bytes) {
        int ans = 0;
        for(int i = 0; i < bytes.length; i++) {
            ans<<=8;
            ans|=(bytes[i]&0xff);
        }
        return ans;
    }
}
