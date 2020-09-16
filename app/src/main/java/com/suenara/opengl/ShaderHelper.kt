package com.suenara.opengl

import android.opengl.GLES20

object ShaderHelper {
    private const val TAG = "ShaderHelper"

    private const val DEBUG = true

    fun validateProgram(programObjectId: Int): Boolean {
        GLES20.glValidateProgram(programObjectId)

        val validateStatus = intArrayOf(0)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)
        TAG debug "Results of validating program: ${validateStatus[0] > 0}\nLog: ${GLES20.glGetProgramInfoLog(programObjectId)}"
        return validateStatus[0] > 0
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = GLES20.glCreateProgram()
        requireHandler(programObjectId) { "Could not create new program." }
        GLES20.glAttachShader(programObjectId, vertexShaderId)
        GLES20.glAttachShader(programObjectId, fragmentShaderId)
        GLES20.glLinkProgram(programObjectId)

        if (DEBUG) {
            TAG debug "Results of linking program:\n${GLES20.glGetProgramInfoLog(programObjectId)}"
        }
        if (!programObjectId.isProgramLinked()) {
            GLES20.glDeleteProgram(programObjectId)
            if (DEBUG) TAG error "Could not link program."
            throw IllegalStateException("Could not link program.")
        }
        return programObjectId
    }

    fun compileVertexShader(shader: String): Int = compileShader(Type.VERTEX_SHADER, shader)

    fun compileFragmentShader(shader: String): Int = compileShader(Type.FRAGMENT_SHADER, shader)

    private fun compileShader(type: Type, shader: String): Int {
        val shaderObjectId = GLES20.glCreateShader(type.shaderConst)
        requireHandler(shaderObjectId) { "Could not create new shader." }
        GLES20.glShaderSource(shaderObjectId, shader)
        GLES20.glCompileShader(shaderObjectId)
        if (DEBUG) {
            TAG debug "Results of compiling source:\n$shader:\n${GLES20.glGetShaderInfoLog(shaderObjectId)}"
        }
        val isCompiled = shaderObjectId.isShaderCompiled()
        if (!isCompiled) {
            GLES20.glDeleteShader(shaderObjectId)
            if (DEBUG) TAG error "Compilation of shader failed."
            throw IllegalStateException("Compilation of shader failed.")
        }
        return shaderObjectId
    }

    private fun Int.isShaderCompiled(): Boolean {
        val compileStatus = intArrayOf(0)
        GLES20.glGetShaderiv(this, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        return compileStatus[0] > 0
    }

    private fun Int.isProgramLinked(): Boolean {
        val linkStatus = intArrayOf(0)
        GLES20.glGetProgramiv(this, GLES20.GL_LINK_STATUS, linkStatus, 0)
        return linkStatus[0] > 0
    }

    private enum class Type(val shaderConst: Int) { VERTEX_SHADER(GLES20.GL_VERTEX_SHADER), FRAGMENT_SHADER(GLES20.GL_FRAGMENT_SHADER) }
}