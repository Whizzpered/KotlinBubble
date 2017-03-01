package com.whizzpered.bubbleshooter.engine.entities

abstract class Effect {
    abstract fun act(owner: Entity, delta: Float)
}

open class StatusEffect(val statementName: String,
                        val apply: (Float) -> Float,
                        val idle: (Float) -> Float) : Effect() {
    override fun act(owner: Entity, delta: Float) {}
}

class RatioEffect(statementName: String, val ratio: Float) :
        StatusEffect(statementName, { it * ratio }, { it / ratio })

class IncreasingEffect(statementName: String, val ratio: Float) :
        StatusEffect(statementName, { it + ratio }, { it - ratio })