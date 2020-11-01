package com.suenara.opengl

import com.suenara.opengl.geometry.Point
import com.suenara.opengl.geometry.Rectangle
import com.suenara.opengl.geometry.Vector

interface GameState {
    val puckPosition: Point
    val puckRadius: Float
    val puckVelocity: Vector
    val redMalletPosition: Point
    val blueMalletPosition: Point
    val malletRadius: Float
    val tableBounds: Rectangle
}