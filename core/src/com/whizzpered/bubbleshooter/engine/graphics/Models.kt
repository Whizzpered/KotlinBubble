package com.whizzpered.bubbleshooter.engine.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.whizzpered.bubbleshooter.engine.handler.Main
import com.whizzpered.bubbleshooter.engine.utils.sin
import java.util.*

/**
 * Created by phdeh on 27/02/2017.
 */

abstract class Shape {
    val position: MutablePoint
    var priority: Float
    var deformation = 0f
    var angle = 0f

    fun project(angle: Float): Point {
        val px = (MathUtils.cos(-angle) * position.x - MathUtils.sin(-angle) * position.y)
        val py = position.z - (MathUtils.sin(-angle) * position.x + MathUtils.cos(-angle) * position.y) / 5f
        return Point(px, py)
    }

    constructor(priority: Float = 0f) {
        position = MutablePoint(0f, 0f, 0f)
        this.priority = priority
    }

    constructor(p: Point, priority: Float = 0f) {
        position = p.toMutablePoint()
        this.priority = priority
    }

    constructor(p: MutablePoint, priority: Float = 0f) {
        position = p.copy()
        this.priority = priority
    }

    constructor(x: Float, y: Float, z: Float, priority: Float = 0f) {
        position = MutablePoint(x, y, z)
        this.priority = priority
    }

    abstract fun render(x: Float, y: Float, z: Float, angle: Float)
}

object emptyShape : Shape() {
    override fun render(x: Float, y: Float, z: Float, angle: Float) {

    }
}

class Billboard : Shape {
    val texture: String
    private val sprite: Sprite
    var width: Float
    var height: Float

    private var tryed = false

    constructor(texture: String, width: Float = 1.0f, height: Float = 1.0f) : super() {
        this.texture = texture
        this.width = width
        this.height = height
        sprite = Main.atlas.getSprite(texture)
    }

    constructor(texture: String, width: Float = 1.0f, height: Float = 1.0f, p: Point) : super(p) {
        this.texture = texture
        this.width = width
        this.height = height
        sprite = Main.atlas.getSprite(texture)
    }

    constructor(texture: String, width: Float = 1.0f, height: Float = 1.0f, p: MutablePoint) : super(p) {
        this.texture = texture
        this.width = width
        this.height = height
        sprite = Main.atlas.getSprite(texture)
    }

    constructor(texture: String, width: Float = 1.0f, height: Float = 1.0f,
                x: Float, y: Float, z: Float) : super(x, y, z) {
        this.texture = texture
        this.width = width
        this.height = height
        sprite = Main.atlas.getSprite(texture)
    }

    override fun render(x: Float, y: Float, z: Float, angle: Float) {
        val p = project(angle)
        sprite.render {
            it.x = x + p.x * (1 + deformation * sin(angle))
            it.y = z + y + p.y
            it.setSize(width * (1 + deformation * sin(angle)), height)
            it.angle = 0f
        }
    }
}

class Circle : Shape {
    var color: Color
    var radius: Float

    constructor(radius: Float = 0f, color: Color = Color.BLACK) : super() {
        this.radius = radius
        this.color = color
    }

    constructor(p: Point, radius: Float = 0f, color: Color = Color.BLACK) : super(p) {
        this.radius = radius
        this.color = color
    }

    constructor(p: MutablePoint, radius: Float = 0f, color: Color = Color.BLACK) : super(p) {
        this.radius = radius
        this.color = color
    }

    constructor(x: Float, y: Float, z: Float,
                radius: Float = 0f, color: Color = Color.BLACK) : super(x, y, z) {
        this.radius = radius
        this.color = color
    }

    override fun render(x: Float, y: Float, z: Float, angle: Float) {
        //throw UnsupportedOperationException("Not supported yet")
    }
}

class Model : Shape {

    private val shapes: Array<Shape>

    private fun toArray(m: MutableList<Shape>): Array<Shape> {
        synchronized(m) {
            val arr = Array<Shape>(m.size) { emptyShape }
            for (i in 0..m.size - 1)
                arr[i] = m[i]
            return arr
        }
    }

    constructor(initializer: (ModelBuilder) -> Unit) : super() {
        val m = ModelBuilder(this)
        initializer(m)
        m.lock()
        shapes = toArray(m.shapes)
    }

    constructor(p: Point, initializer: (ModelBuilder) -> Unit) : super(p) {
        val m = ModelBuilder(this)
        initializer(m)
        m.lock()
        shapes = toArray(m.shapes)
    }

    constructor(p: MutablePoint, initializer: (ModelBuilder) -> Unit) : super(p) {
        val m = ModelBuilder(this)
        initializer(m)
        m.lock()
        shapes = toArray(m.shapes)
    }

    constructor(x: Float, y: Float, z: Float, initializer: (ModelBuilder) -> Unit) : super(x, y, z) {
        val m = ModelBuilder(this)
        initializer(m)
        m.lock()
        shapes = toArray(m.shapes)
    }

    fun render(x: Float, y: Float, angle: Float) {
        render(x, 0f, y, angle)
    }

    override fun render(x: Float, y: Float, z: Float, angle: Float) {
        if (!Main.isRenderThread())
            return

        val tangle = angle + this.angle
        val shapeComparator = object : Comparator<Shape> {
            override fun compare(a: Shape?, b: Shape?): Int {
                if (a == null || b == null)
                    return 0
                else {
                    val ya = a.position.x * MathUtils.sin(-tangle) +
                            a.position.y * MathUtils.cos(-tangle) + a.priority
                    val yb = b.position.x * MathUtils.sin(-tangle) +
                            b.position.y * MathUtils.cos(-tangle) + b.priority
                    return ya.compareTo(yb)
                }
            }
        }
        shapes.sortWith(shapeComparator)
        val p = project(angle)
        shapes.forEach { it.render(x + p.x, y, z + p.y, tangle) }
    }

    class ModelBuilder internal constructor(val model: Model) {

        private var locked = false
        internal val shapes: MutableList<Shape> = mutableListOf()

        fun add(s: Shape) {
            if (!locked)
                shapes += s
        }

        operator fun plusAssign(s: Shape) = add(s)

        fun remove(s: Shape) {
            if (!locked)
                shapes -= s
        }

        operator fun minusAssign(s: Shape) = add(s)

        fun lock() {
            locked = true
        }

    }

}