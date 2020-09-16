package com.suenara.opengl

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    var rendererSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkGlVersion(this)

        prepareSurface(gl_surface)

    }

    private fun prepareSurface(surface: GLSurfaceView) {
        surface.setEGLContextClientVersion(GL_VERSION)
        surface.setRenderer(MyRenderer(readTextFile(R.raw.simple_vertex_shader).debug, readTextFile(R.raw.simple_fragment_shader).debug))
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