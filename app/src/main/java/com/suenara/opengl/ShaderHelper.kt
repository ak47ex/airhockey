package com.suenara.opengl

import android.opengl.GLES20.*

object ShaderHelper {
    private const val TAG = "ShaderHelper"

    private const val DEBUG = true

    fun validateProgram(programObjectId: Int): Boolean {
        glValidateProgram(programObjectId)

        val validateStatus = intArrayOf(0)
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
        TAG debug "Results of validating program: ${validateStatus[0] > 0}\nLog: ${glGetProgramInfoLog(programObjectId)}"
        return validateStatus[0] > 0
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = glCreateProgram()
        requireHandler(programObjectId) { "Could not create new program." }
        glAttachShader(programObjectId, vertexShaderId)
        glAttachShader(programObjectId, fragmentShaderId)
        glLinkProgram(programObjectId)

        if (DEBUG) {
            TAG debug "Results of linking program:\n${glGetProgramInfoLog(programObjectId)}"
        }
        if (!programObjectId.isProgramLinked()) {
            glDeleteProgram(programObjectId)
            if (DEBUG) TAG error "Could not link program."
            throw IllegalStateException("Could not link program.")
        }
        return programObjectId
    }

    fun compileVertexShader(shader: String): Int = compileShader(Type.VERTEX_SHADER, shader)

    fun compileFragmentShader(shader: String): Int = compileShader(Type.FRAGMENT_SHADER, shader)

    private fun compileShader(type: Type, shader: String): Int {
        val shaderObjectId = glCreateShader(type.shaderConst)
        requireHandler(shaderObjectId) { "Could not create new shader." }
        glShaderSource(shaderObjectId, shader)
        glCompileShader(shaderObjectId)
        if (DEBUG) {
            TAG debug "Results of compiling source:\n$shader:\n${glGetShaderInfoLog(shaderObjectId)}"
        }
        val isCompiled = shaderObjectId.isShaderCompiled()
        if (!isCompiled) {
            glDeleteShader(shaderObjectId)
            if (DEBUG) TAG error "Compilation of shader failed."
            throw IllegalStateException("Compilation of shader failed.")
        }
        return shaderObjectId
    }

    private fun Int.isShaderCompiled(): Boolean {
        val compileStatus = intArrayOf(0)
        glGetShaderiv(this, GL_COMPILE_STATUS, compileStatus, 0)
        return compileStatus[0] > 0
    }

    private fun Int.isProgramLinked(): Boolean {
        val linkStatus = intArrayOf(0)
        glGetProgramiv(this, GL_LINK_STATUS, linkStatus, 0)
        return linkStatus[0] > 0
    }

    private enum class Type(val shaderConst: Int) { VERTEX_SHADER(GL_VERTEX_SHADER), FRAGMENT_SHADER(GL_FRAGMENT_SHADER) }
}