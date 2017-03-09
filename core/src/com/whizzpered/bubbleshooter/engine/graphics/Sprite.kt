package com.whizzpered.bubbleshooter.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.whizzpered.bubbleshooter.engine.handler.Main
import com.whizzpered.bubbleshooter.engine.memory.makeListFrom
import kotlin.concurrent.thread
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedQueue

class Sprite internal constructor(val atlas: Atlas, val texture: String) {
    class SpriteBuilder internal constructor() {
        var x = 0f
        var y = 0f
        var width = 0f
        var height = 0f
        var angle = 0f
        var color: Color = Color(1f, 1f, 1f, 1f)

        public fun setPosition(x: Float = 0f, y: Float = 0f) {
            this.x = x
            this.y = y
        }

        public fun setSize(width: Float = 0f, height: Float = 0f) {
            this.width = width
            this.height = height
        }
    }

    private val builder = SpriteBuilder()

    private var tryed = false
    private var libgdxsprite: com.badlogic.gdx.graphics.g2d.Sprite? = null
    private var handler: Atlas.AtlasHandler? = null

    fun render(build: (SpriteBuilder) -> Unit) {
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
            builder.x = 0f
            builder.y = 0f
            builder.width = 0f
            builder.height = 0f
            builder.angle = 0f
            builder.color = Color.WHITE
            build(builder)
            val x = builder.x
            val y = builder.y
            val width = builder.width
            val height = builder.height
            v.setOrigin(v.width / 2, v.height / 2)
            v.color = builder.color
            v.setCenter(x, y)
            v.rotation = builder.angle
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
        LOW(.25f, antialiasing = false),
        POTATO(.125f, antialiasing = false);

        operator fun inc(): Quality {
            val i = Sorter.sortedQualities.indexOf(this)
            if (i < Sorter.sortedQualities.size - 1)
                return Sorter.sortedQualities[i + 1]
            else
                return this
        }

        operator fun dec(): Quality {
            val i = Sorter.sortedQualities.indexOf(this)
            if (i > 0)
                return Sorter.sortedQualities[i - 1]
            else
                return this
        }

        private companion object Sorter {
            private val sortedQualities = makeListFrom(Quality.values(), {it}).sortedBy{it.scale}
        }

        val atlasPath = this.name.toLowerCase() + "_quality_atlas"
    }

    private var actualQuality = Quality.values().maxBy { it.scale } ?: Quality.values()[0]

    internal @Volatile var handler: AtlasHandler = AtlasHandler(quality)
    private val queue = ConcurrentLinkedQueue<AtlasHandler>()

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
                queue.add(h)
            }
        }
    }

    fun render(delta: Float) {
        do {
            val v = queue.poll()
            if (v != null)
                v.atlas.dispose()
        } while(v != null)
    }

    private val sprites = mutableListOf<WeakReference<Sprite>>()

    fun getSprite(texture: String): Sprite {
        synchronized(sprites) {
            sprites.forEach {
                val v = it.get()
                if (v != null && v.texture == texture)
                    return v
            }
        }
        val v = Sprite(this, texture)
        sprites += WeakReference(v)
        return v
    }

}