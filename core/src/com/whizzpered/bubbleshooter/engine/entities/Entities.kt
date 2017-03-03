package com.whizzpered.bubbleshooter.engine.entities

import com.whizzpered.bubbleshooter.engine.graphics.MutablePoint
import com.whizzpered.bubbleshooter.engine.handler.AbstractGame
import com.whizzpered.bubbleshooter.engine.handler.EMPTY_GAME
import com.whizzpered.bubbleshooter.engine.memory.*
import java.lang.reflect.Modifier

object EMPTY_ENTITY_POOL_CONFIG : PoolConfiguration<EMPTY_ENTITY>(1, { EMPTY_ENTITY })

object EMPTY_ENTITY_POOL : AbstractPool<EMPTY_ENTITY>() {
    override fun unlock(obj: Any) {
    }

    override fun lock(): EMPTY_ENTITY {
        return EMPTY_ENTITY
    }
}

object EMPTY_ENTITY : Entity() {
    override val pool: AbstractPool<out Poolable>
        get() = EMPTY_ENTITY_POOL

    override fun getPoolConfigurator(): AbstractPoolConfiguration<EMPTY_ENTITY> {
        return EMPTY_ENTITY_POOL_CONFIG
    }
    override val actionContainers = mutableListOf<ActionContainer<*>>()
    val EMPTY_STATEMENT = createStatement(0f)
    val EMPTY_RELATED_VARIABLE = EMPTY_STATEMENT % 0
    val EMPTY_RELATED_OBJECT = object : RelatedObject(this) {
    }

    override fun initHandlers() {

    }

    override fun lock() {

    }

    override fun reset() {

    }

    override fun unlock() {

    }
}

abstract class Entity : Poolable {
    val position = MutablePoint(0f, 0f)
    val velocity = MutablePoint(0f, 0f)
    val acceleration = MutablePoint(0f, 0f)
    var angle = 0f
    var radius = 0f
    var game: AbstractGame = EMPTY_GAME
    var solid = false
    private val stats: MutableList<Statement> = mutableListOf()
    private val rvars: MutableList<RelatedVariable> = mutableListOf()
    private val objects: MutableList<RelatedObject> = mutableListOf()
    internal val effects = UnpureSet<Effect>(32)
    override val actionContainers = mutableListOf<ActionContainer<*>>()
    var initialized = false
        private set

    var actHandlers: Set<(Float) -> Unit> = setOf()
        private set
    var renderHandlers: Set<(Float) -> Unit> = setOf()
        private set
    var aiHandlers: Set<(Float) -> Unit> = setOf()
        private set
    var initialActHandlers: MutableSet<(Float) -> Unit> = mutableSetOf()
        private set
    var initialRenderHandlers: MutableSet<(Float) -> Unit> = mutableSetOf()
        private set
    var initialAiHandlers: MutableSet<(Float) -> Unit> = mutableSetOf()
        private set

    fun handler(handler: (Float) -> Unit) = handler

    fun act(delta: Float) {
        actHandlers.forEach { it(delta) }
    }

    fun render(delta: Float) {
        renderHandlers.forEach { it(delta) }
    }

    fun ai(delta: Float) {
        aiHandlers.forEach { it(delta) }
    }

    fun applyEffect(effect: Effect) {
        effects += effect
    }

    operator fun plusAssign(effect: Effect) {
        applyEffect(effect)
    }

    fun removeEffect(effect: Effect) {
        effects -= effect
    }

    operator fun minusAssign(effect: Effect) {
        removeEffect(effect)
    }

    protected fun createStatement(defaultValue: Float): Statement {
        if (initialized) return EMPTY_ENTITY.EMPTY_STATEMENT

        val s = Statement(this)
        s.originalValue = defaultValue
        s.initialValue = defaultValue
        stats += s
        return s
    }

    protected infix fun Statement.relate(defaultValue: Float): RelatedVariable {
        if (initialized) return EMPTY_ENTITY.EMPTY_RELATED_VARIABLE

        val s = RelatedVariable(this.owner, this)
        s.value = defaultValue
        s.initialValue = defaultValue
        rvars += s
        return s
    }

    protected operator fun Statement.mod(value: Float): RelatedVariable {
        if (initialized) return EMPTY_ENTITY.EMPTY_RELATED_VARIABLE

        val s = RelatedVariable(this.owner, this)
        s.realValue = value / 100f
        s.initialValue = value / 100f
        rvars += s
        return s
    }

    protected operator fun Statement.mod(value: Int): RelatedVariable = this % value.toFloat()

    protected operator fun Statement.mod(value: Double): RelatedVariable = this % value.toFloat()

    private fun entitle() {
        entitle(this, "")
    }

    abstract fun initHandlers()

    override final fun init() {
        initHandlers()
        initialized = true
        actHandlers = initialActHandlers.toSet()
        initialActHandlers.clear()
        renderHandlers = initialRenderHandlers.toSet()
        initialRenderHandlers.clear()
        aiHandlers = initialAiHandlers.toSet()
        initialAiHandlers.clear()
    }

    private fun entitle(scan: Any, str: String) {
        val name = scan.javaClass.name
        val fields = this.javaClass.declaredFields
        for (f in fields)
            if (f != null) {
                val name = f.name
                if (f.modifiers == Modifier.FINAL && f.name != "owner")
                    when (f.type) {
                        Statement::class.java -> {
                            val s = f.get(scan) as Statement
                            s.name = str + f.name
                        }
                        RelatedVariable::class.java -> {
                            val v = f.get(scan) as RelatedVariable
                            v.name = str + f.name
                        }
                        RelatedObject::class.java ->
                            entitle(f.get(scan), "${str}${f.name}::")
                    }
            }
    }

    class Statement internal constructor(val owner: Entity) {
        internal var initialValue = 0f

        var name: String? = null

        var originalValue = 1f

        var value = 0f
            private set
            get() {
                if (!updated) {
                    var v = originalValue
                    owner.effects.forEach {
                        if (it is StatusEffect && it.statementName == name)
                            v = it.apply(v)
                    }
                    lazyValue = v
                    updated = true
                }
                return lazyValue
            }

        private var lazyValue = 0f

        private var updated = false
    }

    class RelatedVariable internal constructor(val owner: Entity, val stat: Statement) {
        internal var initialValue = 0f

        var name: String? = null

        internal var realValue = 1f

        var value: Float
            get() {
                return realValue * stat.value
            }
            set(value) {
                realValue = Math.max(0f, Math.min(1f, value / stat.value))
            }
    }

    abstract class RelatedObject(val owner: Entity) {

        init {
            if (owner.initialized)
                owner.objects += this
        }

        var actHandlers: Set<(Float) -> Unit> = setOf()
            private set
        var renderHandlers: Set<(Float) -> Unit> = setOf()
            private set
        var aiHandlers: Set<(Float) -> Unit> = setOf()
            private set
        var initialActHandlers: MutableSet<(Float) -> Unit> = mutableSetOf()
            private set
        var initialRenderHandlers: MutableSet<(Float) -> Unit> = mutableSetOf()
            private set
        var initialAiHandlers: MutableSet<(Float) -> Unit> = mutableSetOf()
            private set

        internal fun act(delta: Float) {
            actHandlers.forEach { it(delta) }
        }

        internal fun render(delta: Float) {
            renderHandlers.forEach { it(delta) }
        }

        internal fun ai(delta: Float) {
            aiHandlers.forEach { it(delta) }
        }
    }
}