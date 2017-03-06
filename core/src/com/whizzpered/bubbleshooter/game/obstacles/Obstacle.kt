package com.whizzpered.bubbleshooter.game.obstacles

import com.whizzpered.bubbleshooter.engine.entities.Entity
import com.whizzpered.bubbleshooter.engine.graphics.Model
import com.whizzpered.bubbleshooter.engine.memory.AbstractPool
import com.whizzpered.bubbleshooter.engine.memory.AbstractPoolConfiguration
import com.whizzpered.bubbleshooter.engine.memory.Poolable

abstract class Obstacle(private val model: Model, override val pool: AbstractPool<out Poolable>) : Entity() {
    override fun reset() {
    }

    override fun initHandlers() {
    }

    override fun getPoolConfigurator(): AbstractPoolConfiguration<out Poolable> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun lock() {
    }
}