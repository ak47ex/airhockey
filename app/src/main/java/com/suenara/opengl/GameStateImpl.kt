package com.suenara.opengl

import com.suenara.opengl.geometry.Point
import com.suenara.opengl.geometry.Rectangle
import com.suenara.opengl.geometry.Vector

data class GameStateImpl(
    override var puckPosition: Point = Point(),
    override var puckRadius: Float = 0f,
    override var malletRadius: Float = 0f,
    var puckVelocity: Vector = Vector(),
    override var redMalletPosition: Point = Point(),
    override var blueMalletPosition: Point = Point(),
    override var tableBounds: Rectangle = Rectangle(0f, 0f, 0f, 0f),
    var timestamp: Long = 0L
) : GameState {
    fun consume(other: GameState): GameStateImpl = apply {
        puckPosition = other.puckPosition
        puckRadius = other.puckRadius
        redMalletPosition = other.redMalletPosition
        blueMalletPosition = other.blueMalletPosition
        malletRadius = other.malletRadius
        tableBounds = other.tableBounds

        timestamp = 0L
        puckVelocity = Vector()
    }
}