package com.suenara.opengl.geometry

import android.opengl.Matrix
import kotlin.math.atan2
import kotlin.math.sign
import kotlin.math.sqrt

data class Vector(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {

    infix fun dotProduct(other: Vector): Float = x * other.x + y * other.y + z * other.z

    infix fun crossProduct(other: Vector): Vector = Vector(
        x = (y * other.z) - (z * other.y),
        y = (z * other.x) - (x * other.z),
        z = (x * other.y) - (y * other.x)
    )

    fun length() = sqrt(lengthSquared())

    @Suppress("MemberVisibilityCanBePrivate")
    fun lengthSquared() = x * x + y * y + z * z

    fun scale(scaleFactor: Float): Vector = copy(x = x * scaleFactor, y = y * scaleFactor, z = z * scaleFactor)
    fun scale(scaleFactorX: Float, scaleFactorY: Float, scaleFactorZ: Float): Vector =
        copy(x = x * scaleFactorX, y = y * scaleFactorY, z = z * scaleFactorZ)

    fun unit(): Vector = Vector(x.sign, y.sign, z.sign)

    fun normalize() = length().let { copy(x = x / it, y = y / it, z = z / it) }

    operator fun plus(vector: Vector): Vector = copy(x = x + vector.x, y = y + vector.y, z = z + vector.z)
    operator fun minus(vector: Vector): Vector = copy(x = x - vector.x, y = y - vector.y, z = z - vector.z)
    operator fun minus(scalar: Float): Vector = copy(x = x - scalar, y = y - scalar, z = z - scalar)

    companion object {
        infix fun Vector.angleWith(other: Vector): Float {
            val cross = crossProduct(other)
            val dot = dotProduct(other)
            return atan2(cross.length(), dot)
        }

        infix fun Point.vectorTo(other: Point) = Vector(other.x - x, other.y - y, other.z - z)

        fun Vector.rotateX(degrees: Float): Vector = rotate(degrees, 1f, 0f, 0f)
        fun Vector.rotateY(degrees: Float): Vector = rotate(degrees, 0f, 1f, 0f)
        fun Vector.rotateZ(degrees: Float): Vector = rotate(degrees, 0f, 0f, 1f)

        /**
         * counterclockwise rotate vector
         */
        fun Vector.rotate(degrees: Float, xAxis: Float, yAxis: Float, zAxis: Float): Vector {
            val mtrx = FloatArray(16)
            Matrix.setIdentityM(mtrx, 0)
            Matrix.rotateM(mtrx, 0, degrees, xAxis, yAxis, zAxis)
            val result = FloatArray(4)
            Matrix.multiplyMV(result, 0, mtrx, 0, floatArrayOf(x, y, z, 0f), 0)
            return Vector(result[0], result[1], result[2])
        }

        val ZERO = Vector(0f, 0f, 0f)
        val ONE = Vector(1f, 1f, 1f)
    }
}