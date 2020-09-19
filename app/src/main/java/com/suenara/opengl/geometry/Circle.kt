package com.suenara.opengl.geometry

data class Circle(val center: Point, val radius: Float) {
    fun scale(scale: Float) = copy(radius = radius * scale)
}