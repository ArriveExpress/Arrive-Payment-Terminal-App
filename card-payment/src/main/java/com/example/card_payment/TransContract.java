package com.example.card_payment;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TransContract extends ActivityResultContract<CardReadInfo, TransResult> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, CardReadInfo input) {
        Intent intent = new Intent(context, TransActivity.class);
        intent.putExtra("input", input);
        return intent;
    }

    @Override
    public TransResult parseResult(int i, @Nullable Intent intent) {
        if (intent != null && intent.hasExtra("result")) {
            return intent.getParcelableExtra("result");
        } else {
            return null;
        }
    }
}
