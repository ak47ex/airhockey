package com.suenara.opengl.geometry

import kotlin.math.abs

data class Rectangle(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    val height: Float by lazy { abs(top - bottom) }
    val width: Float by lazy { abs(left - right) }
    val area: Float by lazy { width * height }
}