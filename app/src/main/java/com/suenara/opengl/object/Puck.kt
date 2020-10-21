package com.suenara.opengl.`object`

import com.suenara.opengl.`object`.data.VertexArray
import com.suenara.opengl.geometry.Cylinder
import com.suenara.opengl.geometry.Point
import com.suenara.opengl.program.ColorShaderProgram

class Puck(val radius: Float, val height: Float, numPointsAroundPuck: Int) : GLDrawable {

    private val vertexArray: VertexArray
    private val drawList: List<DrawCommand>

    init {
        ObjectBuilder.createPuck(Cylinder(Point(0f, 0f, 0f), radius, height), numPointsAroundPuck).run {
            vertexArray = VertexArray(vertexData)
            this@Puck.drawList = drawList
        }
    }

    override fun draw() {
        drawList.forEach(DrawCommand::draw)
    }

    fun bindData(colorShaderProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorShaderProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            0
        )
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }
}