package com.suenara.opengl

import com.suenara.opengl.geometry.Point
import com.suenara.opengl.geometry.Vector
import com.suenara.opengl.geometry.Vector.Companion.angleWith
import com.suenara.opengl.geometry.Vector.Companion.rotateY
import com.suenara.opengl.geometry.Vector.Companion.vectorTo
import com.suenara.opengl.utils.TimeUtils
import com.suenara.opengl.utils.currentTimeMillis
import com.suenara.opengl.utils.debug
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

class AirHockeyGame {

    val state: GameState
        get() = internalState

    private var internalState = GameStateImpl()

    private var malletMoveHitProcessor = MalletMoveHitProcessor(0f, 0f)

    fun loadState(gameState: GameState) {
        internalState.consume(gameState)
        malletMoveHitProcessor = MalletMoveHitProcessor(gameState.malletRadius, gameState.puckRadius)
    }

    fun updateState() = with(internalState) {
        if (timestamp > 0) {
            var delta = currentTimeMillis() - timestamp
            timestamp = currentTimeMillis()
            while (delta > 0) {
                tick(min(delta, MIN_FRAME_DELTA_MILLIS))
                delta -= MIN_FRAME_DELTA_MILLIS
            }
        } else {
            timestamp = currentTimeMillis()
        }
    }

    fun updateRedMalletPosition(point: Point) = with(internalState) {
        redMalletPosition = updateMalletPosition(point, redMalletPosition)
    }

    fun updateBlueMalletPosition(point: Point) = with(internalState) {
        blueMalletPosition = updateMalletPosition(point, blueMalletPosition)
    }

    private fun tick(deltaMillis: Long) = with(internalState) {
        if (deltaMillis <= 0) return
        val dt = TimeUtils.millisToSeconds(deltaMillis).toFloat()

        val acceleration = puckVelocity.normalize().scale(PUCK_ACCELERATION * dt)
        puckVelocity = (puckVelocity + acceleration).run {
            copy(
                x = if (x.sign != puckVelocity.x.sign) 0f else x,
                y = if (y.sign != puckVelocity.y.sign) 0f else y,
                z = if (z.sign != puckVelocity.z.sign) 0f else z
            )
        }
        puckPosition = puckPosition.translate(puckVelocity.scale(dt))
        if (isPuckHitByMallet(blueMalletPosition)) {
            val malletToPuck = blueMalletPosition.vectorTo(puckPosition).copy(y = 0f)
            val distance = malletToPuck.length()
            val withoutRadiuses = abs(distance - state.malletRadius - puckRadius)
            val offset = malletToPuck.let { it.normalize().scale(withoutRadiuses) }
            puckPosition = puckPosition.translate(offset)
            val cross = malletToPuck.crossProduct(puckVelocity)
            val angleRad = malletToPuck.angleWith(puckVelocity)
            val degrees = Math.toDegrees(angleRad.toDouble()).toFloat() / 2f

            puckVelocity = puckVelocity.rotateY(if (cross.y < 0 && cross.x >= 0) -degrees else degrees)
            "puck hit blue mallet distance: ${withoutRadiuses} ".debug
            //TODO: update position and velocity vector
        }
        if (isPuckHitByMallet(redMalletPosition)) {
            val malletToPuck = redMalletPosition.vectorTo(puckPosition)
            val distance = malletToPuck.copy(y = 0f).length()
            val withoutRadiuses = abs(distance - state.malletRadius - puckRadius)
            val offset = malletToPuck.let { it.normalize().scale(withoutRadiuses).copy(y = 0f) }
            puckPosition = puckPosition.translate(offset)
            "puck hit red mallet distance: ${withoutRadiuses}".debug
            //TODO: update position and velocity vector
        }

        clampPuckWithinTable()
    }

    private fun clampPuckWithinTable() = with(internalState) {
        if (puckPosition.x - puckRadius <= tableBounds.left || puckPosition.x + puckRadius >= tableBounds.right) {
            puckPosition = if (puckPosition.x - puckRadius <= tableBounds.left) {
                puckPosition.copy(x = tableBounds.left + puckRadius)
            } else {
                puckPosition.copy(x = tableBounds.right - puckRadius)
            }
            puckVelocity = puckVelocity.copy(x = -puckVelocity.x)
        }

        if (puckPosition.z - puckRadius <= tableBounds.top || puckPosition.z + puckRadius >= tableBounds.bottom) {
            puckPosition = if (puckPosition.z - puckRadius <= tableBounds.top) {
                puckPosition.copy(z = tableBounds.top + puckRadius)
            } else {
                puckPosition.copy(z = tableBounds.bottom - puckRadius)
            }
            puckVelocity = puckVelocity.copy(z = -puckVelocity.z)
        }
    }

    private fun updateMalletPosition(point: Point, currentMalletPosition: Point) = internalState.run {
        Point(
            point.x.coerceIn(tableBounds.left + malletRadius, tableBounds.right - malletRadius),
            point.y,
            point.z.coerceIn(0f + malletRadius, tableBounds.bottom - malletRadius)
        ).also { newMalletPosition ->
            if (isPuckHitByMallet(newMalletPosition)) {
                malletMoveHitProcessor.process(currentMalletPosition, newMalletPosition, puckPosition)
                    .takeIf { it != Vector.ZERO }?.let {
                        puckVelocity = it.scale(PUCK_HIT_MULTIPLIER, 0f, PUCK_HIT_MULTIPLIER)
                    }
                //TODO: updatePuckPositionAfterHit
            }
        }
    }

    private fun isPuckHitByMallet(malletPosition: Point): Boolean = with(internalState) {
        return (malletPosition vectorTo puckPosition).copy(y = 0f).lengthSquared().let { distance ->
            distance <= (puckRadius + malletRadius) * (puckRadius + malletRadius)
        }
    }

    companion object {
        private const val PUCK_ACCELERATION = -3f
        private const val PUCK_HIT_MULTIPLIER = 100f
        private const val MIN_FRAME_DELTA_MILLIS = 16L
    }
}