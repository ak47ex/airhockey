package com.suenara.opengl.program

import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUniform3f
import android.opengl.GLES20.glUniform4f
import android.opengl.GLES20.glUniformMatrix4fv

class ColorShaderProgram : ShaderProgram(VERTEX_SOURCE, FRAGMENT_SOURCE) {

    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)
    private val uColorLocation: Int = glGetUniformLocation(program, U_COLOR)

    private val aPositionLocation: Int = glGetAttribLocation(program, A_POSITION)

    fun getPositionAttributeLocation(): Int = aPositionLocation

    fun setUniforms(mtrx: FloatArray, r: Float, g: Float, b: Float) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, mtrx, 0)
        glUniform4f(uColorLocation, r, g, b, 1f)
    }

    companion object {
        private val VERTEX_SOURCE = """
            uniform mat4 u_Matrix;
            attribute vec4 a_Position;

            void main()
            {
                gl_Position = u_Matrix * a_Position;
            }
        """.trimIndent()
        private val FRAGMENT_SOURCE = """
            precision mediump float;
            uniform vec4 u_Color;

            void main()
            {
                gl_FragColor = u_Color;
            }
        """.trimIndent()
    }
}