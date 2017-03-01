package com.whizzpered.bubbleshooter.engine.graphics

data class Point(val x: Float, val y: Float) {

    fun toMutablePoint(): MutablePoint {
        return MutablePoint(x, y)
    }

    operator fun plus(p: MutablePoint): Point {
        return Point(x + p.x, y + p.y)
    }

    operator fun plus(p: Point): Point {
        return Point(x + p.x, y + p.y)
    }

    operator fun minus(p: MutablePoint): Point {
        return Point(x - p.x, y - p.y)
    }

    operator fun minus(p: Point): Point {
        return Point(x - p.x, y - p.y)
    }

    operator fun times(s: Float): Point {
        return Point(x * s, y * s)
    }
}

data class MutablePoint(var x: Float, var y: Float) {
    fun toPoint(): Point {
        return Point(x, y)
    }

    operator fun plus(p: MutablePoint): Point {
        return Point(x + p.x, y + p.y)
    }

    operator fun plus(p: Point): Point {
        return Point(x + p.x, y + p.y)
    }

    operator fun minus(p: MutablePoint): Point {
        return Point(x - p.x, y - p.y)
    }

    operator fun minus(p: Point): Point {
        return Point(x - p.x, y - p.y)
    }

    operator fun times(s: Float): Point {
        return Point(x * s, y * s)
    }

    operator fun plusAssign(p: MutablePoint) {
        x += p.x
        y += p.y
    }

    operator fun plusAssign(p: Point) {
        x += p.x
        y += p.y
    }

    operator fun minusAssign(p: MutablePoint) {
        x -= p.x
        y -= p.y
    }

    operator fun minusAssign(p: Point) {
        x -= p.x
        y -= p.y
    }

    fun set(p: MutablePoint) {
        x = p.x
        y = p.y
    }

    fun set(p: Point) {
        x = p.x
        y = p.y
    }

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}

data class Point3D(val x: Float, val y: Float, val z: Float) {

    fun toMutablePoint3D(): MutablePoint3D {
        return MutablePoint3D(x, y, z)
    }

    operator fun plus(p: MutablePoint3D): Point3D {
        return Point3D(x + p.x, y + p.y, z + p.z)
    }

    operator fun plus(p: Point3D): Point3D {
        return Point3D(x + p.x, y + p.y, z + p.z)
    }

    operator fun minus(p: MutablePoint3D): Point3D {
        return Point3D(x - p.x, y - p.y, z - p.z)
    }

    operator fun minus(p: Point3D): Point3D {
        return Point3D(x - p.x, y - p.y, z - p.z)
    }
}

data class MutablePoint3D(var x: Float, var y: Float, var z: Float) {
    fun toPoint3D(): Point3D {
        return Point3D(x, y, z)
    }

    operator fun plus(p: MutablePoint3D): Point3D {
        return Point3D(x + p.x, y + p.y, z + p.z)
    }

    operator fun plus(p: Point3D): Point3D {
        return Point3D(x + p.x, y + p.y, z + p.z)
    }

    operator fun minus(p: MutablePoint3D): Point3D {
        return Point3D(x - p.x, y - p.y, z - p.z)
    }

    operator fun minus(p: Point3D): Point3D {
        return Point3D(x - p.x, y - p.y, z + p.z)
    }

    operator fun plusAssign(p: MutablePoint3D) {
        x += p.x
        y += p.y
        z += p.z
    }

    operator fun plusAssign(p: Point3D) {
        x += p.x
        y += p.y
        z += p.z
    }

    operator fun minusAssign(p: MutablePoint3D) {
        x -= p.x
        y -= p.y
        z -= p.z
    }

    operator fun minusAssign(p: Point3D) {
        x -= p.x
        y -= p.y
        z -= p.z
    }

    fun set(p: MutablePoint3D) {
        x = p.x
        y = p.y
        z = p.z
    }

    fun set(p: Point3D) {
        x = p.x
        y = p.y
        z = p.z
    }

    fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }
}