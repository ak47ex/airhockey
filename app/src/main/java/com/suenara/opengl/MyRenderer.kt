package com.suenara.opengl

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(
    vertexShaderSource: String,
    fragmentShaderSource: String
) : GLSurfaceView.Renderer {

    private val vertexData: FloatBuffer

    private val vertexShader by lazy { ShaderHelper.compileVertexShader(vertexShaderSource) }
    private val fragmentShader by lazy { ShaderHelper.compileFragmentShader(fragmentShaderSource) }
    private val program by lazy { ShaderHelper.linkProgram(vertexShader, fragmentShader) }

    private var uColorLocation = 0
    private var aPositionLocation = 0

    init {
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply { put(tableVerticesWithTriangles) }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        if (DEBUG) {
            ShaderHelper.validateProgram(program)
        }
        glUseProgram(program)
        uColorLocation = glGetUniformLocation(program, U_COLOR)
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        vertexData.position(0)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData)
        glEnableVertexAttribArray(aPositionLocation)
        glClearColor(0f, 0f, 0f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        glUniform4f(uColorLocation, 1f, 1f, 1f, 1f)
        glDrawArrays(GL_TRIANGLES, 0, 6)

        glUniform4f(uColorLocation, 1f, 0f, 0f, 1f)
        glDrawArrays(GL_LINES, 6, 2)

        glUniform4f(uColorLocation, 0f, 0f, 1f, 1f)
        glDrawArrays(GL_POINTS, 8, 1)

        glUniform4f(uColorLocation, 1f, 0f, 0f, 1f)
        glDrawArrays(GL_POINTS, 9, 1)

        glUniform4f(uColorLocation, 0f, 1f, 0f, 1f)
        glDrawArrays(GL_LINE_LOOP, 10, 4)
    }

    companion object {
        private const val DEBUG = true

        private const val U_COLOR = "u_Color"
        private const val A_POSITION = "a_Position"

        private const val POSITION_COMPONENT_COUNT = 2
        private const val BYTES_PER_FLOAT = 4

        private val tableVerticesWithTriangles = floatArrayOf(
            //Triangle 1
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,

            //Triangle 2
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,

            //Line 1
            -0.5f, 0f,
            0.5f, 0f,

            //Mallets
            0f, -0.25f,
            0f, 0.25f,

            //Borders
            -0.5f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
        )
    }
}