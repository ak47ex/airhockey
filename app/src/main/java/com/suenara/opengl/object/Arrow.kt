package com.suenara.opengl.`object`

import android.opengl.GLES20
import com.suenara.opengl.`object`.data.VertexArray
import com.suenara.opengl.program.ColorShaderProgram

class Arrow : GLDrawable {

    private val vertexArray = VertexArray(vertices)

    override fun draw() {
        GLES20.glLineWidth(10f)
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertices.size / POSITION_COMPONENT_COUNT)
    }

    fun bindData(shaderProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(0, shaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3

        private val vertices = floatArrayOf(
            0f, 0f, 0f,
            0f, 0f, 1f,
            -0.15f, 0f, 0.85f,
            0f, 0f, 1f,
            0.15f, 0f, 0.85f,
        )
    }
}