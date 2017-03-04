package com.whizzpered.bubbleshooter.engine.handler

import com.whizzpered.bubbleshooter.engine.graphics.MutablePoint
import com.whizzpered.bubbleshooter.engine.graphics.Point
import com.whizzpered.bubbleshooter.engine.gui.*
import com.whizzpered.bubbleshooter.game.Game
import java.util.*

abstract class AbstractGame {
    val random = Random()
    val camera = MutablePoint(0f, 0f)
    val gui = Container(
            Position(Coordinate.AT_CENTER, Coordinate.AT_CENTER),
            Size(
                    width = { Main.viewportWidth }.toLength(),
                    height = { Main.viewportHeight }.toLength()
            )
    )
    var height = 6f
    abstract fun init()
    abstract fun act(delta: Float)
    abstract fun render(delta: Float)
    abstract fun ai(delta: Float)

    fun project(screenX: Float, screenY: Float): Point {
        val sx = (screenX - Main.width / 2).toFloat() /
                (Main.width.toFloat() / Main.camera!!.viewportWidth!!.toFloat())
        val sy = (Main.height / 2 - screenY).toFloat() /
                (Main.height.toFloat() / Main.camera!!.viewportHeight!!.toFloat())
        return Point(sx + Game.camera.x, sy + Game.camera.y)
    }
}