/*
 * Copyright (C) 2018 Sneyder Angulo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("unused", "NOTHING_TO_INLINE")

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager
import java.io.Serializable
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import android.app.FragmentTransaction
import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.app.NotificationManager
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v7.app.ActionBar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.*
import com.sneyder.sdmessages.utils.asS3UrlIfApplicable
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

// Toasts
fun Fragment.toast(text: String) = activity.toast(text)

fun Fragment.toast(@StringRes resId: Int) = activity.toast(resId)
fun Context.toast(text: String) = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
fun Context.toast(@StringRes resId: Int) = Toast.makeText(applicationContext, resId, Toast.LENGTH_SHORT).show()

fun Fragment.longToast(text: String) = activity.longToast(text)
fun Fragment.longToast(@StringRes resId: Int) = activity.longToast(resId)
fun Context.longToast(text: String) = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
fun Context.longToast(@StringRes resId: Int) = Toast.makeText(applicationContext, resId, Toast.LENGTH_LONG).show()


// SnackBars
inline fun Activity.snackBar(
        message: CharSequence,
        view: View = findViewById(android.R.id.content)
) = view.snackBar(message)

inline fun Activity.snackBar(
        @StringRes resId: Int,
        view: View = findViewById(android.R.id.content)
) = view.snackBar(resId)

inline fun Activity.longSnackBar(
        message: CharSequence,
        view: View = findViewById(android.R.id.content)
) = view.longSnackBar(message)

inline fun Activity.longSnackBar(
        @StringRes resId: Int,
        view: View = findViewById(android.R.id.content)
) = view.longSnackBar(resId)

inline fun Activity.snackBar(
        message: CharSequence,
        actionText: CharSequence,
        noinline action: (View) -> Unit,
        view: View = findViewById(android.R.id.content)
) = view.snackBar(message, actionText, action)

inline fun Activity.snackBar(
        @StringRes resId: Int,
        actionText: CharSequence,
        noinline action: (View) -> Unit,
        view: View = findViewById(android.R.id.content)
) = view.snackBar(resId, actionText, action)

inline fun Activity.longSnackBar(
        message: CharSequence,
        actionText: CharSequence,
        noinline action: (View) -> Unit,
        view: View = findViewById(android.R.id.content)
) = view.longSnackBar(message, actionText, action)

inline fun Activity.longSnackBar(
        @StringRes resId: Int,
        actionText: CharSequence,
        noinline action: (View) -> Unit,
        view: View = findViewById(android.R.id.content)
) = view.longSnackBar(resId, actionText, action)

inline fun Activity.longSnackBar(
        @StringRes resId: Int,
        @StringRes actionText: Int,
        noinline action: (View) -> Unit,
        view: View = findViewById(android.R.id.content)
) = view.longSnackBar(resId, actionText, action)


inline fun View.snackBar(message: CharSequence) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()

inline fun View.snackBar(@StringRes resId: Int) = Snackbar.make(this, resId, Snackbar.LENGTH_SHORT).show()

inline fun View.longSnackBar(message: CharSequence) = Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()

inline fun View.longSnackBar(@StringRes resId: Int) = Snackbar.make(this, resId, Snackbar.LENGTH_LONG).show()

inline fun View.snackBar(
        message: CharSequence,
        actionText: CharSequence,
        noinline action: (View) -> Unit
) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setAction(actionText, action)
        .show()

inline fun View.longSnackBar(
        message: CharSequence,
        actionText: CharSequence,
        noinline action: (View) -> Unit
) = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAction(actionText, action)
        .show()

inline fun View.snackBar(
        @StringRes resId: Int,
        actionText: CharSequence,
        noinline action: (View) -> Unit
) = Snackbar.make(this, resId, Snackbar.LENGTH_SHORT)
        .setAction(actionText, action)
        .show()

inline fun View.longSnackBar(
        @StringRes resId: Int,
        actionText: CharSequence,
        noinline action: (View) -> Unit
) = Snackbar.make(this, resId, Snackbar.LENGTH_LONG)
        .setAction(actionText, action)
        .show()

inline fun View.longSnackBar(
        @StringRes resId: Int,
        @StringRes actionText: Int,
        noinline action: (View) -> Unit
) = Snackbar.make(this, resId, Snackbar.LENGTH_LONG)
        .setAction(actionText, action)
        .show()

// Dimensions
inline fun View.setWidth(width: Int) {
    val params = layoutParams
    params.width = width
    layoutParams = params
}

inline fun View.setHeight(height: Int) {
    val params = layoutParams
    params.height = height
    layoutParams = params
}

inline fun View.setDimensions(width: Int, height: Int) {
    val params = layoutParams
    params.width = width
    params.height = height
    layoutParams = params
}


inline fun Context.screenDimensions(): Pair<Int, Int> {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels to metrics.heightPixels
}

inline fun Activity.screenWidth(): Int {
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

inline fun Activity.screenHeight(): Int {
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.heightPixels
}


inline fun Context.screenWidth(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.widthPixels
}

inline fun Context.screenHeight(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}

inline fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}


// Fragments
inline fun Activity.addFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        noinline func: (FragmentTransaction.() -> FragmentTransaction)? = null
) {
    fragmentManager.beginTransaction().apply {
        add(containerViewId, fragment)
        func?.let { func() }
    }.commit()
}

inline fun Activity.addFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        fragmentTag: String,
        noinline func: (FragmentTransaction.() -> FragmentTransaction)? = null
) {
    fragmentManager.beginTransaction().apply {
        add(containerViewId, fragment, fragmentTag)
        func?.let { func() }
    }.commit()
}


inline fun Activity.replaceFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        noinline func: (FragmentTransaction.() -> FragmentTransaction)? = null
) {
    fragmentManager.beginTransaction().apply {
        replace(containerViewId, fragment)
        func?.let { func() }
    }.commit()
}


inline fun Activity.replaceFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        fragmentTag: String,
        noinline func: (FragmentTransaction.() -> FragmentTransaction)? = null
) {
    fragmentManager.beginTransaction().apply {
        replace(containerViewId, fragment, fragmentTag)
        func?.let { func() }
    }.commit()
}


// Support Library fragments
inline fun AppCompatActivity.addFragment(
        @IdRes containerViewId: Int,
        fragment: android.support.v4.app.Fragment,
        noinline func: (android.support.v4.app.FragmentTransaction.() -> android.support.v4.app.FragmentTransaction)? = null
) {
    supportFragmentManager.beginTransaction().apply {
        add(containerViewId, fragment)
        func?.let { func() }
    }.commit()
}

inline fun AppCompatActivity.addFragment(
        @IdRes containerViewId: Int,
        fragment: android.support.v4.app.Fragment,
        fragmentTag: String,
        noinline func: (android.support.v4.app.FragmentTransaction.() -> android.support.v4.app.FragmentTransaction)? = null
) {
    supportFragmentManager.beginTransaction().apply {
        add(containerViewId, fragment, fragmentTag)
        func?.let { func() }
    }.commit()
}

inline fun AppCompatActivity.replaceFragment(
        @IdRes containerViewId: Int,
        fragment: android.support.v4.app.Fragment,
        noinline func: (android.support.v4.app.FragmentTransaction.() -> android.support.v4.app.FragmentTransaction)? = null
) {
    supportFragmentManager.beginTransaction().apply {
        replace(containerViewId, fragment)
        func?.let { func() }
    }.commit()
}


inline fun AppCompatActivity.replaceFragment(
        @IdRes containerViewId: Int,
        fragment: android.support.v4.app.Fragment,
        fragmentTag: String,
        noinline func: (android.support.v4.app.FragmentTransaction.() -> android.support.v4.app.FragmentTransaction)? = null
) {
    supportFragmentManager.beginTransaction().apply {
        replace(containerViewId, fragment, fragmentTag)
        func?.let { func() }
    }.commit()
}


// Dialogs
inline fun Context.alertDialog(
        title: CharSequence? = null,
        message: CharSequence? = null,
        positiveText: CharSequence? = null,
        noinline positiveEvent: (() -> Unit)? = null,
        negativeText: CharSequence? = null,
        noinline negativeEvent: (() -> Unit)? = null

): AlertDialog.Builder = AlertDialog.Builder(this).apply {
    title?.let { setTitle(title) }
    message?.let { setMessage(message) }
    positiveText?.let {
        setPositiveButton(positiveText) { _, _ ->
            positiveEvent?.let { positiveEvent() }
        }
    }
    negativeText?.let {
        setNegativeButton(negativeText) { _, _ ->
            negativeEvent?.let { negativeEvent() }
        }
    }
}


inline fun Context.selectorList(
        title: CharSequence? = null,
        items: Array<CharSequence>,
        noinline onClick: ((Int) -> Unit)? = null

): AlertDialog.Builder = AlertDialog.Builder(this).apply {
    title?.let { setTitle(title) }
    setItems(items) { _, item ->
        onClick?.let { onClick(item) }
    }
}

inline fun Context.selectorSingle(
        title: CharSequence? = null,
        items: Array<CharSequence>,
        checkedItem: Int = -1,
        noinline onClick: ((Int) -> Unit)? = null

): AlertDialog.Builder = AlertDialog.Builder(this).apply {
    title?.let { setTitle(title) }
    setSingleChoiceItems(items, checkedItem) { _, item ->
        onClick?.let { onClick(item) }
    }
}

inline fun Context.selectorMultiple(
        title: CharSequence? = null,
        items: Array<CharSequence>,
        checkedItems: BooleanArray,
        noinline onClick: ((Int, Boolean) -> Unit)? = null
) = AlertDialog.Builder(this).apply {
    title?.let { setTitle(title) }
    setMultiChoiceItems(items, checkedItems) { _, item, isChecked ->
        onClick?.let { onClick(item, isChecked) }
    }
}


// Views visibility

/**
 * Sets the view's visibility to VISIBLE
 */
inline fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Sets the view's visibility to INVISIBLE
 */
inline fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Sets the view's visibility to GONE
 */
inline fun View.gone() {
    visibility = View.GONE
}

/**
 * Toggle's view's visibility. If View is visible, then sets to gone. Else sets Visible
 * Previously knows as toggle()
 */
inline fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

@TargetApi(21)
inline fun View.exitReveal(noinline func: (() -> Unit)? = null) {
    if (isLollipopOrLater()) {
        val cx = measuredWidth / 2
        val cy = measuredHeight / 2

        val initialRadius = width / 2

        val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius.toFloat(), 0f)

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (func != null) func()
            }
        })
        anim.start()
    } else {
        if (func != null) func()
    }
}

@TargetApi(21)
inline fun View.enterReveal(noinline func: (() -> Unit)? = null) {
    if (isLollipopOrLater()) {
        val cx = measuredWidth / 2
        val cy = measuredHeight / 2

        val finalRadius = Math.max(width, height) / 2

        val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius.toFloat())

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (func != null) func()
            }
        })

        visible()
        anim.start()
    } else {
        visible()
    }
}


inline fun Context.inflate(
        @LayoutRes res: Int,
        parent: ViewGroup? = null,
        attachToRoot: Boolean = false)
        : View = LayoutInflater.from(this).inflate(res, parent, attachToRoot)


// Keyboard utils
inline fun Activity.hideSoftInput() {
    var view = currentFocus
    if (view == null) view = View(this)
    hideSoftInput(view)
}

inline fun Context.hideSoftInput(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

inline fun Context.showSoftInput(editText: EditText? = null) {
    if (editText != null) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, 0)
    } else {
        toggleSoftInput()
    }
}

inline fun Context.toggleSoftInput() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}


// Strings
inline fun String.isValidEmail(): Boolean {
    val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    val pattern = Pattern.compile(EMAIL_PATTERN)
    return pattern.matcher(this).matches()
}


// Network
/**
 * Checks for network availability
 * NOTE: Don't forget to add android.permission.ACCESS_NETWORK_STATE permission to manifest
 */
@SuppressLint("MissingPermission")
fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}


// Storage
inline fun isExternalStorageWritable(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED


// Collections
inline fun <T> Collection<T>.showValues(label: String = "") {
    debug("start -----------------------$label------------------------")
    forEach { debug(it) }
    debug("end   -----------------------$label------------------------")
}


// Math
inline fun Int.toAbsoluteValue() = Math.abs(this)

inline fun Number.pxToDp(): Float {
    val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
    return this.toFloat() / (densityDpi / 160f)
}

inline fun Number.dpToPx(): Int {
    val density = Resources.getSystem().displayMetrics.density
    return Math.round(this.toFloat() * density)
}


// Some other stuff
inline val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

@SuppressLint("all")
inline fun getDeviceId(context: Context)
        : String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)


inline val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

inline val Fragment.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(activity)

inline fun <T : Fragment> T.withArguments(vararg params: Pair<String, Any>): T {
    arguments = bundleOf(*params)
    return this
}

inline fun <T : android.support.v4.app.Fragment> T.withArguments(vararg params: Pair<String, Any>): T {
    arguments = bundleOf(*params)
    return this
}

inline fun bundleOf(vararg params: Pair<String, Any>): Bundle {
    val b = Bundle()
    for ((k, v) in params) {
        when (v) {
            is Boolean -> b.putBoolean(k, v)
            is Byte -> b.putByte(k, v)
            is Char -> b.putChar(k, v)
            is Short -> b.putShort(k, v)
            is Int -> b.putInt(k, v)
            is Long -> b.putLong(k, v)
            is Float -> b.putFloat(k, v)
            is Double -> b.putDouble(k, v)
            is String -> b.putString(k, v)
            is CharSequence -> b.putCharSequence(k, v)
            is Parcelable -> b.putParcelable(k, v)
            is Serializable -> b.putSerializable(k, v)
            is BooleanArray -> b.putBooleanArray(k, v)
            is ByteArray -> b.putByteArray(k, v)
            is CharArray -> b.putCharArray(k, v)
            is DoubleArray -> b.putDoubleArray(k, v)
            is FloatArray -> b.putFloatArray(k, v)
            is IntArray -> b.putIntArray(k, v)
            is LongArray -> b.putLongArray(k, v)
            is Array<*> -> {
                @Suppress("UNCHECKED_CAST")
                when {
                    v.isArrayOf<Parcelable>() -> b.putParcelableArray(k, v as Array<out Parcelable>)
                    v.isArrayOf<CharSequence>() -> b.putCharSequenceArray(k, v as Array<out CharSequence>)
                    v.isArrayOf<String>() -> b.putStringArray(k, v as Array<out String>)
                    else -> error("Error in bundle of: the argument passed is not valid")
                }
            }
            is ShortArray -> b.putShortArray(k, v)
            is Bundle -> b.putBundle(k, v)
            else -> error("Error in bundle of: the argument passed is not valid")
        }
    }

    return b
}

inline fun Context.email(email: String, subject: String = "", text: String = ""): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    if (subject.isNotEmpty())
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (text.isNotEmpty())
        intent.putExtra(Intent.EXTRA_TEXT, text)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
        return true
    }
    return false

}

inline fun Fragment.makeCall(number: String): Boolean = activity.makeCall(number)

@SuppressLint("MissingPermission")
inline fun Context.makeCall(number: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

inline fun Fragment.sendSMS(number: String, text: String = ""): Boolean = activity.sendSMS(number, text)

inline fun Context.sendSMS(number: String, text: String = ""): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
        intent.putExtra("sms_body", text)
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

inline fun Fragment.browse(url: String = "about:blank", newTask: Boolean = false) = activity.browse(url, newTask)

inline fun Context.browse(url: String = "about:blank", newTask: Boolean = false): Boolean {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        if (url != "about:blank" && (!url.contains("https://") || !url.contains("http://"))) {
            intent.data = Uri.parse("http://" + url)
        } else {
            intent.data = Uri.parse(url)
        }
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

inline fun Intent.clearTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) }

inline fun Intent.clearTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }

inline fun Intent.excludeFromRecents(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }

inline fun Intent.multipleTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK) }

inline fun Intent.newTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

inline fun Intent.noAnimation(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) }

inline fun Intent.noHistory(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }

inline fun Intent.singleTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }

inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.func()
    editor.apply()
}

// time

/**
 * returns daysToMillis as milliseconds
 */
infix fun Number.daysToMillis(days: Number): Long = (this.toDouble() + hoursToMillis(days.toDouble()) * 24).toLong()

/**
 * returns hoursToMillis as milliseconds
 */
infix fun Number.hoursToMillis(hours: Number): Long = (this.toDouble() + minutesToMillis(hours.toDouble()) * 60).toLong()

/**
 * returns minutesToMillis as milliseconds
 */
infix fun Number.minutesToMillis(minutes: Number): Long = (this.toDouble() + secondsToMillis(minutes.toDouble()) * 60).toLong()

/**
 * returns minutesToMillis as milliseconds
 */
infix fun Number.secondsToMillis(seconds: Number): Long = (this.toDouble() + (seconds.toDouble() * 1000).toLong()).toLong()


/**
 * returns hoursToMillis as milliseconds
 */
fun daysToMillis(hours: Number): Long = hoursToMillis(hours) * 24

/**
 * returns hoursToMillis as milliseconds
 */
fun hoursToMillis(hours: Number): Long = minutesToMillis(hours) * 60

/**
 * returns minutesToMillis as milliseconds
 */
fun minutesToMillis(minutes: Number): Long = secondsToMillis(minutes.toDouble()) * 60

/**
 * returns minutesToMillis as milliseconds
 */
fun secondsToMillis(seconds: Number): Long = (seconds.toDouble() * 1000).toLong()

fun String.formatted(pattern: String): String = DecimalFormat(pattern).format(this) ?: this


/**
 * It receives a long with time expressed in milliseconds
 * returns a Pair with minutesToMillis and secondsToMillis in a sort of clock format
 */
fun Long.toMinutesAndSecondsFormat(): Pair<Int, Int> {
    val min = (this / (1000 * 60))
    val sec = ((this / 1000) - min * 60)
    return Pair(min.toInt(), sec.toInt())
}

fun Long.toHoursAndMinutesFormat(): Pair<Int, Int> {
    val hours = (this / (1000 * 60 * 60))
    val minutes = ((this / 1000 / 60) - (hours * 60))
    return Pair(hours.toInt(), minutes.toInt())
}

infix fun Long.percentageOf(number: Long): Double {
    return this / number.toDouble() * 100
}

fun Long.asCalendar() = Calendar.getInstance().apply { timeInMillis = this@asCalendar }!!

fun newThread(block: () -> Unit) {
    object : Thread() {
        override fun run() {
            block()
        }
    }.start()
}

fun todayFromDateToDate(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    val fromDate = calendar.timeInMillis
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    val toDate = calendar.timeInMillis
    return Pair(fromDate, toDate)
}

fun yesterdayFromDateToDate(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1)
    val fromDate = calendar.timeInMillis
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    val toDate = calendar.timeInMillis
    return Pair(fromDate, toDate)
}

fun lastWeekFromDateToDate(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7)
    val fromDate = calendar.timeInMillis
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7)
    val toDate = calendar.timeInMillis
    return Pair(fromDate, toDate)
}

fun lastMonthFromDateToDate(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
    val fromDate = calendar.timeInMillis
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
    val toDate = calendar.timeInMillis
    return Pair(fromDate, toDate)
}

fun lastYearFromDateToDate(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
    val fromDate = calendar.timeInMillis
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
    val toDate = calendar.timeInMillis
    return Pair(fromDate, toDate)
}

fun daysBetweenCalendars(calendar1: Calendar, calendar2: Calendar): Long {
    val dayOne = Calendar.getInstance()
    val dayTwo = Calendar.getInstance()
    dayOne.set(Calendar.YEAR, calendar1.get(Calendar.YEAR))
    dayOne.set(Calendar.MONTH, calendar1.get(Calendar.MONTH))
    dayOne.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH))

    dayTwo.set(Calendar.YEAR, calendar2.get(Calendar.YEAR))
    dayTwo.set(Calendar.MONTH, calendar2.get(Calendar.MONTH))
    dayTwo.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH))

    val diff = dayOne.timeInMillis - dayTwo.timeInMillis
    return Math.abs(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS))
}


fun Context.saveToInternalStorage(bitmapImage: Bitmap): File {
    val cw = ContextWrapper(this)
    val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
    val myPath = File(directory, generateImageFileName())
    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(myPath)
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (fileOutputStream != null) {
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return myPath
}

@Throws(IOException::class)
@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    // Create an image file name
    val cw = ContextWrapper(this)
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "SDMessages_" + timeStamp

    val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
    return File(directory, generateImageFileName())
}


val DIR_ANDROID = "Android"
private val DIR_DATA = "data"
private val DIR_FILES = "files"
private val DIR_CACHE = "cache"

@Synchronized
fun getExternalStorageAppFilesFile(context: Context?, fileName: String?): File? {
    if (context == null) return null
    if (fileName == null) return null
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        val dirs = buildExternalStorageAppFilesDirs(Environment.getExternalStorageDirectory().absolutePath, context.packageName)
        return File(dirs, fileName)
    }
    return null
}


@Synchronized
fun buildExternalStorageAppFilesDirs(externalStoragePath: String, packageName: String): File {
    return buildPath(externalStoragePath, DIR_ANDROID, DIR_DATA, packageName, DIR_FILES)
}


@Synchronized
fun buildPath(base: String, vararg segments: String): File {
    var cur = File(base)
    for (segment in segments) {
        cur = File(cur, segment)
    }
    return cur
}

@SuppressLint("SimpleDateFormat")
fun generateImageFileName(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    return "SDMessages_$timeStamp.jpg"
}

fun TextView.setDrawableLeft(drawable: Drawable) {
    val drawables = compoundDrawables
    setCompoundDrawables(drawable, drawables[1], drawables[2], drawables[3])
}

fun String.isValidURL(): Boolean {
    return try {
        val uri = URI(this)
        uri.scheme == "http" || uri.scheme == "https"
    } catch (e: Exception) {
        false
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}


/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.replaceFragmentInActivity(fragment: android.support.v4.app.Fragment, frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

/**
 * The `fragment` is added to the container view with tag. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.addFragmentToActivity(fragment: android.support.v4.app.Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}

fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
        ViewModelProviders.of(this).get(viewModelClass)

/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun FragmentManager.transact(action: android.support.v4.app.FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

fun generateUUID() = UUID.randomUUID().toString().replace("-".toRegex(), "")

/**
 *
 */
fun List<String>.fromListToString(): String = TextUtils.join(", ", this)

fun String.fromStringToList(): List<String> {
    var string = this.trim()
    return if (string.isNotEmpty()) {
        string = string.removeSuffix(",")
        string.split(",").map { it.trim() }.filter { it.isNotEmpty() && it.isNotBlank() }
    } else emptyList()
}

fun TextView.addTextChangedListener(onTextChanged: (CharSequence) -> Unit, beforeTextChanged: (CharSequence) -> Unit = {}, afterTextChanged: (CharSequence) -> Unit = {}) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(afterTextChanged: Editable?) {
            afterTextChanged(afterTextChanged ?: return)
        }

        override fun beforeTextChanged(beforeTextChanged: CharSequence?, p1: Int, p2: Int, p3: Int) {
            beforeTextChanged(beforeTextChanged ?: return)
        }

        override fun onTextChanged(textChanged: CharSequence?, p1: Int, p2: Int, p3: Int) {
            onTextChanged(textChanged ?: return)
        }
    })
}

/**
 * Executes a statement just is no one of the arguments passed is null
 * Examples:
ifNotNull("text", 45, 4.45, null){
println("It will NOT show up")
}
ifNotNull("text", 45, 4.45){
println("It will show up")
}
 */
fun ifNotNull(vararg items: Any?, func: () -> Unit) {
    if (!items.contains(null))
        func()
}


/**
 * Callback when spinner item is selected
 */
fun Spinner.onItemSelected(
        onNothingSelect: (parent: AdapterView<*>?) -> Unit = { _ -> },
        onItemSelect: (parent: AdapterView<*>?, view: View?, position: Int?, id: Long?) -> Unit = { _, _, _, _ -> }): AdapterView.OnItemSelectedListener {

    val itemSelected = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
            onNothingSelect(p0)
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            onItemSelect(p0, p1, p2, p3)
        }

    }

    onItemSelectedListener = itemSelected
    return itemSelected
}

/**
 * Sets ArrayList of objects with an additional string conversion method for objects
 */
fun <T> Spinner.setItems(
        items: ArrayList<T>?,
        layoutResource: Int = android.R.layout.simple_spinner_dropdown_item,
        getTitle: (item: T) -> String = { a -> a.toString() }): SpinnerAdapter? {

    val finalList: ArrayList<String> = ArrayList()
    items?.forEach {
        finalList.add(getTitle(it))
    }

    val myAdapter = ArrayAdapter(this.context, layoutResource, finalList)
    adapter = myAdapter

    return adapter
}


/**
 * Sets the onClick listener on the View
 */
inline fun View.onClick(crossinline function: View.() -> Unit) {
    setOnClickListener { function() }
}


/**
 * Sets the onLongClick listener on the View
 */
inline fun View.onLongClick(crossinline function: View.() -> Unit) {
    setOnLongClickListener { function(); true }
}

/**
 * Fades in the View
 */
inline fun View.fadeIn(duration: Long = 400): ViewPropertyAnimator? {
    return animate()
            .alpha(1.0f)
            .setDuration(duration)
}

/**
 * Fades out the View
 */
inline fun View.fadeOut(duration: Long = 400): ViewPropertyAnimator? {
    return animate()
            .alpha(0.0f)
            .setDuration(duration)
}

/**
 * Fades to a specific alpha between 0 to 1
 */
inline fun View.fadeTo(alpha: Float, duration: Long = 400): ViewPropertyAnimator? {
    return animate()
            .alpha(alpha)
            .setDuration(duration)
}

/**
 * Animation: Enter from left
 */
inline fun View.enterFromLeft(duration: Long = 400): ViewPropertyAnimator? {
    val x = this.x    // store initial x
    this.x = 0f - this.width    // move to left

    return animate()
            .x(x)
            .setDuration(duration)
}

/**
 * Animation: Enter from right
 */
inline fun View.enterFromRight(duration: Long = 400): ViewPropertyAnimator? {
    val widthPixels = Resources.getSystem().displayMetrics.widthPixels    // get device width
    val x = this.x    // store initial x
    this.x = widthPixels.toFloat()    // move to right

    return animate()
            .x(x)
            .setDuration(duration)
}

/**
 * Animation: Enter from top
 */
inline fun View.enterFromTop(duration: Long = 400): ViewPropertyAnimator? {
    val y = this.y    // store initial y
    this.y = 0f - this.height    // move to top

    return animate()
            .y(y)
            .setDuration(duration)
}

/**
 * Animation: Enter from bottom
 */
inline fun View.enterFromBottom(duration: Long = 400): ViewPropertyAnimator? {
    val heightPixels = Resources.getSystem().displayMetrics.heightPixels    // get device height

    val y = this.y    // store initial y
    this.y = heightPixels.toFloat()   // move to bottom

    return animate()
            .y(y)
            .setDuration(duration)
}

/**
 * Animation: Exit to left
 */
inline fun View.exitToLeft(duration: Long = 400): ViewPropertyAnimator? {
    return animate()
            .x(0f - this.width)
            .setDuration(duration)
}

/**
 * Animation: Exit to right
 */
inline fun View.exitToRight(duration: Long = 400): ViewPropertyAnimator? {
    val widthPixels = Resources.getSystem().displayMetrics.widthPixels    // get device width

    return animate()
            .x(widthPixels.toFloat())
            .setDuration(duration)
}

/**
 * Animation: Exit to top
 */
inline fun View.exitToTop(duration: Long = 400): ViewPropertyAnimator? {
    return animate()
            .y(0f - this.height)
            .setDuration(duration)
}

/**
 * Animation: Exit to bottom
 */
inline fun View.exitToBottom(duration: Long = 400): ViewPropertyAnimator? {
    val heightPixels = Resources.getSystem().displayMetrics.heightPixels    // get device height

    return animate()
            .y(heightPixels.toFloat())
            .setDuration(duration)
}


/**
 * Adds an element to Collection if not already exists
 */
fun <T> MutableCollection<T>.addIfNew(t: T): Boolean = when {
    !contains(t) -> add(t)
    else -> false
}


/**
 * Accepts 3 text watcher methods with default empty implementation.
 * Returns the TextWatcher added to EditText
 */
fun EditText.addTextWatcher(afterTextChanged: (s: Editable?) -> Unit = { _ -> },
                            beforeTextChanged: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit = { _, _, _, _ -> },
                            onTextChanged: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit = { _, _, _, _ -> }): TextWatcher {

    val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s, start, before, count)
        }
    }

    addTextChangedListener(textWatcher)
    return textWatcher
}

fun Drawable.toBitmap(): Bitmap {
    val bitmap = if (this.intrinsicWidth <= 0 || this.intrinsicHeight <= 0) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    if (this is BitmapDrawable) {
        if (this.bitmap != null) {
            return this.bitmap
        }
    }

    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

infix fun Context.load(url: String?): Pair<Context, String>? = if (url != null && url.isNotBlank()) this to url else null
fun Pair<Context, String>?.into(imageView: ImageView, requestCreator: RequestCreator.() -> RequestCreator = { this } ) {
    if (this == null) return
    launch(UI) {
        Picasso.with(this@into.first).load(this@into.second.asS3UrlIfApplicable().await()).requestCreator().into(imageView)
    }
}
infix fun Pair<Context, String>?.into(target: Target) {
    if (this == null) return
    launch(UI) {
        Picasso.with(this@into.first).load(this@into.second.asS3UrlIfApplicable().await()).into(target)
    }
}