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

    private var aColorLocation = 0
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
        aColorLocation = glGetAttribLocation(program, A_COLOR)
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        vertexData.position(0)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData)
        glEnableVertexAttribArray(aPositionLocation)

        vertexData.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData)
        glEnableVertexAttribArray(aColorLocation)

        glClearColor(0f, 0f, 0f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
        glDrawArrays(GL_LINE_STRIP, 6, 3)
        glDrawArrays(GL_POINTS, 9, 1)
        glDrawArrays(GL_POINTS, 10, 1)
        glDrawArrays(GL_LINE_LOOP, 11, 4)
    }

    companion object {
        private const val DEBUG = true

        private const val A_COLOR = "a_Color"
        private const val A_POSITION = "a_Position"

        private const val BYTES_PER_FLOAT = 4
        private const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

        private val tableVerticesWithTriangles = floatArrayOf(
            //X Y R G B
            //Triangle fan
             0.0f,    0.0f,    1f,     1f,     1f,
            -0.5f,   -0.5f,  0.7f,   0.7f,   0.7f,
             0.5f,   -0.5f,  0.7f,   0.7f,   0.7f,
             0.5f,    0.5f,  0.7f,   0.7f,   0.7f,
            -0.5f,    0.5f,  0.7f,   0.7f,   0.7f,
            -0.5f,   -0.5f,  0.7f,   0.7f,   0.7f,

            //Line 1
            -0.5f, 0f, 1f, 0f, 0f,
             0f, 0f, 1f, 0.8f, 0f,
             0.5f, 0f, 1f, 0f, 0f,

            //Mallets
            0f, -0.25f, 0f, 0f, 1f,
            0f,  0.25f, 1f, 0f, 0f,

            //Borders
            -0.5f,  0.5f, 0f, 1f, 0f,
            -0.5f, -0.5f, 0f, 1f, 0f,
             0.5f, -0.5f, 0f, 1f, 0f,
             0.5f,  0.5f, 0f, 1f, 0f,
        )
    }
}