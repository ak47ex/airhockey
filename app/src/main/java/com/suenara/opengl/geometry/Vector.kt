package com.suenara.opengl.geometry

import kotlin.math.sqrt

data class Vector(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {

    infix fun dotProduct(other: Vector): Float =  x * other.x + y * other.y + z * other.z

    infix fun crossProduct(other: Vector): Vector = Vector(
        x = (y * other.z) - (z * other.y),
        y = (z * other.x) - (x * other.z),
        z = (x * other.y) - (y * other.x)
    )

    fun length() = sqrt(lengthSquared())

    @Suppress("MemberVisibilityCanBePrivate")
    fun lengthSquared() = x * x + y * y + z * z

    fun scale(scaleFactor: Float): Vector = copy(x = x * scaleFactor, y = y * scaleFactor, z = z * scaleFactor)

    operator fun plus(vector: Vector): Vector = copy(x = x + vector.x, y = y + vector.y, z = z + vector.z)
    operator fun minus(vector: Vector): Vector = copy(x = x - vector.x, y = y - vector.y, z = z - vector.z)
    companion object {
        infix fun Point.vectorTo(other: Point) = Vector(other.x - x, other.y - y, other.z - z)
    }
}