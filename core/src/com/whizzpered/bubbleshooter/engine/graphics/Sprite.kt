package com.whizzpered.bubbleshooter.engine.graphics

import com.badlogic.gdx.graphics.Color
import com.whizzpered.bubbleshooter.engine.handler.Main

class Sprite internal constructor(val atlas: Atlas, val texture: String, var color: Color) {
    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f

    public fun setPosition(x: Float = 0f, y: Float = 0f) {
        this.x = x
        this.y = y
    }

    public fun setSize(width: Float = 0f, height: Float = 0f) {
        this.width = width
        this.height = height
    }

    private var tryed = false
    private var quality = Atlas.Quality.HIGH
    private var libgdxsprite: com.badlogic.gdx.graphics.g2d.Sprite? = null

    fun render() {
        if (!Main.isRenderThread())
            return
        if (!tryed) {
            tryed = true
            libgdxsprite = Main.createSprite(texture)
        }
        val v = libgdxsprite
        if (v != null) {
            val x = x
            val y = y
            val width = width
            val height = height
            v.color = color
            v.x = x - width / 2f
            v.y = y - height / 2f
            v.setSize(width, height)
            val b = Main.batch
            if (b != null && b.isDrawing)
                v.draw(b)
        }
    }
}

class Atlas {
    var quality = Quality.HIGH

    enum class Quality(val atlasPath: String, val scale: Float, val antialiasing: Boolean = true) {
        HIGH("high_quality_atlas", 1f),
        MEDIUM("medium_quality_atlas", .5f),
        LOW("low_quality_atlas", .25f, false)
    }

    fun getSrpite(texture: String, color: Color = Color.WHITE) = Sprite(this, texture, color)
}