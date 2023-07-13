package com.nayae.math

import kotlin.math.cos
import kotlin.math.sin

class Quaternion private constructor() {
    val data = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)

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

    inline var w: Float
        get() = data[3]
        set(value) {
            data[3] = value
        }

    companion object {
        fun identity(): Quaternion {
            return Quaternion().apply {
                // M11 Identity
                // M12 Identity
                // M13 Identity
                // M14 Identity

                // M21 Identity
                // M22 Identity
                // M23 Identity
                // M24 Identity

                // M31 Identity
                // M32 Identity
                // M33 Identity
                // M34 Identity

                // M41 Identity
                // M42 Identity
                // M43 Identity
                // M44 Identity
            }
        }

        fun createFromAxisAngle(axis: Vector3, angle: Float): Quaternion {
            return Quaternion().apply {
                val halfAngle = angle * 0.5f
                val sa = sin(halfAngle)
                val ca = cos(halfAngle)

                x = axis.x * sa
                y = axis.y * sa
                z = axis.z * sa
                w = ca
            }
        }
    }
}