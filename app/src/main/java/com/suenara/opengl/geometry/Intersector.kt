package com.suenara.opengl.geometry

import com.suenara.opengl.geometry.Vector.Companion.vectorTo

object Intersector {
    infix fun Ray.intersectionPointWith(plane: Plane): Point {
        val rayToPlaneVector = point vectorTo plane.point
        val scaleFactor = (rayToPlaneVector dotProduct plane.normal) / (vector dotProduct plane.normal)
        return point.translate(vector.scale(scaleFactor))
    }

    infix fun Sphere.intersect(ray: Ray): Boolean = (center distanceTo ray) < radius

    infix fun Point.distanceTo(ray: Ray): Float {
        val p1ToPoint = ray.point vectorTo this
        val p2ToPoint = ray.point.translate(ray.vector) vectorTo this

        val areaOfTriangleTimesTwo = (p1ToPoint crossProduct p2ToPoint).length()
        val lengthOfBase = ray.vector.length()

        return areaOfTriangleTimesTwo / lengthOfBase
    }

    infix fun Rectangle.contains(point: Point): Boolean = point.x in left..right && point.y in top..bottom
}