package com.whizzpered.bubbleshooter.engine.memory

import java.util.*

class UnpureSet<T>(val length: Int) {

    internal val content: Array<Any?>
    var range = 0
    var curar = 0

    init {
        content = Array(length) { null as Any? }
    }

    fun add(t: T): Boolean {
        var i = 0
        do {
            if (content[i] == t)
                return true
            else if (content[i] == null) {
                content[i] = t
                range = Math.min(Math.max(i + 1, range), length)
                return true
            }
        } while (++i < length)
        return false
    }

    operator fun plusAssign(t: T) {
        add(t)
    }

    operator fun minusAssign(t: T) {
        remove(t)
    }

    fun remove(t: T): Boolean {
        var i = 0
        do {
            if (content[i] == t) {
                content[i] = null
                return true
            }
        } while (++i < range)
        return false
    }

    fun forEach(eacher: (T) -> Unit) {
        var i = 0
        do {
            val t = content[i]
            if (t != null)
                try {
                    @Suppress("UNCHECKED_CAST")
                    eacher(t as T)
                } catch (e: Exception) {
                }
        } while (++i < range)
    }

    fun sorted(comparator: Comparator<T>) = SortedBuffer<T>(this, comparator)

    fun sorted(comparator: (a: T, b: T) -> Int) = SortedBuffer<T>(this,
            object : Comparator<T> {
                override fun compare(p0: T, p1: T): Int {
                    return comparator(p0, p1)
                }
            }
    )

    class SortedBuffer<T> internal constructor(val unpureset: UnpureSet<T>, comparator: Comparator<T>) {

        val arr: Array<Any?> = Array(unpureset.length) { null }

        val comp = object : Comparator<Any?> {
            override fun compare(p0: Any?, p1: Any?): Int {
                if (p0 == null && p1 == null)
                    return 0
                else if (p1 == null)
                    return -1
                else if (p0 == null)
                    return 1
                else
                    try {
                        @Suppress("UNCHECKED_CAST")
                        return comparator.compare(p0 as T?, p1 as T?)
                    } catch (e: ClassCastException) {

                    }
                return 0
            }
        }

        init {

        }


        fun forEach(eacher: (T) -> Unit) {
            // synchronized(arr) {
            var range = 0
            run {
                var i = 0
                do {
                    arr[i] = unpureset.content[i]
                } while (++i < unpureset.range)
                range = unpureset.range
            }
            try {
                arr.sortWith(comp)
            } catch (e: IllegalArgumentException) {

            }
            run {
                var i = 0
                do {
                    val t = arr[i]
                    if (t != null)
                        try {
                            @Suppress("UNCHECKED_CAST")
                            eacher(t as T)
                        } catch (e: ClassCastException) {
                        }
                } while (++i < range)
            }
            //}
        }
    }
}

class Context<T : Poolable>(val length: Int) {
    val pools = mutableListOf<Pair<AbstractPoolConfiguration<out T>, AbstractPool<out T>>>()

    infix fun <G : T> new(p: PoolConfiguration<G>): G {
        synchronized(pools) {
            pools.forEach { if (it.first == p) return it.second.lock() as G }

            val pc = p.pool
            pools += Pair(p, pc)
            return pc()
        }
    }

    private val content = UnpureSet<T>(length)

    fun add(t: T) {
        if (!content.add(t)) {
            System.err.println("Context ${this} overflowed!!!")
            t.unlock()
        }
    }

    fun sorted(comparator: Comparator<T>) = content.sorted(comparator)

    fun sorted(comparator: (T, T) -> Int) = content.sorted(comparator)

    fun remove(t: T) {
        t.unlock()
        content.remove(t)
    }

    operator fun plusAssign(t: T) {
        add(t)
    }

    operator fun minusAssign(t: T) {
        remove(t)
    }

    fun forEach(eacher: (T) -> Unit) {
        content.forEach { eacher(it) }
    }
}