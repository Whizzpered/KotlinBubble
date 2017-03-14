package com.whizzpered.bubbleshooter.engine.utils

import com.whizzpered.bubbleshooter.engine.graphics.Point

private enum class Dir(val addition: Int, val x: Int, val y: Int) {
    UP(1, 0, 1),
    RIGHT(0, 1, 0),
    DOWN(1, 0, -1),
    LEFT(0, -1, 0)
}

fun spiral(lambda: (x: Int, y: Int) -> Boolean) {
    var i = 0
    var x = 0
    var y = 0
    var length = 0
    val dirs = Dir.values()
    var crdi = -1
    do {
        crdi++;
        if (crdi == dirs.size)
            crdi = 0
        val curdir = dirs[crdi]

        length += curdir.addition
        x += length * curdir.x
        y += length * curdir.y
        val b = lambda(x, y)
    } while (b)
}