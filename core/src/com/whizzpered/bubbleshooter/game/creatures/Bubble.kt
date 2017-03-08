package com.whizzpered.bubbleshooter.game.creatures

import com.badlogic.gdx.graphics.Color
import com.whizzpered.bubbleshooter.engine.graphics.Billboard
import com.whizzpered.bubbleshooter.engine.graphics.Model
import com.whizzpered.bubbleshooter.engine.handler.Main
import com.whizzpered.bubbleshooter.engine.memory.AbstractPool
import com.whizzpered.bubbleshooter.engine.memory.AbstractPoolConfiguration
import com.whizzpered.bubbleshooter.engine.memory.PoolConfiguration
import com.whizzpered.bubbleshooter.engine.memory.Poolable
import com.whizzpered.bubbleshooter.game.Game

enum class BubbleType {
    BLUE(25, 105, 255),
    PINK(225, 105, 255),
    RED(255, 205, 25),
    GREEN(130, 220, 50),
    YELLOW(255, 200, 50),
    CYAN(140, 220, 255);

    val color: Color

    constructor(color: Color) {
        this.color = color
    }

    constructor(r: Int, g: Int, b: Int) {
        this.color = Color(r / 255f, g / 255f, b / 255f, 1f)
    }
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

    val sprite = Main.atlas.getSprite("enemy/right_eye")
    var type = BubbleType.BLUE

    override fun reset() {

    }

    private var lifetime = 2f

    override fun lock() {
        val types = BubbleType.values()
        type = types[game.random.nextInt(types.size)]
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
        sprite.render {
            it.setSize(.3f, .3f)
            it.setPosition(position.x, position.y + 0.5f)
            it.color = type.color
        }
    }

    override fun initHandlers() {
        initialActHandlers.add(movementHandler)
        initialRenderHandlers.add(renderHandler)
    }
}