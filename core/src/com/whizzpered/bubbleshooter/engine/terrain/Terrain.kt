package com.whizzpered.bubbleshooter.engine.terrain

import com.badlogic.gdx.Gdx
import com.whizzpered.bubbleshooter.engine.handler.AbstractGame
import com.whizzpered.bubbleshooter.engine.handler.Main
import com.whizzpered.bubbleshooter.game.Game

private object EMPTY_TILE : Tile(true, "") {
    override fun render(x: Int, y: Int, terrain: Terrain) {

    }
}

private fun generate(it: Terrain) {
    val terrain = it
    if (it.allTiles.size < 2)
        return
    val r = it.game.random
    var f = Array(it.width) { Array(terrain.height) { r.nextInt(500) } }
    var t = Array(it.width) { Array(terrain.height) { r.nextInt(500) } }
    var cx = it.width / 2;
    var cy = it.height / 2;
    var dir = 0
    for (i in 0..500) {
        if (r.nextInt(5) == 0)
            dir = r.nextInt(4)
        when (dir) {
            0 -> cx++
            1 -> cx--
            2 -> cy++
            3 -> cy--
        }
        cx = it.correct(cx, it.width); cy = it.correct(cy, it.height)
        f[cx][cy] = 500 + r.nextInt(1000);
    }

    /*
    for (x in 0..it.width - 1)
        for (i in 0..0) {
            f[x][i] = -r.nextInt(500)
            f[x][it.height - 1 - i] = -r.nextInt(500)
        }

    for (y in 0..it.height - 1)
        for (i in 0..0) {
            f[i][y] = -r.nextInt(500)
            f[it.width - 1 - i][y] = -r.nextInt(500)
        }
    */

    for (i in 0..3) {
        for (x in 0..it.width - 1)
            for (y in 0..it.height - 1)
                if (r.nextInt(3) == 0)
                    f[x][y] = r.nextInt(500);
        for (x in 0..it.width - 1)
            for (y in 0..it.height - 1) {
                var s = 0;
                for (dx in -1..1)
                    for (dy in -1..1)
                        s += f[it.correct(x + dx, it.width)][it.correct(y + dy, it.height)]
                t[x][y] = s / 9
            }
        val v = f
        f = t
        t = v
    }

    for (y in 0..it.height - 1) {
        for (x in 0..it.width - 1)
            if (f[x][y] < 250) {
                it[x, y] = it.allTiles[0]
            } else {
                it[x, y] = it.allTiles[1]
            }
    }
}

class Terrain(val width: Int, val height: Int, val game: AbstractGame,
              val allTiles: List<Tile>, val generator: (Terrain) -> Unit = ::generate) {


    private val defaultTile = if (allTiles.size > 0) allTiles[0] else EMPTY_TILE
    private val tiles: Array<Array<Tile>> = Array(width) { Array(height) { defaultTile } }

    init {
        generator(this)
    }

    internal fun correct(x: Int, w: Int): Int {
        var cx = x;
        while (cx < 0)
            cx += w;
        while (cx >= w)
            cx -= w;
        return cx;
    }

    operator fun get(x: Int, y: Int): Tile {
        return tiles[correct(x, width)][correct(y, height)];
    }

    operator fun set(x: Int, y: Int, v: Tile) {
        tiles[correct(x, width)][correct(y, height)] = v;
    }

    fun render() {
        val w = Main.camera?.viewportWidth ?: 1f
        val h = Main.camera?.viewportHeight ?: 1f

        for (x in ((-w / 2.0 + Game.camera.x) / defaultTile.size - 3).toInt()..
                ((w / 2.0 + Game.camera.x) / defaultTile.size + 3).toInt())

            for (y in ((-h / 2.0 + Game.camera.y) / defaultTile.size - 3).toInt()..
                    ((h / 2.0 + Game.camera.y) / defaultTile.size + 3).toInt())

                tiles[correct(x, width)][correct(y, height)].render(x, y, this)
    }

    fun tileAt(x: Float, y: Float): Tile {
        val nx = ((x + defaultTile.size / 2) / defaultTile.size).toInt()
        val ny = ((y + defaultTile.size / 2) / defaultTile.size).toInt()
        return tiles[correct(nx, width)][correct(ny, height)];
    }
}