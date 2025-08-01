package com.lollipop.play.core.helper

enum class JoystickDirection(
    val dx: Int, val dy: Int
) {

    LEFT(dx = -1, dy = 0),
    UP(dx = 0, dy = -1),
    RIGHT(dx = 1, dy = 0),
    DOWN(dx = 0, dy = 1);

    private val contentValue = "${name}[$dx, $dy]"

    override fun toString(): String {
        return contentValue
    }

}