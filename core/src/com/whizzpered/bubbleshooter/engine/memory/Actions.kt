package com.whizzpered.bubbleshooter.engine.memory

class ActionContainer<T> {
	private val l: MutableList<(T) -> Void> = mutableListOf()

	fun add(action: (T) -> Void) {
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