package com.whizzpered.bubbleshooter.game

import com.whizzpered.bubbleshooter.engine.gui.*

fun makeGUI() {
    val pic = Picture(
            texture = "enemy",
            position = Position(Coordinate.AT_CENTER, 0f from Border.TOP),
            size = Size(.5f.toLength(), .5f.toLength())
    )
    //Game.gui += pic
}