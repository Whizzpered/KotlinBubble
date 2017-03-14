package com.whizzpered.bubbleshooter.game

import com.whizzpered.bubbleshooter.engine.entities.Entity
import com.whizzpered.bubbleshooter.engine.handler.AbstractGame
import com.whizzpered.bubbleshooter.engine.memory.Context
import com.whizzpered.bubbleshooter.engine.memory.Mix
import com.whizzpered.bubbleshooter.engine.memory.makeListFrom
import com.whizzpered.bubbleshooter.engine.terrain.Terrain
import com.whizzpered.bubbleshooter.engine.terrain.Tile
import com.whizzpered.bubbleshooter.game.creatures.Enemy
import com.whizzpered.bubbleshooter.game.creatures.Hero

object Game : AbstractGame() {
    val context = Context<Entity>(100,
            handleAdding = {
                it.game = this
            },
            handleRemoving = {

            }
    )
    val allTiles = makeListFrom<Tile, Tiles>(Tiles.values()) { it.tile }
    val terrain: Terrain = Terrain(
            size = 50,
            width = 64,
            height = 64,
            game = this,
            allTiles = allTiles
    )
    internal val renderMix = Mix(context, terrain).sortedWith {
        a, b ->
        -(a as Entity).position.y.compareTo((b as Entity).position.y)
    }

    override fun init() {
        for (i in 0..50) {
            val enemy = context new Enemy.config
            enemy.position.set(
                    x = (random.nextFloat() - .5f) * 10,
                    y = (random.nextFloat() - .5f) * 10
            )
            enemy.target.set(
                    x = (random.nextFloat() - .5f) * 10,
                    y = (random.nextFloat() - .5f) * 10
            )
            context += enemy
        }
        context += context new Hero.config
        Tiles.values()
        makeGUI()
    }

    override fun act(delta: Float) {
        context.forEach { it.act(delta) }
    }

    override fun render(delta: Float) {
        terrain.render()
        renderMix.forEach { it.render(delta) }
    }

    override fun ai(delta: Float) {
        context.forEach { it.ai(delta) }
    }
}