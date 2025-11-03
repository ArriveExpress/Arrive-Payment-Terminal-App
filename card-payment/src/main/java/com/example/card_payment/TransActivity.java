package com.example.card_payment;

import static com.example.card_payment.utils.SharePreferenceUtils.CAN_MAGSTRIPE;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.card_payment.emv.utils.EmvCard;
import com.example.card_payment.emvconfig.BerTag;
import com.example.card_payment.emvconfig.BerTlv;
import com.example.card_payment.emvconfig.BerTlvBuilder;
import com.example.card_payment.emvconfig.BerTlvParser;
import com.example.card_payment.emvconfig.BerTlvs;
import com.example.card_payment.emvconfig.HexUtil;
import com.example.card_payment.pinpad.MaterialDialog;
import com.example.card_payment.pinpad.PinPadDialog;
import com.example.card_payment.pinpad.PinPadPHY;
import com.example.card_payment.utils.AppExecutors;
import com.example.card_payment.utils.BundleUtil;
import com.example.card_payment.utils.DialogUtils;
import com.example.card_payment.utils.ParameterInit;
import com.example.card_payment.utils.SharePreferenceUtils;
import com.example.card_payment.utils.Utils;
import com.pos.sdk.accessory.POIGeneralAPI;
import com.pos.sdk.emvcore.IPosEmvCoreListener;
import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvCardInfoConstraints;
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvOnlineConstraints;
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvTransDataConstraints;
import com.pos.sdk.emvcore.PosEmvErrorCode;
import com.pos.sdk.security.POIHsmManage;
import com.pos.sdk.utils.PosByteArray;
import com.pos.sdk.utils.PosUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransActivity extends BaseActivity {

    private static final String TAG = "TransActivity";
//    private TextView tvMessage1;
    private TextView tv_cancel;
    private int transType = -1;

    private boolean isFallBack;
    private POIEmvCoreManager emvCoreManager;
    private POIEmvCoreListener emvCoreListener;

//    private String eAmount = "0";
    private int mTransResult = -1;
    private byte[] transData;

//    private LinearLayout ll_light;
//    private ImageView iv1, iv2, iv3, iv4, iv_present_hand;

    public final static String TransResult_Amount = "TransResult_Amount";
    public final static String TransResult_Data = "TransResult_Data";
    public final static String TransResult_Code = "TransResult_Code";
    public final static String TransResult_Card_Type = "TransResult_Card_Type";
    private int Card_Type;

    private long getAmountInCent(double amount) {
        return (long) (amount * 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_QuickStart);
        setContentView(R.layout.activity_trans_card);
//        if (getSupportActionBar().isShowing() && ScreenUtils.getScreenHeight(this) <= 480) {
//            getSupportActionBar().hide();
//        }

        CircularGradientDrawable circularGradientDrawable = new CircularGradientDrawable();

        /*  custom animation
        View animationView = findViewById(R.id.animation);

        animationView.setBackground(circularGradientDrawable);

        animationView
                .getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        animationView.getViewTreeObserver().removeOnPreDrawListener(this);
                        circularGradientDrawable.animateGradientRadius(circularGradientDrawable, animationView);
                        return true;
                    }
                });
         */

        Intent inputIntent = getIntent();
        CardReadInfo info = inputIntent.getParcelableExtra("input");
        TextView price = findViewById(R.id.price);
        if (info != null && info.getAmount() != null) {
            price.setText(info.getAmount());
        } else {
            price.setVisibility(View.GONE);
        }
//        ll_light = findViewById(R.id.ll_light);
//        ll_light.setVisibility(View.GONE);
//        iv1 = findViewById(R.id.iv1);
//        iv2 = findViewById(R.id.iv2);
//        iv3 = findViewById(R.id.iv3);
//        iv4 = findViewById(R.id.iv4);
//        iv_present_hand = findViewById(R.id.iv_present_hand);


//        tvMessage1 = findViewById(R.id.tvMessage1);
        tv_cancel = findViewById(R.id.tv_cancel);
//        TextView tv_amount = findViewById(R.id.tv_amount);
//
//        String amountText = new StringBuilder()
//                .append("Amount $")
//                .append(eAmount)
//                .toString();
//        if (TextUtils.isEmpty(eAmount)) {
//            tv_amount.setVisibility(View.GONE);
//        } else {
//            tv_amount.setText(amountText);
//        }
        transType = 0;

        emvCoreManager = POIEmvCoreManager.getDefault();
        emvCoreListener = new POIEmvCoreListener();

        findViewById(R.id.manual).setOnClickListener(v -> {
            TransResult result = new TransResult(true, "", -1, -1, null);
            Intent intent = new Intent();
            intent.putExtra("result", result);
            TransActivity.this.setResult(RESULT_OK, intent);
            TransActivity.this.finish();
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Date cardDate = new Date();
//                Calendar calendar = Calendar.getInstance();
//                calendar.clear(Calendar.ZONE_OFFSET);
//                calendar.setTime(cardDate);
//                TransResult result = new TransResult("4266841848765853",
//                        /* month */ calendar.get(Calendar.MONTH) + 1,
//                        /* year */ calendar.get(Calendar.YEAR));
//                Intent intent = new Intent();
//                intent.putExtra("result", result);
//                TransActivity.this.setResult(RESULT_OK, intent);
//                finish();

                try {
                    emvCoreManager.stopTransaction();
                } catch (Exception e) {
                } finally {
                    finish();
                }
            }
        });

        onTransStart();
//        setPad();
    }

    void setPad() {
        int keyMode = POIHsmManage.PED_PINBLOCK_FETCH_MODE_TPK;
        int keyIndex = ParameterInit.KeyIndexConstants.SESSION_PIN_KEY_INDEX;
        keyMode = POIHsmManage.PED_PINBLOCK_FETCH_MODE_TPK;
        keyIndex = ParameterInit.KeyIndexConstants.SESSION_PIN_KEY_INDEX;
        Bundle bundle = new Bundle();
        bundle.putInt(POIEmvCoreManager.EmvPinConstraints.PIN_TYPE, 1);
        bundle.putString(POIEmvCoreManager.EmvPinConstraints.PIN_CARD, "3456789098765456789");
        PinPadPHY dialog = new PinPadPHY(TransActivity.this, bundle, keyMode, keyIndex, new PinPadPHY.PinInputFinish() {
            @Override
            public void onSuccess(byte[] pinBlock, byte[] pinKsn) {
            }

            @Override
            public void onError(int verifyResult, int pinTryCntOut) {

            }
        });
    }

    private void onTransStart() {

//        TranslateAnimation translateAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(TransActivity.this, R.anim.anim_move);
//        iv_present_hand.startAnimation(translateAnimation);

        long amount = 0;
        long amountOther = 0;
//        if (!TextUtils.isEmpty(eAmount)) {
//            amount = getAmountInCent(Double.parseDouble(eAmount));
//        }
//        ll_light.setVisibility(View.GONE);

//        tvMessage1.setText(R.string.present_card);
//        TextView tv_loading_dot1 = findViewById(R.id.tv_loading_dot1);
//        TextView tv_loading_dot2 = findViewById(R.id.tv_loading_dot2);
//        TextView tv_loading_dot3 = findViewById(R.id.tv_loading_dot3);
//        startLightAnim(tv_loading_dot1,1200,0,-1);
//        startLightAnim(tv_loading_dot2,1200,1,-1);
//        startLightAnim(tv_loading_dot3,1200,2,-1);

        try {
            Bundle bundle = new Bundle();

            bundle.putInt(EmvTransDataConstraints.TRANS_TYPE, transType);
            bundle.putLong(EmvTransDataConstraints.TRANS_AMOUNT, amount);
            bundle.putLong(EmvTransDataConstraints.TRANS_AMOUNT_OTHER, amountOther);

            if (isFallBack) {
                bundle.putInt(EmvTransDataConstraints.TRANS_MODE, POIEmvCoreManager.DEVICE_MAGSTRIPE);
                bundle.putBoolean(EmvTransDataConstraints.TRANS_FALLBACK, true);
            } else {
                int mode = 0;
                //SupportContact
                mode |= POIEmvCoreManager.DEVICE_CONTACT;
                //SupportContactless
                mode |= POIEmvCoreManager.DEVICE_CONTACTLESS;
                //SupportMagstripe
                mode |= POIEmvCoreManager.DEVICE_MAGSTRIPE;
                bundle.putInt(EmvTransDataConstraints.TRANS_MODE, mode);
                bundle.putBoolean(EmvTransDataConstraints.TRANS_FALLBACK, true);
            }

            bundle.putInt(EmvTransDataConstraints.TRANS_TIMEOUT, 60);

            //Todo// When the magstripe or contact card recognized as contactless, when detect contactless, whether wait magstripe(or contact) card
//            bundle.putBoolean(EmvTransDataConstraints.SPECIAL_CONTACT, false);  //normal is true
//            bundle.putBoolean(EmvTransDataConstraints.SPECIAL_MAGSTRIPE, true);  //normal is true

//            Todo// When the magstripe or contact card recognized as contactless,when detect contactless, then wait magstripe(or contact) card time(ms)
//            Todo this time will delay the contactless
//            bundle.putInt(EmvTransDataConstraints.SPECIAL_CONTACT_TIME, 500);
//            bundle.putInt(EmvTransDataConstraints.SPECIAL_MAGSTRIPE_TIME, 500);

//            bundle.putBoolean(EmvTransDataConstraints.TRANS_FALLBACK, true);
//            transType = POIEmvCoreManager.EMV_REFUND;
            transType = POIEmvCoreManager.EMV_GOODS;
            bundle.putInt(EmvTransDataConstraints.TRANS_TYPE, transType);
//            bundle.putByte(EmvTransDataConstraints.SPECIAL_TYPE,
//                    PosUtils.hexStringToBytes("20")[0]);

//            bundle.putByte(EmvTransDataConstraints.SPECIAL_TYPE, HexUtil.parseHex("30")[0]);


            bundle.putBoolean(EmvTransDataConstraints.USE_FILTER, true);
            bundle.putBoolean(EmvTransDataConstraints.USE_DELAY_PIN, true);
//            getSdkVersion();
            //bundle.putBoolean(EmvTransDataConstraints.SPECIAL_CONTACT, true);
            //bundle.putInt(EmvTransDataConstraints.SPECIAL_CONTACT_TIME, 500);
            boolean isMag = SharePreferenceUtils.getBoolean(TransActivity.this, CAN_MAGSTRIPE, false);
            if (isMag) {
                bundle.putBoolean(EmvTransDataConstraints.SPECIAL_MAGSTRIPE, true);
                bundle.putInt(EmvTransDataConstraints.SPECIAL_MAGSTRIPE_TIME, 500);
            } else {
                bundle.putBoolean(EmvTransDataConstraints.SPECIAL_MAGSTRIPE, false);
                bundle.putInt(EmvTransDataConstraints.SPECIAL_MAGSTRIPE_TIME, 20);
            }


            bundle.putBoolean(EmvTransDataConstraints.USE_CARD_READ_SUCCESS, true);

            Log.d("onTransStart", "onTransStart bundle: " +
                    BundleUtil.showKeyTypesInBundle(bundle));

            int result = emvCoreManager.startTransaction(bundle, emvCoreListener);

            isFallBack = false;

            if (PosEmvErrorCode.EXCEPTION_ERROR == result) {
                Toast.makeText(this, "startTransaction exception error", Toast.LENGTH_LONG).show();
            } else if (PosEmvErrorCode.EMV_ENCRYPT_ERROR == result) {
                Toast.makeText(this, "startTransaction encrypt error", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class POIEmvCoreListener extends IPosEmvCoreListener.Stub {

        @Override
        public void onEmvProcess(final int type, Bundle bundle) {
            Log.d(TAG, "onEmvProcess type:" + type);
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
//                    ll_light.setVisibility(View.VISIBLE);
//                    int time = 110;
//                    startLightAnim(iv1, time, 0);
//                    startLightAnim(iv2, time, 1);
//                    startLightAnim(iv3, time, 2);
//                    startLightAnim(iv4, time, 3);
//                    iv_present_hand.clearAnimation();
                    switch (type) {
                        case POIEmvCoreManager.DEVICE_CONTACT:
//                            tvMessage1.setText("Contact Card Trans");
                            break;
                        case POIEmvCoreManager.DEVICE_CONTACTLESS:
//                            tvMessage1.setText("Contactless Card Trans");
                            break;
                        case POIEmvCoreManager.DEVICE_MAGSTRIPE:
//                            tvMessage1.setText("Magstripe Card Trans");
                            break;
                        case PosEmvErrorCode.EMV_MULTI_CONTACTLESS:
                            onTransStart();
                            return;
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        public void onSelectApplication(final List<String> list, boolean isFirstSelect) {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    String[] names = list.toArray(new String[0]);
                    MaterialDialog dialog = new MaterialDialog(TransActivity.this);
                    dialog.showListConfirmChoseDialog("Select Application", names,
                            new MaterialDialog.OnChoseListener() {
                                @Override
                                public void onChose(int position) {
                                    emvCoreManager.onSetSelectResponse(position);
                                }
                            });
                }
            });
        }

        @Override
        public void onConfirmCardInfo(int mode, Bundle bundle) {
            Bundle outBundle = new Bundle();
            Log.d(TAG, "onConfirmCardInfo mode:" + mode);
            if (mode == POIEmvCoreManager.CMD_AMOUNT_CONFIG) {
                outBundle.putString(EmvCardInfoConstraints.OUT_AMOUNT, "11");
                outBundle.putString(EmvCardInfoConstraints.OUT_AMOUNT_OTHER, "22");
            } else if (mode == POIEmvCoreManager.CMD_TRY_OTHER_APPLICATION) {
                outBundle.putBoolean(EmvCardInfoConstraints.OUT_CONFIRM, true);
            } else if (mode == POIEmvCoreManager.CMD_ISSUER_REFERRAL) {
                outBundle.putBoolean(EmvCardInfoConstraints.OUT_CONFIRM, true);
            } else if (mode == POIEmvCoreManager.CMD_SELECT_APPLICATION) {
                outBundle.putByteArray(EmvCardInfoConstraints.OUT_TLV, new byte[0]);
//                emvCoreManager.onSetCardInfoResponse(outBundle);
            } else if (mode == POIEmvCoreManager.CMD_READ_RECORD) {
                outBundle.putByteArray(EmvCardInfoConstraints.OUT_TLV, new byte[0]);
//                emvCoreManager.onSetCardInfoResponse(outBundle);
            } else if (mode == POIEmvCoreManager.CMD_CARD_READ_SUCCESS) {
                Log.d(TAG, "onConfirmCardInfo: CMD_CARD_READ_SUCCESS");
                POIGeneralAPI.getDefault().setBeep(true, 200, 200);
//                return;
            }
            emvCoreManager.onSetCardInfoResponse(outBundle);
        }

        @Override
        public void onKernelType(int type) {
            Card_Type = type;
        }

        @Override
        public void onSecondTapCard() {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
//                    tvMessage1.setText("Second Tap Card");
                }
            });
        }

        @Override
        public void onRequestInputPin(final Bundle bundle) {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {

                    int keyMode = POIHsmManage.PED_PINBLOCK_FETCH_MODE_TPK;
                    int keyIndex = ParameterInit.KeyIndexConstants.SESSION_PIN_KEY_INDEX;
                    keyMode = POIHsmManage.PED_PINBLOCK_FETCH_MODE_TPK;
                    keyIndex = ParameterInit.KeyIndexConstants.SESSION_PIN_KEY_INDEX;
                    Log.d("onRequestInputPin", "PinPadDialog bundle:" + BundleUtil.showKeyTypesInBundle(bundle));
                    if ("P13".equals(Build.MODEL) || "P3".equals(Build.MODEL) || "P17".equals(Build.MODEL) || "D300".equals(Build.MODEL)) {
                        PinPadPHY dialog = new PinPadPHY(TransActivity.this, bundle, keyMode, keyIndex, new PinPadPHY.PinInputFinish() {
                            @Override
                            public void onSuccess(byte[] pinBlock, byte[] pinKsn) {
                            }

                            @Override
                            public void onError(int verifyResult, int pinTryCntOut) {

                            }
                        });
                    } else {
//                        TPK
                        PinPadDialog dialog = new PinPadDialog(TransActivity.this, bundle, keyMode, keyIndex, new PinPadDialog.PinInputFinish() {
//                        PinPadDialog dialog = new PinPadDialog(TransActivity.this, bundle, POIHsmManage.PED_PINBLOCK_FETCH_MODE_TPK, SESSION_PIN_KEY_AES_INDEX, new PinPadDialog.PinInputFinish() {

                            //                        AES-DUKPT
//                        PinPadAESDialog dialog = new PinPadAESDialog(TransActivity.this, bundle, PinPadAESDialog.PED_PINBLOCK_FETCH_MODE_DUKPT_AES, DUKPT_AES_PIN_KEY_256_INDEX, new PinPadAESDialog.PinInputFinish() {
                            @Override
                            public void onSuccess(byte[] pinBlock, byte[] pinKsn) {
                            }

                            @Override
                            public void onError(int verifyResult, int pinTryCntOut) {

                            }
                        });
                        dialog.showDialog();
                    }
                }
            });
        }

        private Bundle processOnlineResult(String data) {
            Bundle bundle = new Bundle();
            BerTlvBuilder tlvBuilder = new BerTlvBuilder();
            String authRespCode = null;
            String authCode = null;
            String script = null;
            BerTlvParser tlvParser = new BerTlvParser();
            List<BerTlv> tlvs = tlvParser.parse(PosUtils.hexStringToBytes(data)).getList();
            for (BerTlv tlv : tlvs) {
                switch (tlv.getTag().getBerTagHex()) {
                    case "8A":
                        authRespCode = tlv.getHexValue();
                        break;
                    case "91":
                        authCode = tlv.getHexValue();
                        break;
                    case "71":
                    case "72":
                        tlvBuilder.addBerTlv(tlv);
                        break;
                    default:
                        break;
                }
            }
            if (tlvBuilder.build() != 0) {
                script = PosUtils.bytesToHexString(tlvBuilder.buildArray());
            }

//            EmvOnlineConstraints.EMV_ONLINE_FAIL;
            if (authRespCode != null) {
                switch (authRespCode) {
                    case "3030":
                        bundle.putInt(EmvOnlineConstraints.OUT_AUTH_RESP_CODE, EmvOnlineConstraints.EMV_ONLINE_APPROVE);
                        bundle.putByteArray(EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE,
                                PosUtils.hexStringToBytes("3030"));
                        break;
                    case "3031":
                        bundle.putInt(EmvOnlineConstraints.OUT_AUTH_RESP_CODE, EmvOnlineConstraints.EMV_ONLINE_REFER_TO_CARD_ISSUER);
                        bundle.putByteArray(EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE,
                                PosUtils.hexStringToBytes("3031"));
                        break;
                    case "3032":
                        bundle.putInt(EmvOnlineConstraints.OUT_AUTH_RESP_CODE, EmvOnlineConstraints.EMV_ONLINE_DENIAL);
                        bundle.putByteArray(EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE,
                                PosUtils.hexStringToBytes("3032"));
                        break;
                    case "3535":
                        bundle.putInt(EmvOnlineConstraints.OUT_AUTH_RESP_CODE, EmvOnlineConstraints.EMV_ONLINE_FAIL);
                        bundle.putByteArray(EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE,
                                PosUtils.hexStringToBytes("3535"));
                        break;
                    default:
                        bundle.putInt(EmvOnlineConstraints.OUT_AUTH_RESP_CODE, EmvOnlineConstraints.EMV_ONLINE_FAIL);
                        break;
                }
            }
            if (authCode != null) {
                bundle.putByteArray(EmvOnlineConstraints.OUT_AUTH_DATA, PosUtils.hexStringToBytes(authCode));
            }
            if (script != null) {
                bundle.putByteArray(EmvOnlineConstraints.OUT_ISSUER_SCRIPT, PosUtils.hexStringToBytes(script));
            }

            return bundle;
        }

        public void onRequestOnlineProcess(final Bundle bundle) {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "here is the emv data return from the SDK:");
                    byte[] data = bundle.getByteArray(EmvOnlineConstraints.EMV_DATA);
                    if (data != null) {
                        Log.d(TAG, "Trans Data : " + HexUtil.toHexString(data));
                        StringBuffer sb = new StringBuffer();
                        BerTlvParser tlvParser = new BerTlvParser();
                        BerTlvs tlvs = tlvParser.parse(data);
                        PosByteArray outData = new PosByteArray();
                        for (BerTlv tlv : tlvs.getList()) {

                            Log.d(TAG, String.format("%1$-4s", tlv.getTag().getBerTagHex())
                                    + " : " + tlv.getHexValue());
                        }
                    }
                    Log.d(TAG, "app can pack the iso8583 DE55 data base on the EMV_DATA");

                    DialogUtils.showProgressDialog("Online Authorizing...", TransActivity.this);
                }
            });

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.dismissProgressDialog();
                    Bundle outBundle = new Bundle();
                    //Todo
                    Log.d(TAG, "will feedback the kernel with the host response");
                    Log.d(TAG, "the following is the fix data, and need to modify base on the host response");

                    //Whether the connection is successful, if successful isOnlineSuccess = true, otherwise isOnlineSuccess = false
                    boolean isOnlineSuccess = true;
                    if (isOnlineSuccess) {
                        Log.d(TAG, "please fill in the DE55 data in ISO8583 message ");
                        Log.d(TAG, "here is the hardcode sample data ");
                        outBundle = processOnlineResult("8A023030");
                    } else {
                        outBundle.putInt(EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
                                EmvOnlineConstraints.EMV_ONLINE_FAIL);
                    }
                    Log.d("onRequestOnlineProcess", BundleUtil.showKeyTypesInBundle(outBundle));
                    emvCoreManager.onSetOnlineResponse(outBundle);
                }
            });
        }

        @Override
        public void onTransactionResult(final int result, final Bundle bundle) {
            Log.d(TAG, "onTransactionResult " + result);
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    switch (result) {
                        case PosEmvErrorCode.EMV_MULTI_CONTACTLESS:
                            onTransStart();
                            return;
                        case PosEmvErrorCode.EMV_FALLBACK:
                            isFallBack = true;
                            onTransStart();
                            return;
                        case PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE:
                            onTransStart();
                            return;
                        case PosEmvErrorCode.EMV_APP_EMPTY:
                            isFallBack = true;
                            onTransStart();
                            return;
                        default:
                            break;
                    }

                    switch (result) {
                        case PosEmvErrorCode.EMV_APPROVED:
                        case PosEmvErrorCode.EMV_APPROVED_ONLINE:
                        case PosEmvErrorCode.EMV_FORCE_APPROVED:
                        case PosEmvErrorCode.EMV_DELAYED_APPROVED:
                        case PosEmvErrorCode.APPLE_VAS_APPROVED:
                            mTransResult = 0;

                            break;

                        case PosEmvErrorCode.EMV_TIMEOUT:
                            mTransResult = 1;
                            //Todo
                            break;
                        case PosEmvErrorCode.EMV_CANCEL:
                            mTransResult = 2;
                            //Todo
                            break;
                        default:
                            mTransResult = -1;
                            break;
                    }

                    try {

                        if (mTransResult != 2) {
                            transData = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.EMV_DATA);
                            if (transData != null) {
                                updateCardType(transData);
                            }

                            EmvCard emvCard = new EmvCard(transData);
                            Date cardDate = emvCard.getCardExpireDate();
                            Calendar calendar = Calendar.getInstance();
                            calendar.clear(Calendar.ZONE_OFFSET);
                            calendar.setTime(cardDate);
                            TransResult result = new TransResult(false, emvCard.getCardNumber(),
                                    /* month */ calendar.get(Calendar.MONTH) + 1,
                                    /* year */ calendar.get(Calendar.YEAR),
                                    /* cvv */ emvCard.getCvv());
                            Intent intent = new Intent();
                            intent.putExtra("result", result);
                            TransActivity.this.setResult(RESULT_OK, intent);
                            TransActivity.this.finish();
                        } else {
                            Utils.setToast(TransActivity.this, "User Cancel");
                            TransActivity.this.finish();
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    private void updateCardType(byte[] data) {
        if (Card_Type != POIEmvCoreManager.EMV_CARD_VISA) {
            return;
        }

        BerTlvParser tlvParser = new BerTlvParser();
        BerTlvs tlvs = tlvParser.parse(data);
        BerTlv tlv = tlvs.find(new BerTag("9F06"));
        if (tlv != null && tlv.getHexValue().contains("A000000333")) {
            Card_Type = POIEmvCoreManager.EMV_CARD_UNIONPAY;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        DialogUtils.dismissProgressDialog();
    }

    private void startLightAnim(View view, int time, int index) {
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(time);
        anim.setFillAfter(true);
        anim.setStartOffset(time * index);
        view.startAnimation(anim);
    }

    private void startLightAnim(View view, int time, int index, int repeat) {
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(time);
        anim.setFillAfter(true);
        anim.setRepeatCount(repeat);
        anim.setStartOffset(time * index);
        view.startAnimation(anim);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }
}


