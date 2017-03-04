package com.whizzpered.bubbleshooter.engine.memory

class ActionContainer<T> {
    private val l: MutableList<Pair<Poolable?, (T) -> Unit>> = mutableListOf()

    fun add(action: (T) -> Unit) {
        synchronized(l) {
            l += Pair(null, action)
        }
    }

    operator fun plusAssign(action: (T) -> Unit) {
        add(action)
    }

    fun add(bind: Poolable, action: (T) -> Unit) {
        synchronized(l) {
            l += Pair(bind, action)
        }
    }

    fun remove(bind: Poolable) {
        synchronized(l) {
            var i = 0
            do
                if (l[i].first == bind) {
                    l.removeAt(i)
                }
            while (++i < l.size)
        }
    }

    operator fun invoke(t: T) {
        synchronized(l) {
            l.forEach { it.second(t) }
        }
    }
}