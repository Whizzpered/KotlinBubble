package com.whizzpered.bubbleshooter.game

import com.whizzpered.bubbleshooter.engine.terrain.Tile

enum class Tiles(val tile: Tile) {
    DIRTH(Tile(true, "terrain/grass")),
    GRASS(Tile(true, "terrain/other_grass"))
}