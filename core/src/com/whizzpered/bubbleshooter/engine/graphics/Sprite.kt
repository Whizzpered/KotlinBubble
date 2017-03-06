package com.whizzpered.bubbleshooter.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.whizzpered.bubbleshooter.engine.handler.Main
import kotlin.concurrent.thread

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
    private var libgdxsprite: com.badlogic.gdx.graphics.g2d.Sprite? = null
    private var handler: Atlas.AtlasHandler? = null

    fun render() {
        if (!Main.isRenderThread())
            return
        if (!tryed || atlas.handler != handler) {
            handler = atlas.handler
            val handler = handler
            tryed = true
            if (handler != null)
                libgdxsprite = handler.atlas.createSprite(texture.replace("/", "__"))
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

    enum class Quality(val scale: Float, val antialiasing: Boolean = true) {
        HIGH(1f),
        MEDIUM(.5f),
        LOW(.25f, false),
        CALCULATOR(.125f, false);

        val atlasPath = this.name.toLowerCase() + "_quality_atlas"
    }

    private var actualQuality = Quality.values().maxBy { it.scale } ?: Quality.values()[0]

    internal @Volatile var handler: AtlasHandler = AtlasHandler(quality)

    internal class AtlasHandler(val quality: Quality) {
        val atlas: TextureAtlas
        get() {
            if (realAtlas == null)
                realAtlas = TextureAtlas(Gdx.files.internal(quality.atlasPath + ".atlas"))
            val r = realAtlas
            return if (r != null) r else
                throw NullPointerException("Expression 'realAtlas' must not be null")
        }

        private var realAtlas: TextureAtlas? = null
    }

    var quality: Quality
        get() = actualQuality
        set(value) = update(value)

    private fun update(q: Quality) {
        if (q != actualQuality) {
            actualQuality = q
            thread {
                val h = handler
                handler = AtlasHandler(q)
                h.atlas.dispose()
            }
        }
    }

    fun createSprite(texture: String, color: Color = Color.WHITE): Sprite = Sprite(this, texture, color)

}