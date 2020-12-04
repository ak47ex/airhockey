package com.suenara.opengl

import com.suenara.opengl.geometry.Point
import com.suenara.opengl.geometry.Vector
import com.suenara.opengl.geometry.Vector.Companion.vectorTo
import kotlin.math.pow
import kotlin.math.sqrt

class MalletMoveHitProcessor(private val malletRadius: Float, private val puckRadius: Float) {
    //FIXME: it gives approximate, but not correct value
    fun process(currentMalletPosition: Point, newMalletPosition: Point, puckPosition: Point): Vector {
        val moveVector = currentMalletPosition vectorTo newMalletPosition

        val A = currentMalletPosition
        val B = newMalletPosition
        val C = puckPosition
        val CA = C.vectorTo(A)
        val CB = C.vectorTo(B)
        val AB = A.vectorTo(B)
        val AC = A.vectorTo(C)
        val proj = AC.dotProduct(AB) / AB.length()
        val E = A.translate(AB.unit().scale(proj))
        val CE = C.vectorTo(E)
        return if (CE.length() <= malletRadius + puckRadius) {
            //hit happened
            val DE_length = sqrt(CE.lengthSquared() + (malletRadius + puckRadius).pow(2))
            val D = A.translate(AB.unit().scale(proj - DE_length))
            val DC = D.vectorTo(C)
            val ratio = CA.length() / CE.length()
            val length_BD = A.vectorTo(B).length() / ratio
            val backmove = moveVector.normalize().scale(-1f * length_BD)
            val hitPoint = B.translate(backmove)

            (hitPoint vectorTo puckPosition).normalize()
                .scale(moveVector.length())
                .copy(y = 0f)
        } else {
            return Vector.ZERO
        }
    }
}