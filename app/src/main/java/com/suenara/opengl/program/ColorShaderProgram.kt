package com.suenara.opengl.program

import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUniformMatrix4fv

class ColorShaderProgram : ShaderProgram(VERTEX_SOURCE, FRAGMENT_SOURCE) {

    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)

    private val aPositionLocation: Int = glGetAttribLocation(program, A_POSITION)
    private val aColorLocation: Int = glGetAttribLocation(program, A_COLOR)

    fun getPositionAttributeLocation(): Int = aPositionLocation
    fun getColorAttributeLocation(): Int = aColorLocation

    fun setUniforms(mtrx: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, mtrx, 0)
    }

    companion object {
        private val VERTEX_SOURCE = """
            uniform mat4 u_Matrix;

            attribute vec4 a_Position;
            attribute vec4 a_Color;

            varying vec4 v_Color;

            void main()
            {
                v_Color = a_Color;

                gl_PointSize = 10.0;
                gl_Position = u_Matrix * a_Position;
            }
        """.trimIndent()
        private val FRAGMENT_SOURCE = """
            precision mediump float;
            varying vec4 v_Color;

            void main()
            {
                gl_FragColor = v_Color;
            }
        """.trimIndent()
    }
}