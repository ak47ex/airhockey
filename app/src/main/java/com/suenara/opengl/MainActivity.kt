package com.suenara.opengl

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.suenara.opengl.utils.AdvancedFpsCounter
import com.suenara.opengl.utils.SimpleFpsCounter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var rendererSet = false

    private val fpsCounter = AdvancedFpsCounter(15)

    private val fpsCallback = Runnable {
        "%.0f".format(fpsCounter.averageFps).let { fps.text = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkGlVersion(this)

        prepareSurface(gl_surface)
        gl_surface.renderMode
        reset_button.setOnClickListener {
            (gl_surface.parent as ViewGroup).run {
                removeView(gl_surface)
                addView(gl_surface, 0)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun prepareSurface(surface: GLSurfaceView) = surface.run {
        setEGLContextClientVersion(GL_VERSION)
        val renderer =
            MyRenderer { requireNotNull(loadBitmap(R.drawable.air_hockey_surface)) { "Failed to load bitmap drawable" } }
        renderer.frameRenderListener = {
            fpsCounter.onFrameDraw()
            removeCallbacks(fpsCallback)
            post(fpsCallback)
        }
        setRenderer(renderer)
        setOnTouchListener { _, event ->
            val normalizedX = (event.x / width) * 2 - 1
            val normalizedY = -((event.y / height) * 2 - 1)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    queueEvent { renderer.handleTouchPress(normalizedX, normalizedY) }
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    queueEvent { renderer.handleTouchDrag(normalizedX, normalizedY) }
                    true
                }
                else -> false
            }
        }
        rendererSet = true
    }

    override fun onPause() {
        super.onPause()
        gl_surface.takeIf { rendererSet }?.onPause()
    }

    override fun onResume() {
        super.onResume()
        gl_surface.takeIf { rendererSet }?.onResume()
    }

    override fun onDestroy() {
        rendererSet = false
        clearFindViewByIdCache()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "d34db33f"

        private const val GL_VERSION = 2

        private fun checkGlVersion(context: Context) {
            context.getSystemService<ActivityManager>()?.deviceConfigurationInfo?.reqGlEsVersion?.let {
                val major: Int = it and -0x10000 shr 16
                val minor: Int = it and 0x0000ffff
                Log.d(TAG, "OpenGL version is $major.$minor")
            } ?: Log.e(TAG, "Can't get OpenGL version!")
        }
    }
}