package com.suenara.opengl.`object`

import com.suenara.opengl.data.VertexArray
import com.suenara.opengl.geometry.Point
import com.suenara.opengl.program.ColorShaderProgram

class Mallet(val radius: Float, val height: Float, numPointsAroundMallet: Int) : GLDrawable {

    private val vertexArray: VertexArray
    private val drawList: List<DrawCommand>

    init {
        ObjectBuilder.createMallet(Point(0f, 0f, 0f), radius, height, numPointsAroundMallet).run {
            vertexArray = VertexArray(vertexData)
            this@Mallet.drawList = drawList
        }
    }

    override fun draw() {
        drawList.forEach(DrawCommand::draw)
    }

    fun bindData(shaderProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            shaderProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            0
        )
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }
}