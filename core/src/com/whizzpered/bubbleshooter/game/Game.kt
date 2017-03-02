package com.whizzpered.bubbleshooter.game

import com.whizzpered.bubbleshooter.engine.entities.Entity
import com.whizzpered.bubbleshooter.engine.graphics.MutablePoint
import com.whizzpered.bubbleshooter.engine.graphics.Point
import com.whizzpered.bubbleshooter.engine.handler.Main
import com.whizzpered.bubbleshooter.engine.memory.Context
import com.whizzpered.bubbleshooter.game.creatures.Enemy
import com.whizzpered.bubbleshooter.game.creatures.Hero
import java.util.*

object Game {
    val random = Random()
    val context = Context<Entity>(100)
    internal val renderBuffer = context.sorted {
        a, b ->
        -(a as Entity).position.y.compareTo((b as Entity).position.y)
    }
    val camera = MutablePoint(0f, 0f)
    val height = 6f

    fun project(screenX: Float, screenY: Float): Point {
        val sx = (screenX - Main.width / 2).toFloat() /
                (Main.width.toFloat() / Main.camera!!.viewportWidth!!.toFloat())
        val sy = (Main.height / 2 - screenY).toFloat() /
                (Main.height.toFloat() / Main.camera!!.viewportHeight!!.toFloat())
        return Point(sx + camera.x, sy + camera.y)
    }

    fun init() {
        for (i in 0..50) {
            val enemy = context new Enemy.config
            enemy.position.set(
                    x = (random.nextFloat() - .5f) * 10,
                    y = (random.nextFloat() - .5f) * 10
            )
            enemy.target.set(
                    x = (random.nextFloat() - .5f) * 10,
                    y = (random.nextFloat() - .5f) * 10
            )
            context += enemy
        }
        context += context new Hero.config
    }

    fun act(delta: Float) {
        context.forEach { it.act(delta) }
    }

    fun render(delta: Float) {
        renderBuffer.forEach { it.render(delta) }
    }

    fun ai(delta: Float) {
        context.forEach { it.ai(delta) }
    }
}