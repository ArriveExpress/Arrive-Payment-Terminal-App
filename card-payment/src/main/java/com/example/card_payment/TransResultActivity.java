package com.example.card_payment;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.card_payment.emv.utils.EmvCard;
import com.example.card_payment.utils.ScreenUtils;

public class TransResultActivity extends BaseActivity {

    private ScrollView sl_receipt;
    private TextView tv_result_confirm;
    private View reslut_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_result);

        findViewById(R.id.tv_result_confirm).setOnClickListener(v -> {
            TransResultActivity.this.finish();
        });

        if(getSupportActionBar().isShowing() && ScreenUtils.getScreenHeight(this) <= 480) {
            getSupportActionBar().hide();
        }
        int code = getIntent().getIntExtra(TransActivity.TransResult_Code,-1);
        int Card_Type = getIntent().getIntExtra(TransActivity.TransResult_Card_Type,0);
        String amount = getIntent().getStringExtra(TransActivity.TransResult_Amount);
        byte[] data = getIntent().getByteArrayExtra(TransActivity.TransResult_Data);
        ImageView imageView = findViewById(R.id.iv_result_image);
        reslut_line = findViewById(R.id.reslut_line);
        TextView tv_result = findViewById(R.id.tv_result);
        TextView tv_card_number = findViewById(R.id.tv_card_number);
        TextView tv_card_user = findViewById(R.id.tv_card_user);
        TextView tv_amount = findViewById(R.id.tv_amount);
        tv_result_confirm = findViewById(R.id.tv_result_confirm);
        sl_receipt = findViewById(R.id.sl_receipt);
        LinearLayout content = findViewById(R.id.content);
        LinearLayout result_data_ll = findViewById(R.id.result_data_ll);
        tv_result_confirm.setVisibility(View.GONE);
        tv_amount.setText(amount);
        if (data != null) {
            EmvCard emvCard = new EmvCard(data);

            if (emvCard.getCardNumber() != null) {
                tv_card_number.setText(emvCard.getCardNumber());
            } else {
                tv_card_number.setText("");
            }

//            tv_card_user.setText(EmvCardType.getCardType(Card_Type));

            if (emvCard.getCardHolderName() != null) {
                tv_card_user.setText(emvCard.getCardHolderName());
            }
        }

        if (code == 0) {
            sl_receipt.setVisibility(View.VISIBLE);
            tv_result_confirm.setVisibility(View.VISIBLE);
            imageView.clearAnimation();
            tv_result.setText("Success");
            tv_result_confirm.setText("Done");
            imageView.setImageResource(R.drawable.result_success);
            tv_result_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransResultActivity.this.finish();
//                    sl_receipt.scrollTo(0,0);
//                    slideUp(content);
////                String data = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaa";
////                Printer.printeImage(TransResultActivity.this, Printer.generateBitmap(TransResultActivity.this,data,"fonts/SBSansCondMonoRegular.ttf"));
////                    Printer.printeImage(TransResultActivity.this, layoutToBitmap(content), new Printer.PrintFinish() {
////                    View viewBitmap = LayoutInflater.from(TransResultActivity.this).inflate(R.layout.receipt_content, null);
//                    Printer.printeImage(TransResultActivity.this, layoutToBitmap(TransResultActivity.this), new Printer.PrintFinish() {
//                        @Override
//                        public void onSuccess(String data) {
//                        }
//
//                        @Override
//                        public void onError(int errorCode) {
//                            content.clearAnimation();
//                            if(POIPrinterManager.ERROR_NO_PAPER == errorCode){
//                                DialogUtils.showAlertDialogCenter(TransResultActivity.this, new DialogUtils.DialogCallback() {
//                                    @Override
//                                    public void onConfirm() {
//
//                                    }
//
//                                    @Override
//                                    public void onCancel() {
//
//                                    }
//                                });
//                            }
//
//                        }
//                    });

                }
            });

        } else if (code == -1) {
            sl_receipt.setVisibility(View.GONE);
            tv_result_confirm.setVisibility(View.GONE);
            reslut_line.setVisibility(View.GONE);
            imageView.clearAnimation();
            tv_result.setText("Failed");
            imageView.setImageResource(R.drawable.result_fail);
        } else if (code == 1) {
            sl_receipt.setVisibility(View.GONE);
            tv_result_confirm.setVisibility(View.GONE);
            result_data_ll.setVisibility(View.GONE);
            reslut_line.setVisibility(View.GONE);
            imageView.clearAnimation();
            tv_result.setText("Timeout");
            imageView.setImageResource(R.drawable.result_timeout);
        } else {
            sl_receipt.setVisibility(View.GONE);
            tv_result_confirm.setVisibility(View.GONE);
            result_data_ll.setVisibility(View.GONE);
            reslut_line.setVisibility(View.GONE);
            startLoadingAnimation(imageView);
            tv_result.setText("Waiting for payment...");
            imageView.setImageResource(R.drawable.result_waiting);
        }

//        btnInit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //init key
//                tvMessage1.setText("Initing...");
//                boolean EraseAllKey = true;
//                btnInit.setEnabled(false);
//
//
//            }
//        });

//        edtAmount.setShowSoftInputOnFocus(false);
//        edtAmount.requestFocus();
//        edtAmount.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(edtAmount.getWindowToken(), 0);
////                AppExecutors.getInstance().mainThread().execute(new Runnable() {
////                    @Override
////                    public void run() {
////
////                    }
////                });
//
//
//            }
//        });
//        edtAmount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ValueAnimator animator= ValueAnimator.ofFloat(1f,0f,1f);
//                animator.setDuration(500);
//                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        tableLayout.setAlpha(animation.getAnimatedFraction());
//                    }
//                });
//                animator.start();
//            }
//        });
//        btnTrans.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onTransStart();
//            }
//        });
//
//
//        transType = 0;
//
//        emvCoreManager = POIEmvCoreManager.getDefault();
//        emvCoreListener = new POIEmvCoreListener();

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.MODEL!= null){
            if("N4".equals(Build.MODEL) || "L200".equals(Build.MODEL) || "P17".equals(Build.MODEL) || Build.MODEL.startsWith("P12")) {
                sl_receipt.setVisibility(View.GONE);
                reslut_line.setVisibility(View.GONE);
                tv_result_confirm.setVisibility(View.GONE);

            }
        }
    }

    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0, 0,                // toXDelta
                0-view.getHeight()-20  // fromYDelta
        );
        animate.setDuration(3500);
        animate.setFillBefore(true);
        view.startAnimation(animate);
    }

    public Bitmap layoutToBitmap(Activity activity) {
        XmlResourceParser xmlParser = getResources().getLayout(R.layout.receipt_content);

        View view = LayoutInflater.from(activity).inflate(xmlParser, null);

//        TextView title = view.findViewById(R.id.title);
//        title.setText("=======GET THIS========");
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        width = width-(int)outMetrics.density*50;
//
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST));

        view.layout(0, 0,
                view.getMeasuredWidth(),
                view.getMeasuredHeight());


        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight()+200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        view.draw(canvas);
        return bitmap;
    }

    private void startLoadingAnimation(ImageView iv) {
        Animation animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(1000);
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        iv.startAnimation(animation);//开始动画
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.tv_result_confirm) {
//            TransResultActivity.this.finish();
//        }
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


}
