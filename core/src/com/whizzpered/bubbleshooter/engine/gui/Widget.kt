package com.whizzpered.bubbleshooter.engine.gui

import com.whizzpered.bubbleshooter.engine.memory.ActionContainer


open class Coordinate {
    companion object AT_CENTER : Coordinate() {

    }й

    val from: Border
    var dist = 0f
        get() = lambda()
        private set
    private var lambda: () -> Float

    private constructor() {
        from = Border.LEFT
        lambda = { 0f }
    }

    internal constructor(from: Border, lambda: () -> Float) {
        this.from = from
        this.lambda = lambda
    }

    override fun toString(): String {
        return "$dist from $from"
    }
}


enum class Border {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM
}

infix fun Float.from(border: Border): Coordinate {
    return Coordinate(border, { this })
}

infix fun Int.from(border: Border): Coordinate {
    val v = this.toFloat()
    return Coordinate(border, { v })
}

infix fun (() -> Float).from(border: Border): Coordinate {
    return Coordinate(border, this)
}

data class Position(var x: Coordinate, var y: Coordinate)

class Length {
    var value = 0f
        get() = lambda()
        private set

    private val lambda: () -> Float

    internal constructor(lambda: () -> Float) {
        this.lambda = lambda
    }

    internal constructor(value: Float) {
        this.lambda = { value }
    }
}

fun Float.toLength() = Length(this)

fun (() -> Float).toLength() = Length(this)

data class Size(var width: Length, var height: Length)

abstract class Widget(var position: Position, var size: Size) {
    var parent: Widget? = null

    var enabled = true
        set(value) {
            onEnabledActions(this); this.enabled = true
        }
    var visible = true

    val onEnabledActions = ActionContainer<Widget>()

    operator fun unaryPlus() {
        enabled = true
    }

    operator fun unaryMinus() {
        enabled = false
    }

    var currentX = 0f
        get() {
            val parent = this.parent
            if (parent == null)
                return 0f
            else {
                if (position.x == Coordinate.AT_CENTER)
                    return parent.currentX
                else {
                    val p = position.x
                    if (p.from == Border.LEFT || p.from == Border.TOP)
                        return parent.currentX + (+.5f - p.dist - size.height.value / 2) * parent.size.width.value
                    else
                        return parent.currentX + (-.5f + p.dist + size.width.value / 2) * parent.size.width.value
                }
            }
        }
        private set

    var currentY = 0f
        get() {
            val parent = this.parent
            if (parent == null)
                return 0f
            else {
                if (position.y == Coordinate.AT_CENTER)
                    return parent.currentY
                else {
                    val p = position.y
                    if (p.from == Border.LEFT || p.from == Border.TOP)
                        return parent.currentY + (+.5f - p.dist - size.height.value / 2) * parent.size.height.value
                    else
                        return parent.currentY + (-.5f + p.dist + size.height.value / 2) * parent.size.height.value
                }
            }
        }
        private set

    var currentWidth = 0f
        get() {
            val parent = this.parent
            if (parent == null)
                return size.width.value
            else
                return size.width.value * parent.currentWidth
        }
        private set

    var currentHeight = 0f
        get() {
            val parent = this.parent
            if (parent == null)
                return size.height.value
            else
                return size.height.value * parent.currentHeight
        }
        private set

    abstract fun render(delta: Float)

    abstract fun act(delta: Float)
}
