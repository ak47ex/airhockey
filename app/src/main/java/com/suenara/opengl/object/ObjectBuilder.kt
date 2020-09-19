package com.suenara.opengl.`object`

import android.opengl.GLES20
import com.suenara.opengl.geometry.Circle
import com.suenara.opengl.geometry.Cylinder
import com.suenara.opengl.geometry.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


data class GeneratedData(val vertexData: FloatArray, val drawList: List<DrawCommand>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeneratedData

        if (!vertexData.contentEquals(other.vertexData)) return false
        if (drawList != other.drawList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertexData.contentHashCode()
        result = 31 * result + drawList.hashCode()
        return result
    }
}

class ObjectBuilder(sizeInVertices: Int) {

    private val vertexData = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)
    private val drawList = mutableListOf<DrawCommand>()
    private var offset = 0

    fun appendCircle(circle: Circle, numPoints: Int): ObjectBuilder {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = verticesSizeOf<Circle>(numPoints)

        append(circle.center.x, circle.center.y, circle.center.z)

        for (i in 0..numPoints) {
            val angleInRadians = (i.toFloat() / numPoints) * PI * 2f

            append(
                circle.center.x + circle.radius * cos(angleInRadians).toFloat(),
                circle.center.y,
                circle.center.z + circle.radius * sin(angleInRadians).toFloat()
            )
        }

        drawList.add { GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices) }
        return this
    }

    fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int): ObjectBuilder {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = verticesSizeOf<Cylinder>(numPoints)

        val yStart = cylinder.center.y - cylinder.height / 2f
        val yEnd = cylinder.center.y + cylinder.height / 2f

        for (i in 0..numPoints) {
            val angleInRadians = (i.toFloat() / numPoints) * PI * 2f

            val xPosition = cylinder.center.x + cylinder.radius * cos(angleInRadians).toFloat()
            val zPosition = cylinder.center.z + cylinder.radius * sin(angleInRadians).toFloat()

            append(
                xPosition, yStart, zPosition,
                xPosition, yEnd, zPosition,
            )
        }
        drawList.add { GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices) }

        return this
    }

    fun build(): GeneratedData = GeneratedData(vertexData, drawList)

    private fun append(vararg values: Float) = values.forEach { append(it) }

    private fun append(value: Float) {
        vertexData[offset++] = value
    }

    companion object {
        private const val FLOATS_PER_VERTEX = 3

        fun createPuck(puck: Cylinder, numPoints: Int): GeneratedData {
            val size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)

            val puckTop = Circle(puck.center.translateY(puck.height / 2f), puck.radius)
            return ObjectBuilder(size)
                .appendCircle(puckTop, numPoints)
                .appendOpenCylinder(puck, numPoints)
                .build()
        }

        fun createMallet(center: Point, radius: Float, height: Float, numPoints: Int): GeneratedData {
            val size = verticesSizeOf<Circle>(numPoints) * 2 + verticesSizeOf<Cylinder>(numPoints) * 2
            val baseHeight = 0.25f * height
            val handleHeight = height - baseHeight
            val handleRadius = 0.33f * radius

            val baseCircle = Circle(center.translateY(-baseHeight), radius)
            val baseCylinder = Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight)

            val handleCircle = Circle(center.translateY(height / 2f), handleRadius)
            val handleCylinder =
                Cylinder(handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight)

            return ObjectBuilder(size)
                .appendCircle(baseCircle, numPoints).appendOpenCylinder(baseCylinder, numPoints)
                .appendCircle(handleCircle, numPoints).appendOpenCylinder(handleCylinder, numPoints)
                .build()
        }

        private inline fun <reified T> verticesSizeOf(numPoints: Int): Int {
            return when (T::class) {
                Circle::class -> sizeOfCircleInVertices(numPoints)
                Cylinder::class -> sizeOfOpenCylinderInVertices(numPoints)
                else -> 0
            }
        }

        private fun sizeOfCircleInVertices(numPoints: Int) = 1 + (numPoints + 1)
        private fun sizeOfOpenCylinderInVertices(numPoints: Int) = (numPoints + 1) * 2
    }
}