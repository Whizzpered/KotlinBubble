package com.whizzpered.bubbleshooter.engine.handler

import kotlin.concurrent.thread

class ThreadHandler(val threads: Int, val task: (ThreadHandler) -> Unit) {
    private val realthreads: Array<Thread?> = Array(threads) { null }

    var dispose = false
        private set

    fun start() {
        synchronized(realthreads) {
            dispose = false
            for (i in 0..threads - 1) {
                val v = realthreads[i]
                if (v != null && v.isAlive) v.stop()

                realthreads[i] = thread {
                    task(this)
                }
            }
        }
    }

    fun stop() {
        dispose = true
    }
}