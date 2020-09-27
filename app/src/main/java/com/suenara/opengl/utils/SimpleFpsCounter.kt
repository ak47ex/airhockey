package com.suenara.opengl.utils

class SimpleFpsCounter : FrameDrawListener {
    private var lastTime = 0L

    private var framesDrawn = 0
    var fps: Float = 0f
        private set

    override fun onFrameDraw() {
        framesDrawn++
        val time = System.nanoTime()
        val elapsed = time - lastTime
        if (elapsed > SECOND_TO_US) {
            fps = ((framesDrawn.toLong() / elapsed.toDouble()) * SECOND_TO_US).toFloat()
            lastTime = time
            framesDrawn = 0
        }
    }

    fun reset() {
        lastTime = 0L
        framesDrawn = 0
        fps = 0f
    }

    companion object {
        private const val SECOND_TO_US = 1_000_000_000L
    }
}