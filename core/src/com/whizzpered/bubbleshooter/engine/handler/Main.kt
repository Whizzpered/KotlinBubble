package com.whizzpered.bubbleshooter.engine.handler

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.whizzpered.bubbleshooter.engine.graphics.Atlas
import com.whizzpered.bubbleshooter.game.Game

object EMPTY_GAME : AbstractGame() {
    override fun init() {

    }

    override fun act(delta: Float) {

    }

    override fun ai(delta: Float) {

    }

    override fun render(delta: Float) {

    }
}

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

    var atlas = Atlas()

    var game: AbstractGame = Game

    var camera: OrthographicCamera? = null
        private set
    private var guicamera: OrthographicCamera? = null

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

    var viewportWidth = 0f
        get() {
            val c = camera
            if (c != null)
                return c.viewportWidth
            else
                return 0f
        }
        private set
    var viewportHeight = 0f
        get() {
            val c = camera
            if (c != null)
                return c.viewportHeight
            else
                return 0f
        }
        private set

    internal var paused = false
    internal var dispose = false
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
        camera = OrthographicCamera(16f, 12f)
        guicamera = OrthographicCamera(16f, 12f)

        Input.keyboard.keyPressedActions += {
            if (it == Key.L)
                atlas.quality++
            if (it == Key.K)
                atlas.quality++
        }
        resume()
    }

    val renderDelta = Delta()
    var lastTime = System.currentTimeMillis()
    var deltas = renderDelta()
    var deltaNumber = 1

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
            camera?.position?.set(
                    Math.round(Game.camera.x * 100f) / 100f,
                    Math.round(Game.camera.y * 100f) / 100f,
                    0f)
            camera?.update()
            batch?.projectionMatrix = camera?.combined
            batch?.begin()
            renderThread = Thread.currentThread()
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or
                    if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0)
            val delta = renderDelta()
            deltas += delta
            deltaNumber++
            Game.render(delta)
            batch?.end()
            guicamera?.lookAt(0f, 0f, 0f)
            guicamera?.viewportWidth = camera?.viewportWidth
            guicamera?.viewportHeight = camera?.viewportHeight
            guicamera?.update()
            batch?.projectionMatrix = guicamera?.combined
            batch?.begin()
            Game.gui.render(delta)
            batch?.end()
            atlas.render(delta)
            val currTime = System.currentTimeMillis()
            if (currTime - lastTime > 1000 * 60 * 1.5) {
                if (deltas / deltaNumber > 1f / 30)
                    atlas.quality--
                deltas = delta
                deltaNumber = 1
                lastTime = currTime
            }

            if (Input.keyboard[Key.CMD + Key.W])
                Gdx.app.exit()
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
