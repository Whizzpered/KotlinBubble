package com.whizzpered.bubbleshooter.engine.terrain

import com.badlogic.gdx.graphics.g2d.Sprite
import com.whizzpered.bubbleshooter.engine.handler.Main

open class Tile(val passable: Boolean, val texture: String) {
    val size = 1f
    private var tryed = false
    private var libgdxsprite: Sprite? = null

    open fun render(x: Int, y: Int, terrain: Terrain) {
        if (!tryed) {
            tryed = true
            libgdxsprite = Main.createSprite(texture)
        }
        val v = libgdxsprite
        if (v != null) {
            v.setPosition(x * size, y * size)
            v.setSize(size, size)
            v.draw(Main.batch)
        }
    }
}