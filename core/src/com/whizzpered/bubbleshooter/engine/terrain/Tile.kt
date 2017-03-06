package com.whizzpered.bubbleshooter.engine.terrain

import com.whizzpered.bubbleshooter.engine.handler.Main

open class Tile(val passable: Boolean, val texture: String) {
    val size = 1f
    private var tryed = false
    private val sprite = Main.atlas.createSprite(texture)

    open fun render(x: Int, y: Int, terrain: Terrain) {
        sprite.setPosition(x * size, y * size)
        sprite.setSize(size, size)
        sprite.render()
    }
}