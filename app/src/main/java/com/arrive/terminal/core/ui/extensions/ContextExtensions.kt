package com.arrive.terminal.core.ui.extensions

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.LocationManager
import android.os.Build
import android.os.PersistableBundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import kotlin.math.roundToInt
import kotlin.system.exitProcess

/**
 * Resources
 */

fun Resources.dpToPxFloat(dp: Float): Float = dp * (this.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun Resources.dpToPxFloat(dp: Int): Float = this.dpToPxFloat(dp.toFloat())

fun Resources.dpToPx(dp: Float): Int = this.dpToPxFloat(dp).roundToInt()

fun Resources.dpToPx(dp: Int): Int = this.dpToPxFloat(dp).roundToInt()

fun Resources.pxToDpFloat(px: Float): Float = px / (this.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun Resources.pxToDpFloat(px: Int): Float = this.pxToDpFloat(px.toFloat())

fun Resources.pxToDp(px: Float): Int = this.pxToDpFloat(px).roundToInt()

fun Resources.pxToDp(px: Int): Int = this.pxToDpFloat(px).roundToInt()


/**
 * Context
 */

fun Context.dpToPxFloat(dp: Float): Float = this.resources.dpToPxFloat(dp)

fun Context.dpToPxFloat(dp: Int): Float = this.resources.dpToPxFloat(dp)

fun Context.dpToPx(dp: Float): Int = this.resources.dpToPx(dp)

fun Context.dpToPx(dp: Int): Int = this.resources.dpToPx(dp)

fun Context.pxToDpFloat(px: Float): Float = this.resources.pxToDpFloat(px)

fun Context.pxToDpFloat(px: Int): Float = this.resources.pxToDpFloat(px)

fun Context.pxToDp(px: Float): Int = this.resources.pxToDp(px)

fun Context.pxToDp(px: Int): Int = this.resources.pxToDp(px)


inline val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

inline val Context.locationManager: LocationManager
    get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager

inline val Context.clipboardManager: ClipboardManager
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

fun Context.showKeyboard(view: EditText) {
    view.requestFocus()
    view.setSelection(view.length())
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.toast(message: Any, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message.toString(), length).show()
}

fun Context.showNoImplementedYet() {
    toast("Not implemented yet")
}

fun Context.restartApplication() {
    val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
    val mainIntent = Intent.makeRestartActivityTask(intent.component)
    startActivity(mainIntent)
    exitProcess(0)
}

val Context.layoutInflater get() = LayoutInflater.from(this)

val Context?.isValid: Boolean get() = !this.isNotValid

val Context?.isNotValid: Boolean get() = this == null || (this is Activity && (this.isDestroyed || this.isFinishing))