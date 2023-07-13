package com.nayae.math

object MathF {
    val PI = Math.PI.toFloat()

    fun degreesToRadians(degrees: Float): Float {
        return degrees * (PI / 180.0f)
    }
}