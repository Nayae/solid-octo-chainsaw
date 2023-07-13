package com.nayae

import com.nayae.gfx.Application2D
import com.nayae.input.Mouse
import com.nayae.math.Matrix4
import com.nayae.math.Vector2
import com.nayae.math.Vector3
import org.lwjgl.opengl.GL33.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.properties.Delegates


object Application : Application2D() {
    private const val MaxHighlightCount = 1024
    private const val gridTileSize = 50.0f

    private const val basePath = "C:\\Users\\Gino\\Desktop\\nayae\\src\\main\\resources"

    private val indices = intArrayOf(
        0, 1, 3,   // first triangle
        1, 2, 3    // second triangle
    )

    private val highlights = arrayListOf<Pair<Vector2, Vector3>>()

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

        glVertexAttribDivisor(0, 1)

        eboGrid = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboGrid)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        vaoHl = glGenVertexArrays()
        glBindVertexArray(vaoHl)

        vboHl = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboHl)
        glBufferData(GL_ARRAY_BUFFER, MaxHighlightCount * 5L * Float.SIZE_BYTES, GL_STREAM_DRAW)

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

    private var lastFrameHover: Pair<Int, Int>? = null

    override fun process() {
        glUseProgram(program)

        val width = 1280.0f
        val height = 768.0f
        val hoverRadius = 10.0f

        val countX = ceil(width / gridTileSize)
        val countY = ceil(height / gridTileSize)
        val totalCount = (countX * countY).toInt()

        val relativeMouseX = Mouse.position.x - gridOffset.x
        val relativeMouseY = Mouse.position.y - gridOffset.y
        val distanceX = relativeMouseX % gridTileSize
        val isWithinXRange = distanceX <= hoverRadius || distanceX >= (gridTileSize - hoverRadius)

        val distanceY = relativeMouseY % gridTileSize
        val isWithinYRange = distanceY <= hoverRadius || distanceY >= (gridTileSize - hoverRadius)

        if (isWithinXRange && isWithinYRange) {
            val hoveredX = (relativeMouseX / gridTileSize).roundToInt()
            val hoveredY = (relativeMouseY / gridTileSize).roundToInt()

            if (lastFrameHover == null) {
                toggleCornerHighlight(hoveredX, hoveredY, Vector3(1.0f, 0.0f, 0.0f))
                lastFrameHover = Pair(hoveredX, hoveredY)
            }
        } else {
            if (lastFrameHover != null) {
                toggleCornerHighlight(lastFrameHover!!.first, lastFrameHover!!.second, Vector3.zero)
                lastFrameHover = null
            }
        }

        setUniform("uMode", 0)
        setUniform("uCountX", countX)
        setUniform("uCountY", countY)
        setUniform("uScale", gridTileSize)
        setUniform("uOffset", gridOffset)

        setUniform("uModel", Matrix4.createTranslation(-(width * 0.5f), (height * 0.5f), 0.0f))
//        setUniform("uModel", Matrix4.identity())
        setUniform("uView", Matrix4.createTranslation(0.0f, 0.0f, -3.0f))
        setUniform("uProjection", Matrix4.createOrthographic(width, height, 0.1f, 100.0f))

        setUniform("uMode", 0)
        glBindVertexArray(vaoGrid)
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, totalCount)

        setUniform("uMode", 1)
        glBindVertexArray(vaoHl)
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, highlights.size)

        glBindVertexArray(0)
    }

    override fun afterRun() {
        glDeleteBuffers(vboHl)
        glDeleteBuffers(eboGrid)
        glDeleteBuffers(eboHl)
        glDeleteVertexArrays(vaoGrid)
        glDeleteVertexArrays(vaoHl)
        glDeleteProgram(program)
    }

    private fun toggleCornerHighlight(x: Int, y: Int, color: Vector3) {
        if (isCornerHighlighted(x, y)) {
            highlights.removeIf {
                it.first.x == x.toFloat() && it.first.y == y.toFloat()
            }
        } else {
            highlights.add(Pair(Vector2(x.toFloat(), y.toFloat()), color))
        }

        if (highlights.size > MaxHighlightCount) {
            throw Exception("Cannot highlight more corners, max has been reached of $MaxHighlightCount")
        }

        val data = FloatArray(5 * highlights.size)
        highlights.forEachIndexed { i, e ->
            data[i * 5 + 0] = e.first.x
            data[i * 5 + 1] = e.first.y
            data[i * 5 + 2] = e.second.x
            data[i * 5 + 3] = e.second.y
            data[i * 5 + 4] = e.second.z
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboHl)
        glBufferSubData(GL_ARRAY_BUFFER, 0, data)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    private fun isCornerHighlighted(x: Int, y: Int): Boolean {
        return highlights.any { it.first.x == x.toFloat() && it.first.y == y.toFloat() }
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

