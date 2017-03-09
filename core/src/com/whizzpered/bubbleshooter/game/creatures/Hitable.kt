package com.whizzpered.bubbleshooter.game.creatures

import com.whizzpered.bubbleshooter.engine.entities.Entity

interface Hitable {
    fun hit(hitter: Entity)

    val hitRadius: Float
}