package com.suenara.opengl.`object`

import android.opengl.GLES20
import com.suenara.opengl.BYTES_PER_FLOAT
import com.suenara.opengl.program.TextureShaderProgram
import com.suenara.opengl.data.VertexArray

class Table : GLDrawable {

    private val vertexArray = VertexArray(vertexData)

    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
    }

    fun bindData(shaderProgram: TextureShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            shaderProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            shaderProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            STRIDE
        )
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
        private const val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT

        private val vertexData = floatArrayOf(
            //X   |  Y  |   S  |  T
            //TRIANGLE_FAN
             0.0f,  0.0f, 0.5f, 0.5f,
            -0.5f, -0.8f,   0f, 0.9f,
             0.5f, -0.8f,   1f, 0.9f,
             0.5f,  0.8f,   1f, 0.1f,
            -0.5f,  0.8f,   0f, 0.1f,
            -0.5f, -0.8f,   0f, 0.9f,
        )
    }
}