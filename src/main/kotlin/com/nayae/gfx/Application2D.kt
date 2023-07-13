package com.nayae.gfx

import com.nayae.input.Mouse
import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import kotlin.properties.Delegates

abstract class Application2D {
    private var windowHandle by Delegates.notNull<Long>()

    private val imGuiGlfw = ImGuiImplGlfw()
    private val imGuiGl3 = ImGuiImplGl3()

    protected open fun beforeRun() = Unit
    protected abstract fun onUpdate()
    protected abstract fun onRender()
    protected open fun afterRun() = Unit

    private fun start() {
        initWindow()
        initContext()

        beforeRun()
        render()
        afterRun()

        dispose()
    }

    private fun render() {
        glClearColor(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1.0f)

        while (!glfwWindowShouldClose(windowHandle)) {
            onUpdate()
            
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            imGuiGlfw.newFrame()
            ImGui.newFrame()

            Mouse.newFrame()

            onRender()

            Mouse.endFrame()

            ImGui.render()
            imGuiGl3.renderDrawData(ImGui.getDrawData())

            glfwSwapBuffers(windowHandle)
            glfwPollEvents()
        }
    }

    private fun initWindow() {
        GLFWErrorCallback.createPrint(System.out).set()

        if (!glfwInit()) {
            throw IllegalStateException("Failed to initialize GLFW")
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

        windowHandle = glfwCreateWindow(1280, 768, "Hello, World!", MemoryUtil.NULL, MemoryUtil.NULL)
        if (windowHandle == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create GLFW window")
        }

        glfwSetKeyCallback(windowHandle) { _, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowHandle, true)
            }
        }

        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)

            glfwGetWindowSize(windowHandle, pWidth, pHeight)

            val mode = glfwGetVideoMode(glfwGetPrimaryMonitor()) ?: throw RuntimeException(
                "Failed to get video mode for primary monitor"
            )

            glfwSetWindowPos(
                windowHandle,
                (mode.width() - pWidth.get(0)) / 2,
                (mode.height() - pHeight.get(0)) / 2
            )
        }

        glfwMakeContextCurrent(windowHandle)
        glfwSwapInterval(1)
        glfwShowWindow(windowHandle)

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

        if (System.getProperty("os.name").lowercase().contains("mac")) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        }

        Mouse.init(windowHandle)
    }

    private fun initContext() {
        GL.createCapabilities()

        ImGui.createContext()
        imGuiGlfw.init(windowHandle, true)
        imGuiGl3.init("#version 330")
    }

    private fun dispose() {
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
        ImGui.destroyContext()

        glfwTerminate()
        glfwSetErrorCallback(null)?.set()
    }

    companion object {
        fun launch(application: Application2D) {
            application.start()
        }
    }
}