package com.suenara.opengl

import android.graphics.Bitmap
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.suenara.opengl.`object`.Mallet
import com.suenara.opengl.`object`.Table
import com.suenara.opengl.program.ColorShaderProgram
import com.suenara.opengl.program.TextureShaderProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(private val textureProvider: () -> Bitmap) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private lateinit var table: Table
    private lateinit var mallet: Mallet

    private lateinit var shaderProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)

        table = Table()
        mallet = Mallet()

        shaderProgram = TextureShaderProgram()
        colorProgram = ColorShaderProgram()
        texture = TextureHelper.loadTexture(textureProvider())
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        Matrix.perspectiveM(projectionMatrix, 0, FOV_Y, width/height.toFloat(), Z_NEAR, Z_FAR)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f)
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        projectionMatrix.multiplyMM(modelMatrix).copyInto(projectionMatrix)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        shaderProgram.useProgram()
        shaderProgram.setUniforms(projectionMatrix, texture)
        table.bindData(shaderProgram)
        table.draw()

        colorProgram.useProgram()
        colorProgram.setUniforms(projectionMatrix)
        mallet.bindData(colorProgram)
        mallet.draw()
    }

    companion object {
        private const val FOV_Y = 45f
        private const val Z_NEAR = 1f
        private const val Z_FAR = 10f
    }
}