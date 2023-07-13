package com.nayae.math

class Vector3 private constructor() {
    val data = floatArrayOf(0.0f, 0.0f, 0.0f)

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
    inline var z: Float
        get() = data[2]
        set(value) {
            data[2] = value
        }

    companion object {
        val zero: Vector3
            get() = Vector3()

        val one: Vector3
            get() = Vector3().apply {
                x = 1.0f
                y = 1.0f
                z = 1.0f
            }

        val unitX: Vector3
            get() = Vector3().apply {
                x = 1.0f
            }

        val unitY: Vector3
            get() = Vector3().apply {
                y = 1.0f
            }

        val unitZ: Vector3
            get() = Vector3().apply {
                z = 1.0f
            }
    }
}