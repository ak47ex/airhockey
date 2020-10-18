package com.suenara.opengl.utils

object TimeUtils {
    const val NANOSECONDS_IN_MILLISECOND = 1_000_000L
    const val NANOSECONDS_IN_SECOND = 1_000_000_000L
    const val MILLISECONDS_IN_SECOND = 1_000L

    fun millisToNanos(millis: Double): Double {
        return millis * NANOSECONDS_IN_MILLISECOND
    }

    fun millisToNanos(millis: Long): Double {
        return millis * NANOSECONDS_IN_MILLISECOND.toDouble()
    }

    fun nanosToMillis(nanos: Double): Double {
        return nanos / NANOSECONDS_IN_MILLISECOND.toDouble()
    }

    fun nanosToMillis(nanos: Long): Double {
        return nanos / NANOSECONDS_IN_MILLISECOND.toDouble()
    }

    fun secondsToMillis(seconds: Double): Double {
        return seconds * MILLISECONDS_IN_SECOND
    }

    fun secondsToMillis(seconds: Long): Double {
        return seconds * MILLISECONDS_IN_SECOND.toDouble()
    }

    fun millisToSeconds(millis: Double): Double {
        return millis / MILLISECONDS_IN_SECOND.toDouble()
    }

    fun millisToSeconds(millis: Long): Double {
        return millis / MILLISECONDS_IN_SECOND.toDouble()
    }

    fun secondsToNanos(seconds: Double): Double {
        return seconds * NANOSECONDS_IN_SECOND
    }

    fun secondsToNanos(seconds: Long): Double {
        return seconds * NANOSECONDS_IN_SECOND.toDouble()
    }

    fun nanosToSecond(nanos: Double): Double {
        return nanos / NANOSECONDS_IN_SECOND
    }

    fun nanosToSecond(nanos: Long): Double {
        return nanos / NANOSECONDS_IN_SECOND.toDouble()
    }
}