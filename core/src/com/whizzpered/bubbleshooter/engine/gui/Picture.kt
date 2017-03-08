package com.whizzpered.bubbleshooter.engine.gui

import com.whizzpered.bubbleshooter.engine.handler.Main

class Picture(val texture: String, position: Position, size: Size) : Widget(position, size) {
    private val sprite = Main.atlas.getSprite(texture)

    override fun render(delta: Float) {
        val x = currentX
        val y = currentY
        val w = currentWidth
        val h = currentHeight

        sprite.render {
            it.setPosition(x, y)
            it.setSize(w, h)
        }
    }

    override fun act(delta: Float) {
        
    }
}