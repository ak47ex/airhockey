package com.suenara.opengl

import com.suenara.opengl.geometry.Point
import com.suenara.opengl.geometry.Rectangle
import com.suenara.opengl.geometry.Vector

data class GameStateImpl(
    override var puckPosition: Point = Point(),
    var puckVelocity: Vector = Vector(),
    override var blueMalletPosition: Point = Point(),
    override val tableBounds: Rectangle = Rectangle(0f, 0f, 0f, 0f),
    var timestamp: Long = 0L
) : GameState