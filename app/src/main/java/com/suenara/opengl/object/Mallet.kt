package com.suenara.opengl.`object`

import android.opengl.GLES20
import com.suenara.opengl.BYTES_PER_FLOAT
import com.suenara.opengl.program.ColorShaderProgram
import com.suenara.opengl.data.VertexArray

class Mallet : GLDrawable {

    private val vertexArray = VertexArray(vertexData)

    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2)
    }

    fun bindData(shaderProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            shaderProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            shaderProgram.getColorAttributeLocation(),
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

        private val vertexData = floatArrayOf(
            //X |  Y  | R | G | B
            0f,  -0.4f, 0f, 0f, 1f,
            0f,   0.4f, 1f, 0f, 0f,
        )
    }
}