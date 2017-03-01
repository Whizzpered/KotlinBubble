package com.whizzpered.bubbleshooter.game

import com.badlogic.gdx.graphics.OrthographicCamera
import com.whizzpered.bubbleshooter.engine.entities.Entity
import com.whizzpered.bubbleshooter.engine.handler.Platform
import com.whizzpered.bubbleshooter.engine.memory.Context
import com.whizzpered.bubbleshooter.game.creatures.Enemy
import com.whizzpered.bubbleshooter.handler.Input
import java.util.*

object Game {
    val random = Random()
    val context = Context<Entity>(100)
    internal val renderBuffer = context.sorted {
        a, b ->
        -(a as Entity).position.y.compareTo((b as Entity).position.y)
    }
    val camera = OrthographicCamera(16f, 12f)
    val height = 6f
    val input = Input()
    val platform = Platform

    fun init() {
        for (i in 0..100) {
            val enemy = context new Enemy.config
            enemy.position.set(
                    x = (random.nextFloat() - .5f) * 10,
            y = (random.nextFloat() - .5f) * 100
            )
            enemy.target.set(
                    x = (random.nextFloat() - .5f) * 10,
                    y = (random.nextFloat() - .5f) * 10
            )
            context += enemy
        }

        context += context new Enemy.config
        context += context new Enemy.config
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