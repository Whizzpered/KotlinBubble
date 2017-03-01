package com.whizzpered.bubbleshooter

import com.badlogic.gdx.backends.iosmoe.IOSApplication
import com.badlogic.gdx.backends.iosmoe.IOSApplicationConfiguration
import org.moe.natj.general.Pointer
import com.whizzpered.bubbleshooter.engine.handler.Main

import apple.uikit.c.UIKit

class IOSMoeLauncher protected constructor(peer: Pointer) : IOSApplication.Delegate(peer) {

    override fun createApplication(): IOSApplication {
        val config = IOSApplicationConfiguration()
        config.useAccelerometer = false
        return IOSApplication(Main, config)
    }

    companion object {

        @JvmStatic fun main(argv: Array<String>) {
            UIKit.UIApplicationMain(0, null, null, IOSMoeLauncher::class.java.name)
        }
    }
}
