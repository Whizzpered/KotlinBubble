package com.whizzpered.bubbleshooter.engine.gui

import com.badlogic.gdx.graphics.g2d.Sprite
import com.whizzpered.bubbleshooter.engine.handler.Main
import com.whizzpered.bubbleshooter.utils.sin

class Picture(val texture: String, position: Position, size: Size) : Widget(position, size) {
    private var libgdxsprite: Sprite? = null
    private var tryed = false

    override fun render(delta: Float) {
        if (!Main.isRenderThread())
            return

        if (!tryed) {
            tryed = true
            libgdxsprite = Main.createSprite(texture)
        }
        val v = libgdxsprite
        if (v != null) {
            val x = currentX
            val y = currentY
            val w = currentWidth
            val h = currentHeight

            v.setPosition(x - w/2, y - h/2)
            v.setSize(w, h)
            v.draw(Main.batch)
        }
    }

    override fun act(delta: Float) {

    }
}