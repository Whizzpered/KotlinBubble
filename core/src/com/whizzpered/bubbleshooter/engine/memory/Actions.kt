package com.whizzpered.bubbleshooter.engine.memory

class ActionContainer<T> {
	private val l: MutableList<(T) -> Unit> = mutableListOf()

	fun add(action: (T) -> Unit) {
		synchronized(l) {
			l += action
		}
	}
	
	operator fun invoke(t: T) {
		synchronized(l) {
			l.forEach { it(t) }
		}
	}
}