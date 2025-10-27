package com.example.card_payment;

import static com.example.card_payment.utils.SharePreferenceUtils.CAN_MAGSTRIPE;
import static com.example.card_payment.utils.SharePreferenceUtils.KEY_INIT;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.card_payment.utils.AppExecutors;
import com.example.card_payment.utils.DialogUtils;
import com.example.card_payment.utils.SharePreferenceUtils;

public class TransInitActivity extends BaseActivity {

    private static final String TAG = "TransActivity";
    private TextView tvMessage0, tvMessage1, tvMessage2, tvMessage3;
    private EditText edtAmount;
    private boolean initing = false;
    boolean isTimer = false;
    private static final int MENU_ITEM_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_init);
        edtAmount = findViewById(R.id.edtAmount);
        Button btn_init_next = findViewById(R.id.btn_init_next);
        ImageView iv_del = findViewById(R.id.iv_del);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        edtAmount.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG | Paint.UNDERLINE_TEXT_FLAG);
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtAmount.setText("");
            }
        });
        edtAmount.setShowSoftInputOnFocus(false);
        edtAmount.requestFocus();
        edtAmount.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtAmount.getWindowToken(), 0);
//                AppExecutors.getInstance().mainThread().execute(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });


            }
        });

        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!isTimer && !TextUtils.isEmpty(s.toString()) && (!"0".equals(s.toString()))) {
                    isTimer = true;
                    Log.i("amount=", System.currentTimeMillis() + "");
                }
            }
        });
        edtAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f, 1f);
                animator.setDuration(500);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        tableLayout.setAlpha(animation.getAnimatedFraction());
                    }
                });
                animator.start();
            }
        });
        btn_init_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransInitActivity.this, TransActivity.class);
                intent.putExtra("amount", edtAmount.getText().toString());
                TransInitActivity.this.startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        String new01 = ((TextView) v).getText().toString();
        String old = edtAmount.getText().toString();
        if (".".equals(new01) && old.contains(".")) {
            return;
        }
        String data = old + new01;
        if (data.startsWith("0") && !data.startsWith("0.")) {
            return;
        } else if (data.startsWith(".")) {
            data = "0" + data;
        }
        edtAmount.setText(data);
        edtAmount.setSelection(edtAmount.getText().length());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean isMag = SharePreferenceUtils.getBoolean(TransInitActivity.this, CAN_MAGSTRIPE, false);
        menu.add(Menu.NONE, MENU_ITEM_ID, Menu.NONE, "SPECIAL_MAGSTRIPE")
                .setCheckable(true)
                .setChecked(isMag); // 设置初始状态为选中

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_ID:
                boolean isChecked = item.isChecked();
                item.setChecked(!isChecked);
                if (!item.isChecked()) {
                    SharePreferenceUtils.putBoolean(TransInitActivity.this, CAN_MAGSTRIPE, false);
                } else {
                    SharePreferenceUtils.putBoolean(TransInitActivity.this, CAN_MAGSTRIPE, true);
                }
                // 切换选中状态
                // 这里可以添加其他逻辑
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SharePreferenceUtils.getBoolean(TransInitActivity.this, KEY_INIT, false) && !initing) {
            initing = true;
            initKey();
        }

    }


    private void initKey() {
        AppExecutors.getInstance().diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                boolean EraseAllKey = true;
//   TODO: 2021/7/6  start mobile app

//                int result = ParameterInit.initKey(EraseAllKey);
//                ParameterInit.initEMVConifg(true);
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        initing = false;
                        DialogUtils.setIndeterminateDrawable(TransInitActivity.this, 0, R.drawable.loading_init_success);
                        SharePreferenceUtils.putBoolean(TransInitActivity.this, KEY_INIT, true);
//                        if (result == 0) {
////                            btnInit.setEnabled(false);
////                            btnInit.setText("Init Success");
//                            DialogUtils.setIndeterminateDrawable(TransInitActivity.this,0,R.drawable.loading_init_success);
//                            SharePreferenceUtils.putBoolean(TransInitActivity.this,KEY_INIT,true);
////                            tvMessage1.setText("Init Success");
////                            btnTrans.setEnabled(true);
////                            Toast.makeText(TransActivity.this, "succeed", Toast.LENGTH_SHORT).show();
//                        } else {
//                            DialogUtils.setIndeterminateDrawable(TransInitActivity.this,-1,R.drawable.loading_init_failed);
//                            SharePreferenceUtils.putBoolean(TransInitActivity.this,KEY_INIT,false);
////                            btnInit.setEnabled(true);
////                            btnInit.setText("INIT");
////                            tvMessage1.setText("Init Failure");
////                            Toast.makeText(TransInitActivity.this, "failure", Toast.LENGTH_SHORT).show();
//                        }

                    }
                });
            }
        });
    }
}
