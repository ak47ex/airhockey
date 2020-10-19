package com.suenara.opengl

import com.suenara.opengl.geometry.Point
import com.suenara.opengl.geometry.Rectangle

interface GameState {
    val puckPosition: Point
    val blueMalletPosition: Point
    val tableBounds: Rectangle
}