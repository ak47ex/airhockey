package com.suenara.opengl.utils

import android.opengl.GLES20.GL_COMPILE_STATUS
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINK_STATUS
import android.opengl.GLES20.GL_VALIDATE_STATUS
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glCompileShader
import android.opengl.GLES20.glCreateProgram
import android.opengl.GLES20.glCreateShader
import android.opengl.GLES20.glDeleteProgram
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glGetProgramInfoLog
import android.opengl.GLES20.glGetProgramiv
import android.opengl.GLES20.glGetShaderInfoLog
import android.opengl.GLES20.glGetShaderiv
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glValidateProgram

object ShaderHelper {
    private const val TAG = "ShaderHelper"

    private const val DEBUG = true

    fun buildProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)
        val program = linkProgram(vertexShader, fragmentShader)
        if (DEBUG) {
            validateProgram(program)
        }
        return program
    }

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
            throw IllegalStateException("Could not link program.".also { if (DEBUG) TAG error it })
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
            throw IllegalStateException("Compilation of shader failed.".also { if (DEBUG) TAG error it })
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