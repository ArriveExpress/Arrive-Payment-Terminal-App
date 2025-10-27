package com.arrive.terminal.core.ui.utils

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(api = VERSION_CODES.P)
fun isAtLeastPie() = VERSION.SDK_INT >= VERSION_CODES.P

@ChecksSdkIntAtLeast(api = VERSION_CODES.Q)
fun isAtLeastAndroidQ() = VERSION.SDK_INT >= VERSION_CODES.Q

@ChecksSdkIntAtLeast(api = VERSION_CODES.R)
fun isAtLeastAndroidR() = VERSION.SDK_INT >= VERSION_CODES.R

@ChecksSdkIntAtLeast(api = VERSION_CODES.S)
fun isAtLeastAndroidS() = VERSION.SDK_INT >= VERSION_CODES.S

@ChecksSdkIntAtLeast(api = VERSION_CODES.TIRAMISU)
fun isAtLeastAndroidTiramisu() = VERSION.SDK_INT >= VERSION_CODES.TIRAMISU
