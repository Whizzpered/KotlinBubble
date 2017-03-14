package com.whizzpered.bubbleshooter.engine.handler

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter

import com.whizzpered.bubbleshooter.engine.memory.ActionContainer

object Input {
    val keyboard = Keyboard();
    val touch = Array(10) { Touch() }
    val mouse = touch[0]

    init {
        val i = object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                keyboard.keys[keycode] = true
                for (k in Key.values())
                    if (k.keycode == keycode) {
                        keyboard.keyReleasedActions(k)
                        if (keyboard.binding)
                            keyboard.bind = k
                        break
                    }
                return true;
            }

            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (pointer < touch.size) {
                    val t = touch[pointer]
                    t.dx = 0
                    t.dy = 0
                    t.x = screenX
                    t.y = screenY
                    t.bx = screenX
                    t.by = screenY
                    var b = Button.NONE
                    when (button) {
                        Input.Buttons.LEFT -> {
                            t.leftButton = true; b = Button.LEFT
                        }
                        Input.Buttons.MIDDLE -> {
                            t.middleButton = true; b = Button.MIDDLE
                        }
                        Input.Buttons.RIGHT -> {
                            t.rightButton = true; b = Button.RIGHT
                        }
                    }
                    t.update()
                    t.pressedActions(b)
                    t.pressTime = System.currentTimeMillis()
                    return true
                }
                return false
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (pointer < touch.size) {
                    val t = touch[pointer]
                    t.dx = screenX - t.x
                    t.dy = screenY - t.y
                    t.x = screenX
                    t.y = screenY
                    var b = Button.NONE
                    when (button) {
                        Input.Buttons.LEFT -> {
                            t.leftButton = false; b = Button.LEFT
                        }
                        Input.Buttons.MIDDLE -> {
                            t.middleButton = false; b = Button.MIDDLE
                        }
                        Input.Buttons.RIGHT -> {
                            t.rightButton = false; b = Button.RIGHT
                        }
                    }
                    t.update()
                    t.releasedActions(b)
                    t.pressTime = System.currentTimeMillis()
                    return true
                }
                return false
            }

            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                if (pointer < touch.size) {
                    val t = touch[pointer]
                    t.dx = screenX - t.x
                    t.dy = screenY - t.y
                    t.x = screenX
                    t.y = screenY
                    t.draggedActions(null)
                    return true
                }
                return false
            }

            override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                val t = mouse
                t.dx = screenX - t.x
                t.dy = screenY - t.y
                t.x = screenX
                t.y = screenY
                t.movedActions(null)
                return true
            }

            override fun scrolled(amound: Int): Boolean {
                val t = mouse
                t.scroll = amound
                t.scrolledActions(amound)
                return true
            }

            override fun keyUp(keycode: Int): Boolean {
                keyboard.keys[keycode] = false;

                for (k in Key.values())
                    if (k.keycode == keycode) {
                        keyboard.keyReleasedActions(k)
                        break
                    }
                return true;
            }
        }
        Gdx.input.setInputProcessor(i)
    }
}

enum class Key(internal val keycode: Int, val pcname: String = "", val macname: String = pcname) {
    ANY_KEY(-1, "Any key"),
    NUM_0(7, "0"),
    NUM_1(8, "1"),
    NUM_2(9, "2"),
    NUM_3(10, "3"),
    NUM_4(11, "4"),
    NUM_5(12, "5"),
    NUM_6(13, "6"),
    NUM_7(14, "7"),
    NUM_8(15, "8"),
    NUM_9(16, "9"),
    A(29, "A"),
    ALT_LEFT(57, "Left alt", "⌥"),
    ALT_RIGHT(58, "Right alt", "⌥"),
    APOSTROPHE(75, "'"),
    AT(77, "@"),
    B(30, "B"),
    BACK(4, "Back"),
    BACKSLASH(73, "\\"),
    C(31, "C"),
    CALL(5),
    CAMERA(27),
    CLEAR(28),
    COMMA(55, ","),
    D(32, "D"),
    DEL(112, "Delete", "⌦"),
    BACKSPACE(67, "Backspace", "⌫"),
    CENTER(23, "Center"),
    DOWN(20, "Arrow down", "↓"),
    LEFT(21, "Arrow left", "←"),
    RIGHT(22, "Arrow right", "→"),
    UP(19, "Arrow up", "↑"),
    E(33, "E"),
    ENDCALL(6),
    ENTER(66, "Enter", "↩︎"),
    ENVELOPE(65),
    EQUALS(70, "="),
    EXPLORER(64, "Explorer"),
    F(34, "F"),
    FOCUS(80, "Focus"),
    G(35, "G"),
    GRAVE(68, "`"),
    H(36, "H"),
    HEADSETHOOK(79),
    HOME(3, "Home", "↖︎"),
    I(37, "I"),
    J(38, "J"),
    K(39, "K"),
    L(40, "L"),
    LEFT_BRACKET(71, "["),
    M(41, "M"),
    MEDIA_FAST_FORWARD(90),
    MEDIA_NEXT(87),
    MEDIA_PLAY_PAUSE(85),
    MEDIA_PREVIOUS(88),
    MEDIA_REWIND(89),
    MEDIA_STOP(86),
    MENU(82, "Menu"),
    MINUS(69, "-"),
    MUTE(91),
    N(42, "N"),
    NOTIFICATION(83),
    NUM(78, "Num"),
    O(43, "O"),
    P(44, "P"),
    PERIOD(56, "."),
    PLUS(81, "+"),
    POUND(18, "#"),
    POWER(26, "Power", "⏻"),
    Q(45, "Q"),
    R(46, "R"),
    RIGHT_BRACKET(72, "]"),
    S(47, "S"),
    SEARCH(84, "Search"),
    SEMICOLON(74, ";"),
    SHIFT_LEFT(59, "Left shift", "⇧"),
    SHIFT_RIGHT(60, "Right shift", "⇧"),
    SLASH(76, "/"),
    SOFT_LEFT(1, "Soft left"),
    SOFT_RIGHT(2, "Soft right"),
    SPACE(62, "Space"),
    STAR(17, "*"),
    CMD(63, "Cmd", "⌘"),
    T(48, "T"),
    TAB(61, "Tab", "⇥"),
    U(49, "U"),
    UNKNOWN(0, "�"),
    V(50, "V"),
    VOLUME_DOWN(25, "Volume down"),
    VOLUME_UP(24, "Volume up"),
    W(51, "W"),
    X(52, "X"),
    Y(53, "Y"),
    Z(54, "Z"),
    META_ALT_LEFT_ON(16),
    META_ALT_ON(2),
    META_ALT_RIGHT_ON(32),
    META_SHIFT_LEFT_ON(64),
    META_SHIFT_ON(1),
    META_SHIFT_RIGHT_ON(128),
    META_CMD_ON(4),
    CONTROL_LEFT(129, "Left control", "⌃"),
    CONTROL_RIGHT(130, "Right control", "⌃"),
    ESCAPE(131, "Escape", "⎋"),
    END(132, "End", "↘︎"),
    INSERT(133, "Insert"),
    PAGE_UP(92, "Page up", "⇞"),
    PAGE_DOWN(93, "Page down", "⇟"),
    PICTSYMBOLS(94),
    SWITCH_CHARSET(95),
    BUTTON_CIRCLE(255),
    BUTTON_A(96),
    BUTTON_B(97),
    BUTTON_C(98),
    BUTTON_X(99),
    BUTTON_Y(100),
    BUTTON_Z(101),
    BUTTON_L1(102),
    BUTTON_R1(103),
    BUTTON_L2(104),
    BUTTON_R2(105),
    BUTTON_THUMBL(106),
    BUTTON_THUMBR(107),
    BUTTON_START(108),
    BUTTON_SELECT(109),
    BUTTON_MODE(110),
    NUMPAD_0(144, "Numpad 0"),
    NUMPAD_1(145, "Numpad 1"),
    NUMPAD_2(146, "Numpad 2"),
    NUMPAD_3(147, "Numpad 3"),
    NUMPAD_4(148, "Numpad 4"),
    NUMPAD_5(149, "Numpad 5"),
    NUMPAD_6(150, "Numpad 6"),
    NUMPAD_7(151, "Numpad 7"),
    NUMPAD_8(152, "Numpad 8"),
    NUMPAD_9(153, "Numpad 9");

    operator fun plus(key: Key): CombinationOfKeys {
        return CombinationOfKeys(listOf(this, key))
    }
}

data class CombinationOfKeys(internal val keys: List<Key>) {
    operator fun plus(key: Key): CombinationOfKeys {
        return CombinationOfKeys(keys + listOf(key))
    }
}

enum class Button(internal var code: Int) {
    NONE(-1), COMPLEX(-2), LEFT(0), RIGHT(1), MIDDLE(2), BACK(3), FORWARD(4)
}

class Keyboard {
    val keyPressedActions = ActionContainer<Key>()
    val keyReleasedActions = ActionContainer<Key>()

    internal var binding = false
    internal var bind = Key.ANY_KEY
    internal val keys = Array<Boolean>(256) { false }

    private fun isKeyDownProtected(keycode: Int): Boolean {
        if (keycode >= 0 && keycode < 256)
            return keys[keycode]
        return false
    }

    protected fun isKeyDown(keycode: Int, vararg keycodes: Int): Boolean {
        for (k in keycodes) {
            if (isKeyDownProtected(k))
                return true
        }
        return isKeyDownProtected(keycode)
    }

    protected operator fun get(keycode: Int, vararg keycodes: Int): Boolean {
        for (k in keycodes) {
            if (isKeyDownProtected(k))
                return true
        }
        return isKeyDownProtected(keycode)
    }

    fun isKeyDown(key: Key, vararg keys: Key): Boolean {
        for (k in keys) {
            if (isKeyDownProtected(k.keycode))
                return true
        }
        return isKeyDownProtected(key.keycode)
    }

    operator fun get(key: Key, vararg keys: Key): Boolean {
        for (k in keys) {
            if (isKeyDownProtected(k.keycode))
                return true
        }
        return isKeyDownProtected(key.keycode)
    }

    fun isKeyDown(keys: CombinationOfKeys): Boolean {
        keys.keys.forEach { if (!isKeyDown(it.keycode)) return false }
        return true
    }

    operator fun get(keys: CombinationOfKeys): Boolean {
        keys.keys.forEach { if (!isKeyDown(it.keycode)) return false }
        return true
    }


    fun bindKey(): Key {
        bind = Key.ANY_KEY
        binding = true
        while (bind == Key.ANY_KEY) {
        }
        binding = false
        return bind
    }
}

class Touch {
    val pressedActions = ActionContainer<Button>()
    val releasedActions = ActionContainer<Button>()
    val draggedActions = ActionContainer<Any?>()
    val movedActions = ActionContainer<Any?>()
    val scrolledActions = ActionContainer<Int>()

    internal var pressTime = System.currentTimeMillis()
    internal var releaseTime = System.currentTimeMillis()

    var pressed = false
        get() = leftButton || middleButton || rightButton
        private set

    var pressedTime = 0f
        get() = (System.currentTimeMillis() - pressTime) / 1000f
    var releasedTime = 0f
        get() = (System.currentTimeMillis() - releaseTime) / 1000f
        private set
    var leftButton = false
        internal set
    var middleButton = false
        internal set
    var rightButton = false
        internal set
    var button = Button.NONE
        internal set
    var x = 0
        internal set
    var y = 0
        internal set
    var dx = 0
        internal set
    var dy = 0
        internal set
    var bx = 0
        internal set
    var by = 0
        internal set
    var scroll = 0
        internal set

    internal fun update() {
        if (!leftButton && !middleButton && !rightButton)
            button = Button.NONE
        else if (leftButton && !middleButton && !rightButton)
            button = Button.LEFT
        else if (!leftButton && middleButton && !rightButton)
            button = Button.MIDDLE
        else if (!leftButton && !middleButton && rightButton)
            button = Button.RIGHT
        else
            button = Button.COMPLEX
    }
}