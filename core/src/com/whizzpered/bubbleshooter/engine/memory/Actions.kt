package com.whizzpered.bubbleshooter.engine.memory

import java.lang.ref.WeakReference

class ActionContainer<T> {
    private val l: MutableList<Pair<WeakReference<Any>?, (T) -> Unit>> = mutableListOf()

    fun add(action: (T) -> Unit) {
        synchronized(l) {
            l += Pair(null, action)
        }
    }

    operator fun plusAssign(action: (T) -> Unit) {
        add(action)
    }

    fun add(bind: Any, action: (T) -> Unit) {
        synchronized(l) {
            l += Pair(WeakReference(bind), action)
        }
    }

    fun remove(bind: Any) {
        synchronized(l) {
            var i = 0
            do
                if (l[i].first?.get() == bind) {
                    l.removeAt(i)
                    i--
                }
            while (++i < l.size)
        }
    }

    operator fun invoke(t: T) {
        synchronized(l) {
            run {
                var i = 0
                do {
                    val it = l[i]
                    if (it.first == null)
                        it.second(t)
                    else {
                        val v = it.first
                        if (v != null) {
                            if (v.get() != null)
                                it.second(t)
                            else {
                                l.removeAt(i)
                                i--
                            }
                        }
                    }
                } while (++i < l.size)
            }
        }
    }
}