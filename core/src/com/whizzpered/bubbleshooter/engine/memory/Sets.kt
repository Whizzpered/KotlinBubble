package com.whizzpered.bubbleshooter.engine.memory

import java.util.*

abstract class StrangeCollection<T>(val size: Int) {
    abstract fun forEach(eacher: (T) -> Unit)
}

private fun <T> countSize(col: StrangeCollection<T>, vararg cols: StrangeCollection<T>): Int {
    val v = arrayOf(col, *cols)
    var i = 0
    v.forEach { i += it.size }
    return i
}

class Mix<T> : StrangeCollection<T> {
    private val arr: Array<StrangeCollection<T>>

    constructor(col: StrangeCollection<T>, vararg cols: StrangeCollection<T>) : super(countSize(col, *cols)) {
        val v = arrayOf(col, *cols)
        arr = v
    }

    override fun forEach(eacher: (T) -> Unit) {
        arr.forEach { it.forEach { eacher(it) } }
    }

    fun sortedWith(by: (T, T) -> Int): MixSortedBy<T> {
        return MixSortedBy(arr, size, by)
    }
}

class MixSortedBy<T> : StrangeCollection<T> {
    private val arr: Array<StrangeCollection<T>>
    private val trueArr: Array<T?>
    private val by: Comparator<T?>

    internal constructor(v: Array<StrangeCollection<T>>, s: Int, b: (T, T) -> Int) : super(s) {
        arr = v
        by = object : Comparator<T?> {
            override fun compare(p0: T?, p1: T?): Int {
                if (p0 == null && p1 == null)
                    return 0
                else if (p1 == null)
                    return -1
                else if (p0 == null)
                    return 1
                else
                    try {
                        return b(p0, p1)
                    } catch (e: ClassCastException) {

                    }
                return 0
            }
        }
        @Suppress("UNCHECKED_CAST")
        trueArr = Array<Any?>(size, { null }) as Array<T?>
    }

    override fun forEach(eacher: (T) -> Unit) {
        var i = 0
        arr.forEach {
            it.forEach {
                if (i < size)
                    trueArr[i++] = it
            }
        }
        for (i in i..size - 1) {
            trueArr[i] = null
        }
        trueArr.sortWith(by)
        trueArr.forEach{ if (it != null) eacher(it) }
    }
}

class UnpureSet<T>(size: Int) : StrangeCollection<T>(size) {
    internal val content: Array<Any?>
    var range = 0
    var curar = 0

    init {
        content = Array(size) { null as Any? }
    }

    fun add(t: T): Boolean {
        var i = 0
        do {
            if (content[i] == t)
                return true
            else if (content[i] == null) {
                content[i] = t
                range = Math.min(Math.max(i + 1, range), size)
                return true
            }
        } while (++i < size)
        return false
    }

    operator fun plusAssign(t: T) {
        add(t)
    }

    operator fun minusAssign(t: T) {
        remove(t)
    }

    fun contains(t: T): Boolean {
        var i = 0
        do {
            if (content[i] == t) {
                return true
            }
        } while (++i < range)
        return false
    }


    fun remove(t: T): Boolean {
        var i = 0
        do {
            if (content[i] == t) {
                content[i] = null
            }
        } while (++i < range)
        return false
    }

    override fun forEach(eacher: (T) -> Unit) {
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

    class SortedBuffer<T> internal constructor(val unpureset: UnpureSet<T>, comparator: Comparator<T>) {

        val arr: Array<Any?> = Array(unpureset.size) { null }

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

class Context<T : Poolable>(size: Int,
                            private val handleAdding: (T) -> Unit = {},
                            private val handleRemoving: (T) -> Unit = {}) : StrangeCollection<T>(size) {
    val pools = mutableListOf<Pair<AbstractPoolConfiguration<out T>, AbstractPool<out T>>>()

    infix fun <G : T> new(p: PoolConfiguration<G>): G {
        synchronized(pools) {
            pools.forEach { if (it.first == p) return it.second.lock() as G }
            val pc = p.pool
            pools += Pair(p, pc)
            return pc()
        }
    }

    private val content = UnpureSet<T>(size)

    fun add(t: T) {
        handleAdding(t)
        if (!content.add(t)) {
            System.err.println("Context ${this} overflowed!!!")
            t.unlock()
        }
    }

    fun remove(t: T) {
        handleRemoving(t)
        t.unlock()
        content.remove(t)
    }

    operator fun plusAssign(t: T) {
        add(t)
    }

    operator fun minusAssign(t: T) {
        remove(t)
    }

    override fun forEach(eacher: (T) -> Unit) {
        content.forEach { eacher(it) }
    }
}