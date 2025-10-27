package com.example.card_payment.pinpad;

import android.app.Activity;
import android.app.Dialog;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvPinConstraints;
import com.pos.sdk.security.POIHsmManage;
import com.pos.sdk.security.PedRsaPinKey;
import com.example.card_payment.R;
import com.example.card_payment.utils.PinpadUtils;

public class PinPadPHY {

    public static final int PLAIN_PIN    = 1;
    public static final int ONLINE_PIN   = 2;
    public static final int ENCIPHER_PIN = 3;
    public static final int DEFAULT_KEY_NUMS = 12;
    private String DEFAULT_EXP_PIN_LEN_IND = "0,4,5,6,7,8,9,10,11,12";
    private int    DEFAULT_TIMEOUT_MS      = 30000;

    private int keyIndex;
    private int keyMode = POIHsmManage.PED_PINBLOCK_FETCH_MODE_TPK;
    private boolean isKeyboardFix = true;
    private boolean isEncrypt;
    private String  pinCard;
    private int     pinType;
    private boolean pinBypass;
    private int     pinCounter;
    private byte[]  pinRandom;
    private byte[]  pinModule;
    private byte[]  pinExponent;

    private String title;
    private String message;

    private POIHsmManage     hsmManage;
    private PinEventListener pinEventListener;
    private Dialog    dialog;
    private TextView  tvMessage;
    private TextView  tv_pwd;
    PinInputFinish pinInputFinish;

    private OrientationEventListener mOrientationListener;
    private int mOrientation = 0;
    private boolean isOrientation = false;
    public interface PinInputFinish {
        void onSuccess(byte[] pinBlock, byte[] pinKsn);
        void onError(int verifyResult, int pinTryCntOut);
    }
    public PinPadPHY(Activity context, Bundle bundle, int keyMode, int keyIndex, PinInputFinish pinInputFinish) {
        this.hsmManage = POIHsmManage.getDefault();
        this.pinEventListener = new PinEventListener();
        this.keyMode = keyMode;
        this.keyIndex = keyIndex;
        this.pinInputFinish = pinInputFinish;
        switch (bundle.getInt(EmvPinConstraints.PIN_TYPE, -1)) {
            case POIEmvCoreManager.PIN_PLAIN_PIN:
                pinType = PLAIN_PIN;
                break;
            case POIEmvCoreManager.PIN_ONLINE_PIN:
                pinType = ONLINE_PIN;
                break;
            case POIEmvCoreManager.PIN_ENCIPHER_PIN:
                pinType = ENCIPHER_PIN;
                break;
            default:
                break;
        }

        if (bundle.containsKey(EmvPinConstraints.PIN_ENCRYPT)) {
            isEncrypt = bundle.getBoolean(EmvPinConstraints.PIN_ENCRYPT);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_CARD)) {
            pinCard = bundle.getString(EmvPinConstraints.PIN_CARD);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_BYPASS)) {
            pinBypass = bundle.getBoolean(EmvPinConstraints.PIN_BYPASS);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_COUNTER)) {
            pinCounter = bundle.getInt(EmvPinConstraints.PIN_COUNTER);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_CARD_RANDOM)) {
            pinRandom = bundle.getByteArray(EmvPinConstraints.PIN_CARD_RANDOM);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_MODULE)) {
            pinModule = bundle.getByteArray(EmvPinConstraints.PIN_MODULE);
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_EXPONENT)) {
            pinExponent = bundle.getByteArray(EmvPinConstraints.PIN_EXPONENT);
        }

        switch (pinType) {
            case ONLINE_PIN:
                title = "Online PIN";
                break;
            case PLAIN_PIN:
            case ENCIPHER_PIN:
                title = "Offline PIN";
                if (pinCounter > 1) {
                    message = "PIN " + pinCounter + " ";
                } else if (pinCounter == 1) {
                    message = "PIN Last Times";
                }
                break;
            default:
                break;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.pinpad_input, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvMessage = view.findViewById(R.id.tvMessage);
        tv_pwd = view.findViewById(R.id.tv_pwd);

        tvTitle.setText(title);
        tvMessage.setText(message);

        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
        showDialog();
    }

    public int showDialog() {
        int result;

        switch (pinType) {
            case PLAIN_PIN:
                result = onVerifyPlainPin();
                break;
            case ONLINE_PIN:
                result = onOnlinePin();
                break;
            case ENCIPHER_PIN:
                result = onVerifyEncipherPin();
                break;
            default:
                result = -1;
                break;
        }

        return result;
    }

    public void closeDialog() {
        dialog.dismiss();
        hsmManage.unregisterListener(pinEventListener);
        mOrientationListener.disable();
    }

    private int onVerifyPlainPin() {
        hsmManage.registerListener(pinEventListener);
        return hsmManage.PedVerifyPlainPin(0, 0, DEFAULT_TIMEOUT_MS, DEFAULT_EXP_PIN_LEN_IND);
    }

    private int onVerifyEncipherPin() {
        hsmManage.registerListener(pinEventListener);
        if (pinModule == null) {
            return -1;
        }
        byte[] module = new byte[pinModule.length];
        byte[] exponent = new byte[pinExponent.length];
        byte[] random = new byte[pinRandom.length];
        System.arraycopy(pinModule, 0, module, 0, pinModule.length);
        System.arraycopy(pinExponent, 0, exponent, 0, pinExponent.length);
        System.arraycopy(pinRandom, 0, random, 0, pinRandom.length);

        PedRsaPinKey rsaPinKey = new PedRsaPinKey(module, exponent, random);
        return hsmManage.PedVerifyCipherPin(0, 0, DEFAULT_TIMEOUT_MS, DEFAULT_EXP_PIN_LEN_IND, rsaPinKey);
    }

    private int onOnlinePin() {
        hsmManage.registerListener(pinEventListener);
        // XCSW add for PinpadRotate start
        mOrientationListener = new OrientationEventListener(dialog.getContext(), SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                int curOrientation = PinpadUtils.doGetScreenOrientation(dialog.getContext());
                if(curOrientation != mOrientation) {
                    mOrientation = curOrientation;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isOrientation = true;
                            POIHsmManage.getDefault().PedCancelPinBlock();
                        }
                    }, 100);
                }
            }
        };
        if(PinpadUtils.doCheckSupportRotate() && mOrientationListener.canDetectOrientation()){
            mOrientation = PinpadUtils.doGetScreenOrientation(dialog.getContext());
            mOrientationListener.enable();
        }else {
            mOrientationListener.disable();
        }
        // XCSW add for PinpadRotate end

        byte[] data = new byte[24];
        if (!isEncrypt) {
            byte[] temp = CalcPinBlock.calcPinBlock(pinCard).getBytes();
            System.arraycopy(temp, 0, data, 0, 16);
        } else {
            byte[] temp = pinCard.getBytes();
            System.arraycopy(temp, 0, data, 0, 16);
        }

        byte[] formatData = {0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(formatData, 0, data, 16, 8);

        return hsmManage.PedGetPinBlock(keyMode, keyIndex, 0, DEFAULT_TIMEOUT_MS, data, DEFAULT_EXP_PIN_LEN_IND);
    }

    private class PinEventListener implements POIHsmManage.EventListener {

        private String TAG = "PinEventListener";

        @Override
        public void onPedVerifyPin(POIHsmManage manage, int type, byte[] rspBuf) {
            if (type == POIHsmManage.PED_VERIFY_PIN_TYPE_PLAIN || type == POIHsmManage.PED_VERIFY_PIN_TYPE_CIPHER) {
                int sw1 = (rspBuf[1] >= 0 ? rspBuf[1] : (rspBuf[1] + 256));
                int sw2 = (rspBuf[2] >= 0 ? rspBuf[2] : (rspBuf[2] + 256));

                if (sw1 == 0x90 && sw2 == 0x00) {
                    onPinSuccess(null, null);
                } else if (sw1 == 0x63 && (sw2 & 0xc0) == 0xc0) {
                    if ((sw2 & 0x0F) == 0) {
                        onPinError(EmvPinConstraints.VERIFY_PIN_BLOCK, 0);
                    } else {
                        onPinError(EmvPinConstraints.VERIFY_ERROR, sw2 & 0x0F);
                    }
                } else if (sw1 == 0x69 && (sw2 == 0x83 || sw2 == 0x84)) {
                    onPinError(EmvPinConstraints.VERIFY_PIN_BLOCK, 0);
                } else {
                    onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0);
                }
            } else {
                onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0);
            }
            closeDialog();
        }

        @Override
        public void onPedPinBlockRet(POIHsmManage manage, int type, byte[] rspBuf) {
            if (rspBuf[0] != 0) {
                byte[] pinBlock = new byte[rspBuf[0]];
                System.arraycopy(rspBuf, 1, pinBlock, 0, rspBuf[0]);
                if (rspBuf.length > (rspBuf[0] + 1)) {
                    byte[] ksn = new byte[rspBuf[rspBuf[0] + 1]];
                    System.arraycopy(rspBuf, rspBuf[0] + 2, ksn, 0, rspBuf[rspBuf[0] + 1]);
                    onPinSuccess(pinBlock, ksn);
                } else {
                    onPinSuccess(pinBlock, null);
                }
            }
            closeDialog();
        }

        @Override
        public void onKeyboardShow(POIHsmManage manage, byte[] keys, int timeout) {

        }

        @Override
        public void onKeyboardInput(POIHsmManage manage, int numKeys) {
            StringBuilder info = new StringBuilder();
            while (0 != (numKeys--)) {
                info.append("*");
            }
            if (info.length() <= 12) {
                tv_pwd.setText(info.toString());
            }
        }

        @Override
        public void onInfo(POIHsmManage manage, int what, int extra) {
            Log.e(TAG, "onInfo");
        }

        @Override
        public void onError(POIHsmManage manage, int what, final int extra) {

            Log.e(TAG, "onError:" + extra);

            switch (extra) {
                case 0xFFFF:
                    onPinError(EmvPinConstraints.VERIFY_CANCELED, 0);
                    closeDialog();
                    return;
                case 0xFFFC:
                    tvMessage.setText("The terminal triggers a security check.");
                    break;
                case 0xFED3:
                    tvMessage.setText("The terminal did not write the PIN key. Please check.");
                    break;
                case 0XFECF:
                    if (pinBypass) {
                        onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0);
                    } else {
                        onPinError(EmvPinConstraints.VERIFY_ERROR, 0);
                    }
                    closeDialog();
                    return;
                // XCSW add for PinpadRotate start
                case 0xFFFD:
                    if (isOrientation && PinpadUtils.doCheckSupportRotate()) {
                        tv_pwd.setText("");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                final byte[] data = new byte[24];
                                if (!isEncrypt) {
                                    byte[] temp = CalcPinBlock.calcPinBlock(pinCard).getBytes();
                                    System.arraycopy(temp, 0, data, 0, 16);
                                } else {
                                    byte[] temp = pinCard.getBytes();
                                    System.arraycopy(temp, 0, data, 0, 16);
                                }

                                byte[] formatData = {0, 0, 0, 0, 0, 0, 0, 0};
                                System.arraycopy(formatData, 0, data, 16, 8);
                                hsmManage.PedGetPinBlock(keyMode, keyIndex, 0, DEFAULT_TIMEOUT_MS, data, DEFAULT_EXP_PIN_LEN_IND);
                            }
                        }, 100);
                        return;
                    } else {
                        onPinError(EmvPinConstraints.VERIFY_CANCELED, 0);
                        closeDialog();
                        break;
                    }
                case 0xfc0b://64523
                    if(PinpadUtils.doCheckSupportRotate()){
                        return;
                    }
                    // XCSW add for PinpadRotate end
                default:
                    onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0);
                    closeDialog();
                    return;
            }

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    onPinError(EmvPinConstraints.VERIFY_CANCELED, 0);
                    closeDialog();
                }
            }, 2000);
        }

        @Override
        public void onHwSelfCheckRet(POIHsmManage manage, int type, int checkResult) {
            Log.e(TAG, "onHwSelfCheckRet");
        }

        @Override
        public void onHwSensorTriggered(POIHsmManage manage, int triggered, byte[] sensorValue, byte[] triggerTime) {
            Log.e(TAG, "onHwSensorTriggered");
        }

        @Override
        public void onPedKeyManageRet(POIHsmManage manage, int ret) {
            Log.e(TAG, "onPedKeyManageRet");
        }
    }

    private void onPinSuccess(byte[] pinBlock, byte[] pinKsn) {
        Bundle bundle = new Bundle();
        bundle.putInt(EmvPinConstraints.OUT_PIN_VERIFY_RESULT, EmvPinConstraints.VERIFY_SUCCESS);
        bundle.putInt(EmvPinConstraints.OUT_PIN_TRY_COUNTER, 0);
        if (pinBlock != null) {
            bundle.putByteArray(EmvPinConstraints.OUT_PIN_BLOCK, pinBlock);
        }
        POIEmvCoreManager.getDefault().onSetPinResponse(bundle);
        pinInputFinish.onSuccess(pinBlock, pinKsn);
    }

    private void onPinError(int verifyResult, int pinTryCntOut) {
        Bundle bundle = new Bundle();
        bundle.putInt(EmvPinConstraints.OUT_PIN_VERIFY_RESULT, verifyResult);
        bundle.putInt(EmvPinConstraints.OUT_PIN_TRY_COUNTER, pinTryCntOut);
        POIEmvCoreManager.getDefault().onSetPinResponse(bundle);
        pinInputFinish.onError(verifyResult,pinTryCntOut);
    }
    static class CalcPinBlock {

        static String calcPinBlock(String accountNumber) {
            return "0000" + extractAccountNumberPart(accountNumber);
        }

        static String extractAccountNumberPart(String accountNumber) {
            String accountNumberPart;
            accountNumberPart = takeLastN(accountNumber, 13);
            accountNumberPart = takeFirstN(accountNumberPart, 12);
            return accountNumberPart;
        }

        static String takeLastN(String str, int n) {
            if (str.length() > n) {
                return str.substring(str.length() - n);
            } else {
                if (str.length() < n) {
                    return zero(str, n);
                } else {
                    return str;
                }
            }
        }

        static String takeFirstN(String str, int n) {
            if (str.length() > n) {
                return str.substring(0, n);
            } else {
                if (str.length() < n) {
                    return zero(str, n);
                } else {
                    return str;
                }
            }
        }

        static String zero(String str, int len) {
            str = str.trim();
            StringBuilder builder = new StringBuilder(len);
            int fill = len - str.length();
            while (fill-- > 0) {
                builder.append((char) 0);
            }
            builder.append(str);
            return builder.toString();
        }
    }
}
