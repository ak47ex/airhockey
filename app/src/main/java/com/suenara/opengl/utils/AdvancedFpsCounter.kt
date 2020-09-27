package com.suenara.opengl.utils

class AdvancedFpsCounter(private val averageFrameCount: Int = 100) : FrameDrawListener {

    private var lastTime: Long = 0L
    private var frameCount: Int = 0
    private var passedTime: Long = 0L
    private var fpsSum: Double = .0
    private var aggFps: Double = .0

    var averageFps: Double = .0
        private set
    var actualFps: Double = .0
        private set
    var deltaNanos: Long = 0
        private set

    override fun onFrameDraw() = onFrameDraw(System.nanoTime() - lastTime)

    fun onFrameDraw(deltaNanos: Long) {
        frameCount++
        passedTime += deltaNanos
        this.deltaNanos = deltaNanos

        lastTime = System.nanoTime()

        actualFps = 1e9f / deltaNanos.toDouble()
        fpsSum += actualFps

        if (frameCount >= averageFrameCount) {
            aggFps = frameCount / (passedTime.toDouble() / 1e9f)
            averageFps = fpsSum / frameCount.toDouble()

            frameCount = 0
            passedTime = 0
            fpsSum = .0
        }
    }
}