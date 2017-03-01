package com.whizzpered.bubbleshooter.game.creatures

import com.badlogic.gdx.math.MathUtils
import com.whizzpered.bubbleshooter.engine.graphics.Billboard
import com.whizzpered.bubbleshooter.engine.graphics.Model
import com.whizzpered.bubbleshooter.engine.handler.Input
import com.whizzpered.bubbleshooter.engine.handler.Key
import com.whizzpered.bubbleshooter.engine.handler.Platform
import com.whizzpered.bubbleshooter.engine.memory.AbstractPool
import com.whizzpered.bubbleshooter.engine.memory.AbstractPoolConfiguration
import com.whizzpered.bubbleshooter.engine.memory.PoolConfiguration
import com.whizzpered.bubbleshooter.engine.memory.Poolable
import com.whizzpered.bubbleshooter.game.Game
import com.whizzpered.bubbleshooter.utils.cos
import com.whizzpered.bubbleshooter.utils.sin

private val model = Model {
    it += Billboard("hero/body", 1f, 1.2f, 0f, 0f, .6f)
    it += Billboard("hero/month", .1f, .1f, .3f, 0f, .6f)
    var eye1 = Billboard("hero/eye", .15f, .3f, .3f, .2f, .75f)
    eye1.deformation = -1f
    it += eye1
    var eye2 = Billboard("hero/eye", .15f, .3f, .3f, -.2f, .75f)
    eye2.deformation = -1f
    it += eye2
    var cap = Billboard("hero/cap", .6f, .5f, -.2f, 0f, .95f)
    cap.priority = .1f
    it += cap
    var pompon = Billboard("hero/pompon", .3f, .3f, -.3f, 0f, 1.1f)
    pompon.priority = .1f
    it += pompon
}

class Hero : Creature {

    //POOL\//
    companion object config : PoolConfiguration<Hero>(32, { Hero(it) })

    override fun getPoolConfigurator(): AbstractPoolConfiguration<out Poolable> {
        return config
    }

    override val pool: AbstractPool<out Poolable>

    internal constructor(pool: AbstractPool<Hero>) : super() {
        this.pool = pool
    }
    //\POOL//

    override fun reset() {

    }

    override fun lock() {
        MAX_VELOCITY.originalValue = 2f
    }

    var target = this.position.copy()

    val movementHandler = handler { delta ->
        var x = 0f
        var y = 0f
        var power = 1f
        if (Platform.desktop) {
            if (Input.keyboard.isKeyDown(Key.W, Key.UP))
                y += 1f
            if (Input.keyboard.isKeyDown(Key.S, Key.DOWN))
                y -= 1f
            if (Input.keyboard.isKeyDown(Key.D, Key.RIGHT))
                x += 1f
            if (Input.keyboard.isKeyDown(Key.A, Key.LEFT))
                x -= 1f
        } else {
            Input.touch.forEach { if (it.pressed) x += it.dx.toFloat() }
            Input.touch.forEach { if (it.pressed) y -= it.dy.toFloat() }
            if (Math.abs(x) < 3) x = 0f
            if (Math.abs(y) < 3) y = 0f

        }
        if (x != 0f || y != 0f) {
            angle = MathUtils.atan2(y, x)
            velocity.set(x = cos(angle) * MAX_VELOCITY.value * power,
                    y = sin(angle) * MAX_VELOCITY.value * power)
        } else
            velocity.set(0f, 0f)
        position += velocity * (delta)

        val c = Game.camera

        val p1 = 30
        val p2 = 100

        c.x = (c.x * (p1 - delta * p2) + (position.x + velocity.x) * delta * p2) / p1
        c.y = (c.y * (p1 - delta * p2) + (position.y + velocity.y) * delta * p2) / p1

    }

    val renderHandler = handler { delta ->
        model.render(position.x, position.y, angle)
    }

    override fun initHandlers() {
        initialActHandlers.add(movementHandler)
        initialRenderHandlers.add(renderHandler)
    }

}