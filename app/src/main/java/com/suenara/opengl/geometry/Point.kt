package com.suenara.opengl.geometry

data class Point(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {
    fun translateX(distance: Float) = copy(x = x + distance)
    fun translateY(distance: Float) = copy(y = y + distance)
    fun translateZ(distance: Float) = copy(z = z + distance)
    fun translate(vector: Vector): Point = copy(x = x + vector.x, y = y + vector.y, z = z + vector.z)
}