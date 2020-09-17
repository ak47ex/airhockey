package com.suenara.opengl

import android.content.Context
import android.content.res.Resources
import android.opengl.Matrix
import android.util.Log
import androidx.annotation.RawRes
import java.io.IOException

fun Context.readTextFile(@RawRes resId: Int): String {
    return try {
        resources.openRawResource(resId).reader().buffered().readText()
    } catch (e: IOException) {
        throw RuntimeException("Could not open resource $resId", e)
    } catch (e: Resources.NotFoundException) {
        throw RuntimeException("Resource not found $resId", e)
    }
}

fun FloatArray.multiplyMM(other: FloatArray): FloatArray {
    require(size == other.size)
    val temp = FloatArray(size)
    Matrix.multiplyMM(temp, 0, this, 0, other, 0)
    return temp
}

fun requireHandler(handler: Int, errorMessage: () -> String = {"Required handler is 0"}) {
    if (handler == 0) throw IllegalArgumentException(errorMessage())
}

infix fun String.log(info: Any) = Log.i(this, info.toString())
infix fun String.debug(info: Any) = Log.d(this, info.toString())
infix fun String.error(info: Any) = Log.e(this, info.toString())
infix fun String.warn(info: Any) = Log.w(this, info.toString())

fun <T : Any> T.d() = Log.d("d34db33f", toString())
fun <T : Any> T.w() = Log.w("d34db33f", toString())
fun <T : Any> T.e() = Log.e("d34db33f", toString())

val <T> T.debug
    get() = apply { Log.d("d34db33f", toString()) }