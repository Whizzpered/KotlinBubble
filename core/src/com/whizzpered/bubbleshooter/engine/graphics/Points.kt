package com.whizzpered.bubbleshooter.engine.graphics

data class Point(val x: Float, val y: Float, val z: Float = 0f) {

    fun toMutablePoint(): MutablePoint {
        return MutablePoint(x, y, z)
    }

    operator fun plus(p: MutablePoint): Point {
        return Point(x + p.x, y + p.y, z + p.z)
    }

    operator fun plus(p: Point): Point {
        return Point(x + p.x, y + p.y, z + p.z)
    }

    operator fun minus(p: MutablePoint): Point {
        return Point(x - p.x, y - p.y, z - p.z)
    }

    operator fun minus(p: Point): Point {
        return Point(x - p.x, y - p.y, z - p.z)
    }

    operator fun times(p: Point): Point {
        return Point(x * p.x, y * p.y, z * p.z)
    }

    operator fun times(p: Float): Point {
        return Point(x * p, y * p, z * p)
    }

    fun but(x: Float = this.x, y: Float = this.y, z: Float = this.z) = Point(x, y, z)

    fun butMutable(x: Float = this.x, y: Float = this.y, z: Float = this.z) = MutablePoint(x, y, z)
}

data class MutablePoint(var x: Float, var y: Float, var z: Float = 0f) {
    fun toPoint(): Point {
        return Point(x, y, z)
    }

    operator fun plus(p: MutablePoint): Point {
        return Point(x + p.x, y + p.y, z + p.z)
    }

    operator fun plus(p: Point): Point {
        return Point(x + p.x, y + p.y, z + p.z)
    }

    operator fun minus(p: MutablePoint): Point {
        return Point(x - p.x, y - p.y, z - p.z)
    }

    operator fun minus(p: Point): Point {
        return Point(x - p.x, y - p.y, z + p.z)
    }

    operator fun plusAssign(p: MutablePoint) {
        x += p.x
        y += p.y
        z += p.z
    }

    operator fun plusAssign(p: Point) {
        x += p.x
        y += p.y
        z += p.z
    }

    operator fun minusAssign(p: MutablePoint) {
        x -= p.x
        y -= p.y
        z -= p.z
    }

    operator fun minusAssign(p: Point) {
        x -= p.x
        y -= p.y
        z -= p.z
    }

    fun set(p: MutablePoint) {
        x = p.x
        y = p.y
        z = p.z
    }

    fun set(p: Point) {
        x = p.x
        y = p.y
        z = p.z
    }

    fun set(x: Float, y: Float, z: Float = this.z) {
        this.x = x
        this.y = y
        this.z = z
    }

    operator fun times(p: Point): Point {
        return Point(x * p.x, y * p.y, z * p.z)
    }

    operator fun times(p: Float): Point {
        return Point(x * p, y * p, z * p)
    }

    fun but(x: Float = this.x, y: Float = this.y, z: Float = this.z) = MutablePoint(x, y, z)

    fun butImmutable(x: Float = this.x, y: Float = this.y, z: Float = this.z) = Point(x, y, z)
}