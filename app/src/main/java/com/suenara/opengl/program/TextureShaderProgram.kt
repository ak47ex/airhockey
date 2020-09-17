package com.suenara.opengl.program

import android.opengl.GLES20.GL_TEXTURE0
import android.opengl.GLES20.GL_TEXTURE_2D
import android.opengl.GLES20.glActiveTexture
import android.opengl.GLES20.glBindTexture
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUniform1i
import android.opengl.GLES20.glUniformMatrix4fv

class TextureShaderProgram : ShaderProgram(VERTEX_SOURCE, FRAGMENT_SOURCE) {

    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)
    private val uTextureUnitLocation: Int = glGetUniformLocation(program, U_TEXTURE_UNIT)

    private val aPositionLocation: Int = glGetAttribLocation(program, A_POSITION)
    private val aTextureCoordinatesLocation: Int = glGetAttribLocation(program, A_TEXTURE_COORDINATES)

    fun getPositionAttributeLocation(): Int = aPositionLocation
    fun getTextureCoordinatesAttributeLocation(): Int = aTextureCoordinatesLocation

    fun setUniforms(mtrx: FloatArray, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, mtrx, 0)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }

    companion object {
        private val VERTEX_SOURCE = """
            uniform mat4 u_Matrix;
            
            attribute vec4 a_Position;
            attribute vec2 a_TextureCoordinates;
            
            varying vec2 v_TextureCoordinates;
            
            void main() {
                v_TextureCoordinates = a_TextureCoordinates;
                gl_Position = u_Matrix * a_Position;
            }
        """.trimIndent()

        private val FRAGMENT_SOURCE = """
            precision mediump float;

            uniform sampler2D u_TextureUnit;
            varying vec2 v_TextureCoordinates;

            void main() {
                gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
            }
        """.trimIndent()
    }
}