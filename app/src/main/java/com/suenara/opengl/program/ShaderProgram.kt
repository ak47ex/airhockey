package com.suenara.opengl.program

import android.opengl.GLES20
import com.suenara.opengl.utils.ShaderHelper

open class ShaderProgram(vertexSource: String, fragmentSource: String) {

    protected val program: Int = ShaderHelper.buildProgram(vertexSource, fragmentSource)

    fun useProgram() {
        GLES20.glUseProgram(program)
    }

    companion object {
        const val U_MATRIX = "u_Matrix"
        const val U_TEXTURE_UNIT = "u_TextureUnit"
        const val U_COLOR = "u_Color"

        const val A_POSITION = "a_Position"
        const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    }
}