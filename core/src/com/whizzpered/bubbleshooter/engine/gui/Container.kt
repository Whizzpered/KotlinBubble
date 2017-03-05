package com.whizzpered.bubbleshooter.engine.gui

import com.whizzpered.bubbleshooter.engine.memory.UnpureSet

abstract class AbstractContainer(position: Position, size: Size) : Widget(position, size) {
    abstract fun add(w: Widget)
    abstract fun remove(w: Widget)
    abstract fun contains(w: Widget): Boolean

    operator fun plusAssign(w: Widget) = add(w)
    operator fun minusAssign(w: Widget) = remove(w)
}

class Container(position: Position, size: Size) : AbstractContainer(position, size) {
    val widgets = UnpureSet<Widget>(32)

    override fun add(w: Widget) {
        widgets += w
        w.parent = this
    }

    override fun remove(w: Widget) {
        widgets -= w
        w.parent = null
    }

    override fun contains(w: Widget): Boolean {
        return widgets.contains(w)
    }

    override fun render(delta: Float) {
        widgets.forEach {
            it.render(delta)
        }
    }

    override fun act(delta: Float) {
        widgets.forEach {
            it.act(delta)
        }
    }
}