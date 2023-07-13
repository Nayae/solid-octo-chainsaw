package com.nayae.input

import com.nayae.math.Vector2
import org.lwjgl.glfw.GLFW.glfwGetCursorPos
import org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback
import org.lwjgl.system.MemoryStack
import kotlin.properties.Delegates


object Mouse {
    private var windowHandle by Delegates.notNull<Long>()
    private var isFirstFrame = true

    val delta = Vector2()
    val position = Vector2()

    fun init(windowHandle: Long) {
        this.windowHandle = windowHandle

        glfwSetMouseButtonCallback(windowHandle) { _, button, action, mods ->

        }
    }

    fun newFrame() {
        MemoryStack.stackPush().use { stack ->
            val pX = stack.mallocDouble(1)
            val pY = stack.mallocDouble(1)
            glfwGetCursorPos(windowHandle, pX, pY)

            if (!isFirstFrame) {
                delta.x = (pX.get(0) - position.x).toFloat()
                delta.y = (pY.get(0) - position.y).toFloat()
            }

            position.x = pX.get(0).toFloat()
            position.y = pY.get(0).toFloat()
        }

        isFirstFrame = false
    }

    fun endFrame() {
        delta.x = 0.0f
        delta.y = 0.0f
    }
}