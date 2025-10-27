package com.arrive.terminal.core.ui.utils

import android.os.Looper

fun isUI() = Looper.getMainLooper().thread == Thread.currentThread()