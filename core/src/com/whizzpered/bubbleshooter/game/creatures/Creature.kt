package com.whizzpered.bubbleshooter.game.creatures

import com.whizzpered.bubbleshooter.engine.entities.Entity
import com.whizzpered.bubbleshooter.utils.*

abstract class Creature : Entity() {

    val MAX_VELOCITY = createStatement(0.5f) //* tiles / s

    val MAX_TURN_SPEED = createStatement(1f) //* spin / s

    val MAX_HEALTH_POINTS = createStatement(100f)
    val HEALTH_POINTS = MAX_HEALTH_POINTS % 100
    val HEALTH_POINTS_RESTORATION = MAX_HEALTH_POINTS % 1 //* % / s

}