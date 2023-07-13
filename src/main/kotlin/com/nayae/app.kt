package com.nayae

import com.nayae.gfx.Application2D
import com.nayae.input.Mouse
import com.nayae.math.Matrix4
import com.nayae.math.Vector2
import org.lwjgl.opengl.GL33.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.properties.Delegates


object Application : Application2D() {
    private const val basePath = "C:\\Users\\Gino\\Desktop\\nayae\\src\\main\\resources"

    private val indices = intArrayOf(
        0, 1, 3,   // first triangle
        1, 2, 3    // second triangle
    )

    private val hightlights = floatArrayOf(
        1.0f, 1.0f, 1.0f, 0.0f, 0.0f
    )

    private var gridOffset = Vector2(100.0f)

    private var program by Delegates.notNull<Int>()

    private var eboGrid by Delegates.notNull<Int>()
    private var vaoGrid by Delegates.notNull<Int>()

    private var vboHl by Delegates.notNull<Int>()
    private var eboHl by Delegates.notNull<Int>()
    private var vaoHl by Delegates.notNull<Int>()

    override fun beforeRun() {
        val vertexShader = compileShader(GL_VERTEX_SHADER, "sample.vert.glsl")
        val fragmentShader = compileShader(GL_FRAGMENT_SHADER, "sample.frag.glsl")

        program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)

        val info = glGetProgramInfoLog(program)
        if (!info.isNullOrBlank()) {
            throw Exception("Failed to link shader: $info")
        }

        glDetachShader(program, vertexShader)
        glDetachShader(program, fragmentShader)

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)

        vaoGrid = glGenVertexArrays()
        glBindVertexArray(vaoGrid)

        eboGrid = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboGrid)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0)
        glEnableVertexAttribArray(0)
        glVertexAttribDivisor(0, 1)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        vaoHl = glGenVertexArrays()
        glBindVertexArray(vaoHl)

        vboHl = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboHl)
        glBufferData(GL_ARRAY_BUFFER, hightlights, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 5 * Float.SIZE_BYTES, 0)
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 5 * Float.SIZE_BYTES, 8)
        glEnableVertexAttribArray(1)

        glVertexAttribDivisor(0, 1)
        glVertexAttribDivisor(1, 1)

        eboHl = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboHl)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    override fun process() {
        glUseProgram(program)

        val width = 1280.0f
        val height = 768.0f
        val scale = 50.0f
        val hoverRadius = 10.0f

        val countX = ceil(width / scale)
        val countY = ceil(height / scale)
        val totalCount = (countX * countY).toInt()

        val relativeMouseX = Mouse.position.x - gridOffset.x
        val relativeMouseY = Mouse.position.y - gridOffset.y
        val distanceX = relativeMouseX % scale
        val isWithinXRange = distanceX <= hoverRadius || distanceX >= (scale - hoverRadius)

        val distanceY = relativeMouseY % scale
        val isWithinYRange = distanceY <= hoverRadius || distanceY >= (scale - hoverRadius)

        if (isWithinXRange && isWithinYRange) {
            val hoveredX = (relativeMouseX / scale).roundToInt()
            val hoveredY = (relativeMouseY / scale).roundToInt()

            println("x: $hoveredX, y: $hoveredY")
        }

        setUniform("uMode", 0)
        setUniform("uCountX", countX)
        setUniform("uCountY", countY)
        setUniform("uScale", scale)
        setUniform("uOffset", gridOffset)

        setUniform("uModel", Matrix4.createTranslation(-(width * 0.5f), (height * 0.5f), 0.0f))
//        setUniform("uModel", Matrix4.identity())
        setUniform("uView", Matrix4.createTranslation(0.0f, 0.0f, -3.0f))
        setUniform("uProjection", Matrix4.createOrthographic(width, height, 0.1f, 100.0f))

        setUniform("uMode", 0)
        glBindVertexArray(vaoHl)
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, totalCount)

        setUniform("uMode", 1)
        glBindVertexArray(vaoHl)
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, 1)
    }

    override fun afterRun() {
        glDeleteProgram(program)
    }

    private fun compileShader(type: Int, path: String): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, Files.readString(Path.of(basePath, path)))
        glCompileShader(shader)

        val info = glGetShaderInfoLog(shader)
        if (!info.isNullOrBlank()) {
            throw Exception("Failed to compile shader: $info")
        }

        return shader
    }

    private fun setUniform(name: String, transform: Matrix4) {
        glUniformMatrix4fv(glGetUniformLocation(program, name), false, transform.data)
    }

    private fun setUniform(name: String, value: Float) {
        glUniform1f(glGetUniformLocation(program, name), value)
    }

    private fun setUniform(name: String, value: Int) {
        glUniform1i(glGetUniformLocation(program, name), value)
    }

    private fun setUniform(name: String, value: Vector2) {
        glUniform2fv(glGetUniformLocation(program, name), value.data)
    }
}

fun main() {
    Application2D.launch(Application)
}

