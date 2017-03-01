package com.whizzpered.bubbleshooter.engine.handler

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx

object Platform {
    var mobile = false
        internal set
    var android = false
        internal set
    var iOS = false
        internal set
    var desktop = false
        internal set

    init {
        val type = Gdx.app.type
        when (type) {
            Application.ApplicationType.Android -> {
                mobile = true
                android = true
            }
            Application.ApplicationType.iOS -> {
                mobile = true
                iOS = true
            }
            Application.ApplicationType.Desktop -> {
                desktop = true
            }
        }
    }
}