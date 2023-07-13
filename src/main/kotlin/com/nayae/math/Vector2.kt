package com.nayae.math

class Vector2() {
    constructor(value: Float) : this(value, value)
    constructor(x: Float, y: Float) : this() {
        data[0] = x
        data[1] = y
    }

    val data = floatArrayOf(0.0f, 0.0f)

    inline var x: Float
        get() = data[0]
        set(value) {
            data[0] = value
        }

    inline var y: Float
        get() = data[1]
        set(value) {
            data[1] = value
        }

    override fun toString(): String {
        return "{x: $x, y: $y}"
    }

    operator fun plusAssign(other: Vector2) {
        x += other.x
        y += other.y
    }

    companion object {
        val zero: Vector2
            get() = Vector2()

        val one: Vector2
            get() = Vector2().apply {
                x = 1.0f
                y = 1.0f
            }

        val unitX: Vector2
            get() = Vector2().apply {
                x = 1.0f
            }

        val unitY: Vector2
            get() = Vector2().apply {
                y = 1.0f
            }
    }
}