package com.suenara.opengl.utils

import kotlin.math.max

/**
 * adaptation of LWJGL Sync class (https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/opengl/Sync.java)
 */
class FpsLimiter(private val fpsLimit: Int = 60) : FrameDrawListener {

    private val time: Long
        get() = System.nanoTime()

    private var nextFrame: Long = time
    private val sleepDurations = RunningAvg(10).apply { init(1000 * 1000) }
    private val yieldDurations = RunningAvg(10).apply { init((-(time - time) * 1.333).toLong()) }

    override fun onFrameDraw() {
        try {
            // sleep until the average sleep time is greater than the time remaining till nextFrame
            var t0 = time;
            var t1: Long
            while (nextFrame - t0 > sleepDurations.avg()) {
                Thread.sleep(1);
                t1 = time
                sleepDurations.add(t1 - t0) // update average sleep time
                t0 = t1
            }

            // slowly dampen sleep average if too high to avoid yielding too much
            sleepDurations.dampenForLowResTicker();

            // yield until the average yield time is greater than the time remaining till nextFrame
            t0 = time
            while (nextFrame - t0 > yieldDurations.avg()) {
                Thread.yield()
                t1 = time
                yieldDurations.add(t1 - t0) // update average yield time
                t0 = t1
            }
        } catch (ignore: InterruptedException) { }

        // schedule next frame, drop frame(s) if already too late for next frame
        nextFrame = max(nextFrame + NANOS_IN_SECOND / fpsLimit, time)
    }

    private class RunningAvg(slotCount: Int) {
        private val slots: LongArray = LongArray(slotCount)
        private var offset: Int = 0

        fun init(value: Long) {
            while (offset < slots.size) {
                slots[offset++] = value
            }
        }

        fun add(value: Long) {
            slots[offset++ % slots.size] = value
            offset %= slots.size
        }

        fun avg(): Long = slots.sum() / slots.size

        fun dampenForLowResTicker() {
            if (avg() > DAMPEN_THRESHOLD) {
                for (i in slots.indices) {
                    slots[i] *= DAMPEN_FACTOR.toLong()
                }
            }
        }

        companion object {
            private const val DAMPEN_THRESHOLD = 10 * 1000L * 1000L // 10ms
            private const val DAMPEN_FACTOR = 0.9f // don't change: 0.9f is exactly right!
        }

    }

    companion object {
        private const val NANOS_IN_SECOND = 1_000_000_000L
    }
}