package com.example.card_payment.pinpad;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MaterialDialog {

    private final Context context;
    private int position;
    private final MaterialAlertDialogBuilder builder;

    public MaterialDialog(Context context) {
        this.builder = new MaterialAlertDialogBuilder(context);
        this.context = context;
    }


    public void showListConfirmChoseDialog(String title, String[] list, final OnChoseListener choseListener) {
        position = -1;

        Dialog dialog = builder.setTitle(title).setSingleChoiceItems(list, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        MaterialDialog.this.position = position;
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        choseListener.onChose(position);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        choseListener.onChose(-1);
                    }
                }).show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
    }



    public interface OnChoseListener {

        void onChose(int position);
    }

}
