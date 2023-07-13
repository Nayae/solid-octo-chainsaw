package com.nayae.component.grid

import com.nayae.math.Matrix4
import com.nayae.math.Vector2
import org.lwjgl.opengl.GL33.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.properties.Delegates

class GridView(
    private val service: GridService
) {
    companion object {
        private const val CornerMode = 0
        private const val HighlightMode = 1
        private const val LineMode = 2
    }

    private val basePath = "C:\\Users\\Gino\\Desktop\\nayae\\src\\main\\resources"

    private val indices = intArrayOf(
        0, 1, 3,   // first triangle
        1, 2, 3    // second triangle
    )

    private var program by Delegates.notNull<Int>()

    private var eboGrid by Delegates.notNull<Int>()
    private var vaoGrid by Delegates.notNull<Int>()

    private var vboHl by Delegates.notNull<Int>()
    private var eboHl by Delegates.notNull<Int>()
    private var vaoHl by Delegates.notNull<Int>()

    private var vboLine by Delegates.notNull<Int>()
    private var eboLine by Delegates.notNull<Int>()
    private var vaoLine by Delegates.notNull<Int>()

    private var lastHighlightsHash = 0
    private var lastLinesHash = 0

    fun initialize() {
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

        // Grid
        vaoGrid = glGenVertexArrays()
        glBindVertexArray(vaoGrid)

        glVertexAttribDivisor(0, 1)

        eboGrid = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboGrid)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        // Highlights
        vaoHl = glGenVertexArrays()
        glBindVertexArray(vaoHl)

        vboHl = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboHl)
        glBufferData(GL_ARRAY_BUFFER, GridService.MaxHighlightCount * 5L * Float.SIZE_BYTES, GL_STREAM_DRAW)

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

        // Line
        vaoLine = glGenVertexArrays()
        glBindVertexArray(vaoLine)

        vboLine = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboLine)
        glBufferData(GL_ARRAY_BUFFER, GridService.MaxLineCount * 4L * Float.SIZE_BYTES, GL_STREAM_DRAW)

        glVertexAttribPointer(2, 4, GL_FLOAT, false, 4 * Float.SIZE_BYTES, 0)
        glEnableVertexAttribArray(2)

        glVertexAttribDivisor(2, 1)

        eboLine = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboLine)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    fun render() {
        glUseProgram(program)

        val info = service.getInfo()

        val currentHighlightsHash = info.highlights.hashCode()
        if (currentHighlightsHash != lastHighlightsHash) {
            updateCornerHighlights(info)
            lastHighlightsHash = currentHighlightsHash
        }

        val currentLinesHash = info.lines.hashCode()
        if (currentLinesHash != lastLinesHash) {
            updateLines(info)
            lastLinesHash = currentLinesHash
        }

        setUniform("uCountX", info.columns)
        setUniform("uCountY", info.rows)
        setUniform("uScale", info.tileSize)
        setUniform("uOffset", info.gridOffset)

        setUniform("uModel", Matrix4.createTranslation(-(info.width * 0.5f), (info.height * 0.5f), 0.0f))
        setUniform("uView", Matrix4.createTranslation(0.0f, 0.0f, -3.0f))
        setUniform("uProjection", Matrix4.createOrthographic(info.width, info.height, 0.1f, 100.0f))

        setUniform("uMode", LineMode)
        setUniform("uLineThickness", 1.0f)
        glBindVertexArray(vaoLine)
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, info.lines.size)

        setUniform("uMode", CornerMode)
        glBindVertexArray(vaoGrid)
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, (info.rows * info.columns).toInt())

        setUniform("uMode", HighlightMode)
        glBindVertexArray(vaoHl)
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, info.highlights.size)

        glBindVertexArray(0)
    }

    private fun updateLines(info: GridInfo) {
        val data = FloatArray(4 * info.lines.size)
        info.lines.forEachIndexed { i, line ->
            data[i * 4 + 0] = line.start.x
            data[i * 4 + 1] = line.start.y
            data[i * 4 + 2] = line.end.x
            data[i * 4 + 3] = line.end.y
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboLine)
        glBufferSubData(GL_ARRAY_BUFFER, 0, data)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    private fun updateCornerHighlights(info: GridInfo) {
        val data = FloatArray(5 * info.highlights.size)
        info.highlights.forEachIndexed { i, highlight ->
            data[i * 5 + 0] = highlight.position.x
            data[i * 5 + 1] = highlight.position.y
            data[i * 5 + 2] = highlight.color.x
            data[i * 5 + 3] = highlight.color.y
            data[i * 5 + 4] = highlight.color.z
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboHl)
        glBufferSubData(GL_ARRAY_BUFFER, 0, data)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }


    fun dispose() {
        glDeleteBuffers(vboHl)
        glDeleteBuffers(eboGrid)
        glDeleteBuffers(eboHl)
        glDeleteVertexArrays(vaoGrid)
        glDeleteVertexArrays(vaoHl)
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