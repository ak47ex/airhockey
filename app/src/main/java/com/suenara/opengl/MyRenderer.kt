package com.suenara.opengl

import android.graphics.Bitmap
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLES20.glViewport
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.suenara.opengl.`object`.Arrow
import com.suenara.opengl.`object`.Mallet
import com.suenara.opengl.`object`.Puck
import com.suenara.opengl.`object`.Table
import com.suenara.opengl.geometry.Intersector.intersect
import com.suenara.opengl.geometry.Intersector.intersectionPointWith
import com.suenara.opengl.geometry.Plane
import com.suenara.opengl.geometry.Point
import com.suenara.opengl.geometry.Ray
import com.suenara.opengl.geometry.Rectangle
import com.suenara.opengl.geometry.Sphere
import com.suenara.opengl.geometry.Vector
import com.suenara.opengl.geometry.Vector.Companion.angleWith
import com.suenara.opengl.geometry.Vector.Companion.rotateY
import com.suenara.opengl.geometry.Vector.Companion.vectorTo
import com.suenara.opengl.program.ColorShaderProgram
import com.suenara.opengl.program.TextureShaderProgram
import com.suenara.opengl.utils.FpsLimiter
import com.suenara.opengl.utils.TextureHelper
import com.suenara.opengl.utils.debug
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sign

class MyRenderer(private val textureProvider: () -> Bitmap) : GLSurfaceView.Renderer {

    var frameRenderListener: (() -> Unit)? = null

    private val fpsLimiter = FpsLimiter(60)

    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val invertedViewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck

    private lateinit var puckArrow: Arrow

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture: Int = 0

    private var isMalletPressed = false

    private val game = AirHockeyGame()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)
        table = Table()
        mallet = Mallet(MALLET_RADIUS, MALLET_HEIGHT, CIRCLE_DETALIZATION)
        isMalletPressed = false

        puck = Puck(PUCK_RADIUS, PUCK_HEIGHT, CIRCLE_DETALIZATION)
        puckArrow = Arrow()

        textureProgram = TextureShaderProgram()
        colorProgram = ColorShaderProgram()
        texture = TextureHelper.loadTexture(textureProvider())

        initializeGame()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        Matrix.perspectiveM(projectionMatrix, 0, FOV_Y, width / height.toFloat(), Z_NEAR, Z_FAR)

        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 3f, 1f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        projectionMatrix.multiplyMM(viewMatrix).copyInto(viewProjectionMatrix)
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        game.updateState()
        drawState(game.state)
        frameRenderListener?.invoke()
        fpsLimiter.onFrameDraw()
    }

    fun handleTouchPress(normalizedX: Float, normalizedY: Float) {
        val ray = pointToRay(normalizedX, normalizedY)
        val malletBoundingSphere = Sphere(game.state.blueMalletPosition, mallet.height / 2f)

        isMalletPressed = malletBoundingSphere intersect ray
    }

    fun handleTouchDrag(normalizedX: Float, normalizedY: Float) {
        if (!isMalletPressed) return

        val ray = pointToRay(normalizedX, normalizedY)
        val plane = Plane(Point(0f, 0f, 0f), Vector(0f, 1f, 0f))

        val touchedPoint = ray intersectionPointWith plane
        game.updateBlueMalletPosition(touchedPoint.copy(y = mallet.height / 2f))
    }

    fun initializeGame() {
        game.loadState(
            GameStateImpl(
                puckPosition = Point(0f, puck.height / 2f, 0.2f),
                puckVelocity = Vector(0.0f, 0f, 0f),
                puckRadius = PUCK_RADIUS,
                malletRadius = MALLET_RADIUS,
                redMalletPosition = Point(0f, mallet.height / 2f, -0.4f),
                blueMalletPosition = Point(0f, mallet.height / 2f, 0.4f),
                tableBounds = Rectangle(-0.5f, -0.8f, 0.5f, 0.8f),
                timestamp = 0L
            )
        )
    }

    private fun drawState(gameState: GameState) = with(gameState) {
        glClear(GL_COLOR_BUFFER_BIT)
        setupMVP()

        textureProgram.useProgram()
        textureProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        positionObjectInScene(redMalletPosition.x, redMalletPosition.y, redMalletPosition.z)
        colorProgram.useProgram()
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet.bindData(colorProgram)
        mallet.draw()
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.draw()

        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorProgram)
        puck.draw()

        drawDebugPuckArrow(puckPosition, puckVelocity)
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) = transformObject { mtrx ->
        Matrix.translateM(mtrx, 0, x, y, z)
    }

    private fun setupMVP() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        viewProjectionMatrix.multiplyMM(modelMatrix).copyInto(modelViewProjectionMatrix)
    }

    private fun pointToRay(normalizedX: Float, normalizedY: Float): Ray {
        val nearPoint = floatArrayOf(normalizedX, normalizedY, -1f, 1f)
        val farPoint = floatArrayOf(normalizedX, normalizedY, 1f, 1f)

        val nearPointWorld = FloatArray(4)
        val farPointWorld = FloatArray(4)

        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPoint, 0)
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPoint, 0)

        nearPointWorld.divideByW()
        farPointWorld.divideByW()

        val nearPointRay = nearPointWorld.toPoint()
        val farPointRay = farPointWorld.toPoint()
        return Ray(nearPointRay, nearPointRay vectorTo farPointRay)
    }

    private inline fun transformObject(crossinline block: (modelMatrix: FloatArray) -> Unit) {
        Matrix.setIdentityM(modelMatrix, 0)
        block(modelMatrix)
        viewProjectionMatrix.multiplyMM(modelMatrix).copyInto(modelViewProjectionMatrix)
    }

    private fun drawDebugPuckArrow(puckPosition: Point, puckVelocity: Vector) {
        transformObject {  mtrx ->
            Matrix.translateM(mtrx, 0, puckPosition.x, puckPosition.y, puckPosition.z)

            val arrowVector = Vector(0f, puckVelocity.y, 1f)
            val radians = puckVelocity.angleWith(arrowVector)
            val degrees = Math.toDegrees(radians.toDouble()).toFloat()
            val cross = arrowVector.crossProduct(puckVelocity)
            Matrix.rotateM(mtrx, 0, if (cross.y < 0 && cross.x >= 0) -degrees else degrees, 0f, 1f, 0f)

            val velocity = if (puckVelocity.length() > 0f) 0.25f else 0f
            Matrix.scaleM(mtrx, 0, velocity, 1f, velocity)
        }
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 1f, 0f)
        puckArrow.bindData(colorProgram)
        puckArrow.draw()
    }

    companion object {
        private const val FOV_Y = 45f
        private const val Z_NEAR = 1f
        private const val Z_FAR = 10f

        private const val CIRCLE_DETALIZATION = 32 //greater = smoother

        private const val MALLET_RADIUS = 0.08f
        private const val MALLET_HEIGHT = 0.15f

        private const val PUCK_RADIUS = 0.06f
        private const val PUCK_HEIGHT = 0.02f

        infix fun FloatArray.multiplyMM(other: FloatArray): FloatArray {
            require(size == other.size)
            val temp = FloatArray(size)
            Matrix.multiplyMM(temp, 0, this, 0, other, 0)
            return temp
        }

        infix fun FloatArray.divide(divider: Float) = forEachIndexed { index, value -> set(index, value / divider) }

        fun FloatArray.divideByW(): FloatArray = apply { require(size >= 4); divide(get(3)) }

        fun FloatArray.toPoint(): Point = Point(getOrElse(0) { 0f }, getOrElse(1) { 0f }, getOrElse(2) { 0f })
    }
}