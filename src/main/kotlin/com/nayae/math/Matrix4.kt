package com.nayae.math

import kotlin.math.cos
import kotlin.math.sin

class Matrix4 private constructor() {
    val data = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f
    )

    inline var m11: Float
        get() = data[0]
        set(value) {
            data[0] = value
        }

    inline var m12: Float
        get() = data[1]
        set(value) {
            data[1] = value
        }

    inline var m13: Float
        get() = data[2]
        set(value) {
            data[2] = value
        }

    inline var m14: Float
        get() = data[3]
        set(value) {
            data[3] = value
        }

    inline var m21: Float
        get() = data[4]
        set(value) {
            data[4] = value
        }

    inline var m22: Float
        get() = data[5]
        set(value) {
            data[5] = value
        }

    inline var m23: Float
        get() = data[6]
        set(value) {
            data[6] = value
        }

    inline var m24: Float
        get() = data[7]
        set(value) {
            data[7] = value
        }

    inline var m31: Float
        get() = data[8]
        set(value) {
            data[8] = value
        }

    inline var m32: Float
        get() = data[9]
        set(value) {
            data[9] = value
        }

    inline var m33: Float
        get() = data[10]
        set(value) {
            data[10] = value
        }

    inline var m34: Float
        get() = data[11]
        set(value) {
            data[11] = value
        }

    inline var m41: Float
        get() = data[12]
        set(value) {
            data[12] = value
        }

    inline var m42: Float
        get() = data[13]
        set(value) {
            data[13] = value
        }

    inline var m43: Float
        get() = data[14]
        set(value) {
            data[14] = value
        }

    inline var m44: Float
        get() = data[15]
        set(value) {
            data[15] = value
        }

    operator fun times(other: Matrix4): Matrix4 {
        return Matrix4().apply {
            m11 =
                this@Matrix4.m11 * other.m11 + this@Matrix4.m12 * other.m21 + this@Matrix4.m13 * other.m31 + this@Matrix4.m14 * other.m41
            m12 =
                this@Matrix4.m11 * other.m12 + this@Matrix4.m12 * other.m22 + this@Matrix4.m13 * other.m32 + this@Matrix4.m14 * other.m42
            m13 =
                this@Matrix4.m11 * other.m13 + this@Matrix4.m12 * other.m23 + this@Matrix4.m13 * other.m33 + this@Matrix4.m14 * other.m43
            m14 =
                this@Matrix4.m11 * other.m14 + this@Matrix4.m12 * other.m24 + this@Matrix4.m13 * other.m34 + this@Matrix4.m14 * other.m44

            m21 =
                this@Matrix4.m21 * other.m11 + this@Matrix4.m22 * other.m21 + this@Matrix4.m23 * other.m31 + this@Matrix4.m24 * other.m41
            m22 =
                this@Matrix4.m21 * other.m12 + this@Matrix4.m22 * other.m22 + this@Matrix4.m23 * other.m32 + this@Matrix4.m24 * other.m42
            m23 =
                this@Matrix4.m21 * other.m13 + this@Matrix4.m22 * other.m23 + this@Matrix4.m23 * other.m33 + this@Matrix4.m24 * other.m43
            m24 =
                this@Matrix4.m21 * other.m14 + this@Matrix4.m22 * other.m24 + this@Matrix4.m23 * other.m34 + this@Matrix4.m24 * other.m44

            m31 =
                this@Matrix4.m31 * other.m11 + this@Matrix4.m32 * other.m21 + this@Matrix4.m33 * other.m31 + this@Matrix4.m34 * other.m41
            m32 =
                this@Matrix4.m31 * other.m12 + this@Matrix4.m32 * other.m22 + this@Matrix4.m33 * other.m32 + this@Matrix4.m34 * other.m42
            m33 =
                this@Matrix4.m31 * other.m13 + this@Matrix4.m32 * other.m23 + this@Matrix4.m33 * other.m33 + this@Matrix4.m34 * other.m43
            m34 =
                this@Matrix4.m31 * other.m14 + this@Matrix4.m32 * other.m24 + this@Matrix4.m33 * other.m34 + this@Matrix4.m34 * other.m44

            m41 =
                this@Matrix4.m41 * other.m11 + this@Matrix4.m42 * other.m21 + this@Matrix4.m43 * other.m31 + this@Matrix4.m44 * other.m41
            m42 =
                this@Matrix4.m41 * other.m12 + this@Matrix4.m42 * other.m22 + this@Matrix4.m43 * other.m32 + this@Matrix4.m44 * other.m42
            m43 =
                this@Matrix4.m41 * other.m13 + this@Matrix4.m42 * other.m23 + this@Matrix4.m43 * other.m33 + this@Matrix4.m44 * other.m43
            m44 =
                this@Matrix4.m41 * other.m14 + this@Matrix4.m42 * other.m24 + this@Matrix4.m43 * other.m34 + this@Matrix4.m44 * other.m44
        }
    }

    companion object {
        fun identity(): Matrix4 {
            return Matrix4().apply {
                // m11
                // m12
                // m13
                // m14

                // m21
                // m22
                // m23
                // m24

                // m31
                // m32
                // m33
                // m34

                // m41
                // m42
                // m43
                // m44
            }
        }

        fun createOrthographic(width: Float, height: Float, zNearPlane: Float, zFarPlane: Float): Matrix4 {
            return Matrix4().apply {
                m11 = 2.0f / width
                // m12
                // m13
                // m14

                // m21
                m22 = 2.0f / height
                // m23
                // m24

                // m31
                // m32
                m33 = 1.0f / (zNearPlane - zFarPlane)
                // m34

                // m41
                // m42
                m43 = zNearPlane / (zNearPlane - zFarPlane)
                // m44
            }
        }

        fun createTranslation(vec: Vector3): Matrix4 {
            return createTranslation(vec.x, vec.y, vec.z)
        }

        fun createTranslation(x: Float, y: Float, z: Float): Matrix4 {
            return Matrix4().apply {
                // m11
                // m12
                // m13
                // m14

                // m21
                // m22
                // m23
                // m24

                // m31
                // m32
                // m33
                // m34

                m41 = x
                m42 = y
                m43 = z
                // m44
            }
        }

        fun createFromAxisAngle(axis: Vector3, angle: Float): Matrix4 {
            return Matrix4().apply {
                val x = axis.x
                val y = axis.y
                val z = axis.z

                val sa = sin(angle)
                val ca = cos(angle)

                val xx = x * x
                val yy = y * y
                val zz = z * z

                val xy = x * y
                val xz = x * z
                val yz = y * z

                m11 = xx + ca * (1.0f - xx)
                m12 = xy - ca * xy + sa * z
                m13 = xz - ca * xz - sa * y
                // m14

                m21 = xy - ca * xy - sa * z
                m22 = yy + ca * (1.0f - yy)
                m23 = yz - ca * yz + sa * x
                // m24

                m31 = xz - ca * xz + sa * y
                m32 = yz - ca * yz - sa * x
                m33 = zz + ca * (1.0f - zz)
                // m34

                // m41
                // m42
                // m43
                // m44 = 1.0f
            }
        }

        fun createFromQuaternion(quaternion: Quaternion): Matrix4 {
            return Matrix4().apply {
                val xx = quaternion.x * quaternion.x
                val yy = quaternion.y * quaternion.y
                val zz = quaternion.z * quaternion.z

                val xy = quaternion.x * quaternion.x
                val wz = quaternion.z * quaternion.w
                val xz = quaternion.z * quaternion.x
                val wy = quaternion.y * quaternion.w
                val yz = quaternion.y * quaternion.z
                val wx = quaternion.x * quaternion.w

                m11 = 1.0f - 2.0f * (yy + zz)
                m12 = 2.0f * (xy + wz)
                m13 = 2.0f * (xz - wy)
                // m14

                m21 = 2.0f * (xy - wz)
                m22 = 1.0f - 2.0f * (zz + xx)
                m23 = 2.0f * (yz + wx)
                // m24

                m31 = 2.0f * (xz + wy)
                m32 = 2.0f * (yz - wx)
                m33 = 1.0f - 2.0f * (yy + xx)
                // m34

                // m41
                // m42
                // m43
                // m44
            }
        }
    }
}