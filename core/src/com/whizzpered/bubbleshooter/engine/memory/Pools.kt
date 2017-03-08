package com.whizzpered.bubbleshooter.engine.memory

import java.lang.ref.WeakReference

interface Poolable {
    fun getPoolConfigurator(): AbstractPoolConfiguration<out Poolable>
    val pool: AbstractPool<out Poolable>
    val actionContainers: MutableList<ActionContainer<*>>
    fun init()
    fun lock()
    fun reset()
    fun unlock() {
        pool.unlock(this)
        actionContainers.forEach { it.remove(this) }
    }
}

abstract class AbstractPool<T : Poolable>() {
    abstract fun lock(): T
    internal abstract fun unlock(obj: Any)
    operator fun invoke(): T {
        return lock()
    }
}

abstract class AbstractPoolConfiguration<T : Poolable> {
    abstract val pool: AbstractPool<T>
}

open class PoolConfiguration<T : Poolable> : AbstractPoolConfiguration<T> {
    val fabricator: (AbstractPool<T>) -> T
    private val limit: Int

    constructor(limit: Int, fabricator: (AbstractPool<T>) -> T) {
        this.limit = limit
        this.fabricator = fabricator
    }

    override val pool: AbstractPool<T>
        get() {
            return Pool(this)
        }

    class Pool<T : Poolable> internal constructor(val parent: PoolConfiguration<T>) : AbstractPool<T>() {
        private val stack = java.util.Stack<WeakReference<T>>()

        override fun lock(): T {
            while (true) {
                if (stack.empty()) {
                    val v = parent.fabricator(this)
                    v.init()
                    v.lock()
                    return v
                } else {
                    synchronized(stack) {
                        val v = stack.pop()
                        val g = v.get()
                        if (g != null) {
                            g.lock()
                            return g
                        }
                    }
                }
            }
        }

        override fun unlock(obj: Any) {
            try {
                if (stack.size < parent.limit)
                    stack.push(WeakReference(obj as T))
                (obj as T).reset()
            } catch (e: ClassCastException) {

            }
        }
    }
}