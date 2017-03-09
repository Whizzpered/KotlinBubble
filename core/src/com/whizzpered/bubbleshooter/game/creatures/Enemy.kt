package com.whizzpered.bubbleshooter.game.creatures

import com.whizzpered.bubbleshooter.engine.entities.Entity
import com.whizzpered.bubbleshooter.engine.graphics.Billboard
import com.whizzpered.bubbleshooter.engine.graphics.Model
import com.whizzpered.bubbleshooter.engine.memory.AbstractPool
import com.whizzpered.bubbleshooter.engine.memory.AbstractPoolConfiguration
import com.whizzpered.bubbleshooter.engine.memory.PoolConfiguration
import com.whizzpered.bubbleshooter.engine.memory.Poolable
import com.whizzpered.bubbleshooter.game.Game
import com.whizzpered.bubbleshooter.utils.atan2
import com.whizzpered.bubbleshooter.utils.cos
import com.whizzpered.bubbleshooter.utils.dist
import com.whizzpered.bubbleshooter.utils.sin

private val model = Model {
    it += Billboard("enemy/left_eye", 0.3f, 0.3f, 0f, .2f, .95f)
    it += Billboard("enemy/right_eye", 0.3f, 0.3f, 0f, -.2f, .95f)
    it += Billboard("enemy/body", 1f, 1f, 0f, 0f, .5f)
}

class Enemy : Creature, Hitable {

    //POOL\//
    companion object config : PoolConfiguration<Enemy>(32, { Enemy(it) })

    override fun getPoolConfigurator(): AbstractPoolConfiguration<out Poolable> {
        return config
    }

    override val pool: AbstractPool<out Poolable>

    internal constructor(pool: AbstractPool<Enemy>) : super() {
        this.pool = pool
    }
    //\POOL//

    override fun reset() {

    }

    override fun lock() {
        radius = .5f
    }

    var target = this.position.copy()

    val movementHandler = handler { delta ->
        val (tx, ty) = target
        val ta = atan2(ty - position.y, tx - position.x)
        val dist = dist(tx, ty, position.x, position.y)
        if (dist > 2) {
            velocity.x = cos(ta) * MAX_VELOCITY.value
            velocity.y = sin(ta) * MAX_VELOCITY.value
        } else {
            velocity.x = 0f
            velocity.y = 0f

            target.set(
                    x = (game.random.nextFloat() - .5f) * 30,
                    y = (game.random.nextFloat() - .5f) * 30
            )
        }
        velocity += acceleration * delta
        position += velocity * delta
        angle = ta
    }

    val renderHandler = handler { delta ->
        model.render(position.x, position.y, angle)
    }

    override fun initHandlers() {
        initialActHandlers.add(movementHandler)
        initialRenderHandlers.add(renderHandler)
    }

    override fun hit(hitter: Entity) {
        if (hitter is Bubble) {
            val currgame = this.game
            if (currgame is Game) {
                val game = currgame as Game
                game.context -= this
            }
        }
    }

    override val hitRadius: Float
        get() = radius

}