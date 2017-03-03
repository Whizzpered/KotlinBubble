package com.whizzpered.bubbleshooter.game.creatures

import com.whizzpered.bubbleshooter.engine.graphics.Billboard
import com.whizzpered.bubbleshooter.engine.graphics.Model
import com.whizzpered.bubbleshooter.engine.memory.AbstractPool
import com.whizzpered.bubbleshooter.engine.memory.AbstractPoolConfiguration
import com.whizzpered.bubbleshooter.engine.memory.PoolConfiguration
import com.whizzpered.bubbleshooter.engine.memory.Poolable
import com.whizzpered.bubbleshooter.game.Game

private val model = Model {
    it += Billboard("enemy/right_eye", 0.5f, 0.5f, 0f, 0f, 0.5f)
}

class Bubble : Creature {

    //POOL\//
    companion object config : PoolConfiguration<Bubble>(128, { Bubble(it) })

    override fun getPoolConfigurator(): AbstractPoolConfiguration<out Poolable> {
        return config
    }

    override val pool: AbstractPool<out Poolable>

    internal constructor(pool: AbstractPool<Bubble>) : super() {
        this.pool = pool
    }
    //\POOL//

    override fun reset() {
    }

    private var lifetime = 2f

    override fun lock() {
        MAX_VELOCITY.originalValue = 15f
        lifetime = 2f
    }

    val movementHandler = handler { delta ->
        position += velocity * (delta * MAX_VELOCITY.value)
        lifetime -= delta
        if (lifetime <= 0)
            if (game is Game)
                (game as Game).context -= this

    }

    val renderHandler = handler { delta ->
        model.render(position.x, position.y, angle)
    }

    override fun initHandlers() {
        initialActHandlers.add(movementHandler)
        initialRenderHandlers.add(renderHandler)
    }
}