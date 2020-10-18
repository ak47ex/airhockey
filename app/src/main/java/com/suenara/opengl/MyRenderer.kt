package com.suenara.opengl

import android.graphics.Bitmap
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLES20.glViewport
import android.opengl.GLSurfaceView
import android.opengl.Matrix
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
import com.suenara.opengl.geometry.Vector.Companion.vectorTo
import com.suenara.opengl.program.ColorShaderProgram
import com.suenara.opengl.program.TextureShaderProgram
import com.suenara.opengl.utils.FpsLimiter
import com.suenara.opengl.utils.TimeUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.min
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

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture: Int = 0

    private var isMalletPressed = false
    private var blueMalletPosition: Point = Point()
    private var prevBlueMalletPosition: Point = Point()

    private var puckPosition: Point = Point()
    private var puckVelocity: Vector = Vector()

    private val tableBounds = Rectangle(-0.5f, -0.8f, 0.5f, 0.8f)

    private var lastTime: Long = 0L

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)
        table = Table()
        mallet = Mallet(MALLET_RADIUS, MALLET_HEIGHT, CIRCLE_DETALIZATION).apply {
            blueMalletPosition = Point(0f, height / 2f, 0.4f)
            prevBlueMalletPosition = blueMalletPosition
            isMalletPressed = false
        }
        puck = Puck(PUCK_RADIUS, PUCK_HEIGHT, CIRCLE_DETALIZATION).apply {
            puckPosition = Point(0f, height / 2f, 0f)
            puckVelocity = Vector(0.0f, 0f, 0f)
        }

        textureProgram = TextureShaderProgram()
        colorProgram = ColorShaderProgram()
        texture = TextureHelper.loadTexture(textureProvider())

        lastTime = 0L
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        Matrix.perspectiveM(projectionMatrix, 0, FOV_Y, width / height.toFloat(), Z_NEAR, Z_FAR)

        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 1.2f, 2.2f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        projectionMatrix.multiplyMM(viewMatrix).copyInto(viewProjectionMatrix)
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        updateState()
        drawState()
        frameRenderListener?.invoke()
        fpsLimiter.onFrameDraw()
    }

    fun handleTouchPress(normalizedX: Float, normalizedY: Float) {
        val ray = pointToRay(normalizedX, normalizedY)

        val malletBoundingSphere = Sphere(blueMalletPosition.copy(), mallet.height / 2f)

        isMalletPressed = malletBoundingSphere intersect ray
    }

    fun handleTouchDrag(normalizedX: Float, normalizedY: Float) {
        if (!isMalletPressed) return

        val ray = pointToRay(normalizedX, normalizedY)
        val plane = Plane(Point(0f, 0f, 0f), Vector(0f, 1f, 0f))

        val touchedPoint = ray intersectionPointWith plane
        prevBlueMalletPosition = blueMalletPosition
        blueMalletPosition = touchedPoint.copy(y = mallet.height / 2f)

        if (isPuckHitByMallet()) {
            puckVelocity = prevBlueMalletPosition vectorTo blueMalletPosition
        }
    }

    private fun updateState() {
        if (lastTime > 0) {
            var delta = System.currentTimeMillis() - lastTime
            lastTime = System.currentTimeMillis()
            while (delta > 0) {
                tick(min(delta, MIN_FRAME_DELTA_MILLIS))
                delta -= MIN_FRAME_DELTA_MILLIS
            }
        } else {
            lastTime = System.currentTimeMillis()
        }
    }

    private fun tick(deltaMillis: Long) {
        if (deltaMillis <= 0) return
        val dt = TimeUtils.millisToSeconds(deltaMillis).toFloat()

        val acceleration = puckVelocity.unit().scale(PUCK_ACCELERATION * dt)
        puckVelocity = (puckVelocity + acceleration).run {
            copy(
                x = if (x.sign != puckVelocity.x.sign) 0f else x,
                y = if (y.sign != puckVelocity.y.sign) 0f else y,
                z = if (z.sign != puckVelocity.z.sign) 0f else z
            )
        }
        puckPosition = puckPosition.translate(puckVelocity.scale(dt))
        if (puckPosition.x - puck.radius <= tableBounds.left || puckPosition.x + puck.radius >= tableBounds.right) {
            puckPosition = if (puckPosition.x - puck.radius <= tableBounds.left) {
                puckPosition.copy(x = tableBounds.left + puck.radius)
            } else {
                puckPosition.copy(x = tableBounds.right - puck.radius)
            }
            puckVelocity = puckVelocity.copy(x = -puckVelocity.x)
        }
        if (puckPosition.z - puck.radius <= tableBounds.top || puckPosition.z + puck.radius >= tableBounds.bottom) {
            puckPosition = if (puckPosition.z - puck.radius <= tableBounds.top) {
                puckPosition.copy(z = tableBounds.top + puck.radius)
            } else {
                puckPosition.copy(z = tableBounds.bottom - puck.radius)
            }
            puckVelocity = puckVelocity.copy(z = -puckVelocity.z)
        }
    }

    private fun drawState() {
        glClear(GL_COLOR_BUFFER_BIT)
        positionTableInScene()

        textureProgram.useProgram()
        textureProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        positionObjectInScene(0f, mallet.height / 2f, -0.4f)
        colorProgram.useProgram()
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet.bindData(colorProgram)
        mallet.draw()

        positionObjectInScene(
            blueMalletPosition.x.coerceIn(tableBounds.left + mallet.radius, tableBounds.right - mallet.radius),
            blueMalletPosition.y,
            blueMalletPosition.z.coerceIn(0f + mallet.radius, tableBounds.bottom - mallet.radius)
        )
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.draw()

        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorProgram)
        puck.draw()
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        viewProjectionMatrix.multiplyMM(modelMatrix).copyInto(modelViewProjectionMatrix)
    }

    private fun positionTableInScene() {
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

    private fun isPuckHitByMallet(): Boolean {
        return (blueMalletPosition vectorTo puckPosition).length().let { distance ->
            distance < (puck.radius + mallet.radius)
        }
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

        private const val PUCK_ACCELERATION = -3f

        private val SCREEN_FRAME_RATE_HZ = 60
        private val MIN_FRAME_DELTA_MILLIS = ((1f / SCREEN_FRAME_RATE_HZ) * TimeUtils.MILLISECONDS_IN_SECOND).toLong()
    }
}