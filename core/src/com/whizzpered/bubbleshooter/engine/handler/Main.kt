package com.whizzpered.bubbleshooter.engine.handler

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.whizzpered.bubbleshooter.game.Game

object Main : ApplicationAdapter() {
    class Delta {
        private var last = System.currentTimeMillis()

        operator fun invoke(): Float {
            val cur = System.currentTimeMillis()
            val delta = cur - last
            last = cur
            return delta / 1000f
        }
    }

    var camera: OrthographicCamera? = null
        private set

    internal var renderThread: Thread? = null
    val proc = Runtime.getRuntime().availableProcessors()
    internal val actThread = ThreadHandler(proc / 5 + 2) {
        val delta = Delta()
        while (!it.dispose) {
            Game.act(delta() / it.threads)
        }
    }
    internal val aiThread = ThreadHandler(proc / 5 + 1) {
        val delta = Delta()
        while (!it.dispose) {
            Game.ai(delta() / it.threads)
        }
    }

    var width = 800
        internal set
    var height = 600
        internal set

    internal var paused = false
    internal var dispose = false
    internal var atlas: TextureAtlas? = null
    var batch: SpriteBatch? = null
        internal set
    var img: Texture? = null
        internal set

    fun isRenderThread(): Boolean {
        return Thread.currentThread() == renderThread
    }

    override fun create() {
        batch = SpriteBatch()
        Game.init()
        atlas = TextureAtlas(Gdx.files.internal("pack.atlas"))
        camera = OrthographicCamera(16f, 12f)
        resume()

    }

    fun createSprite(name: String): Sprite? {
        val a = atlas
        return atlas?.createSprite(name)
    }

    val renderDelta = Delta()
    override fun render() {
        if (!paused || Platform.desktop) {
            if (Gdx.graphics.width > Gdx.graphics.height) {
                camera?.viewportWidth = width.toFloat() / (height.toFloat() / Game.height)
                camera?.viewportHeight = Game.height
            } else {
                camera?.viewportHeight = height.toFloat() / (width.toFloat() / Game.height)
                camera?.viewportWidth = Game.height
            }
            if (Platform.iOS) Gdx.gl.glViewport(0, 0, width, height)
            camera?.position?.set(Game.camera.x, Game.camera.y, 0f)
            camera?.update()
            batch?.projectionMatrix = camera?.combined
            batch?.begin()
            renderThread = Thread.currentThread()
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or
                    if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0)
            Game.render(renderDelta())
            batch?.end()
        }
    }

    override fun dispose() {
        dispose = true
        batch?.dispose()
        img?.dispose()
    }


    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun pause() {
        actThread.stop()
        aiThread.stop()
    }

    override fun resume() {
        actThread.start()
        aiThread.start()
    }
}
