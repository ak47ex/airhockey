package com.suenara.opengl

import com.suenara.opengl.geometry.Point
import com.suenara.opengl.geometry.Rectangle

interface GameState {
    val puckPosition: Point
    val puckRadius: Float
    val redMalletPosition: Point
    val blueMalletPosition: Point
    val malletRadius: Float
    val tableBounds: Rectangle
}