package com.nayae.component.grid

import com.nayae.input.Mouse
import com.nayae.math.Vector2
import com.nayae.math.Vector3
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.round

class GridService {
    companion object {
        const val MaxHighlightCount = 1024
        const val MaxLineCount = 1024
    }

    private val gridInfo: GridInfo = GridInfo()
    private var lastHoverCorner: Vector2? = null

    fun update() {
        gridInfo.tileSize = 50.0f
        gridInfo.width = 1280.0f
        gridInfo.height = 768.0f

        val hoverRadius = 10.0f
        gridInfo.gridOffset = Vector2(gridInfo.width / 2.0f, gridInfo.height / 2.0f)

        gridInfo.columns = ceil(gridInfo.width / gridInfo.tileSize)
        gridInfo.rows = ceil(gridInfo.height / gridInfo.tileSize)

        val relativeMouseX = Mouse.position.x - gridInfo.gridOffset.x
        val relativeMouseY = Mouse.position.y - gridInfo.gridOffset.y
        val distanceX = abs(relativeMouseX % gridInfo.tileSize)
        val isWithinXRange = distanceX <= hoverRadius || distanceX >= (gridInfo.tileSize - hoverRadius)

        val distanceY = abs(relativeMouseY % gridInfo.tileSize)
        val isWithinYRange = distanceY <= hoverRadius || distanceY >= (gridInfo.tileSize - hoverRadius)

        if (isWithinXRange && isWithinYRange) {
            val hoveredX = round(relativeMouseX / gridInfo.tileSize)
            val hoveredY = round(relativeMouseY / gridInfo.tileSize)

            if (lastHoverCorner == null) {
                toggleCornerHighlight(hoveredX, hoveredY, Vector3(1.0f, 0.0f, 0.0f))
                lastHoverCorner = Vector2(hoveredX, hoveredY)

                gridInfo.lines.add(GridLine(Vector2.zero, Vector2(hoveredX, hoveredY)))
            }
        } else {
            lastHoverCorner?.let { position ->
                toggleCornerHighlight(position.x, position.y, Vector3.zero)
                lastHoverCorner = null
            }
        }
    }

    private fun toggleCornerHighlight(x: Float, y: Float, color: Vector3) {
        if (isCornerHighlighted(x, y)) {
            gridInfo.highlights.removeIf {
                it.position.x == x && it.position.y == y
            }
        } else {
            gridInfo.highlights.add(GridHighlight(Vector2(x, y), color))
        }

        if (gridInfo.highlights.size > MaxHighlightCount) {
            throw Exception("Cannot highlight more corners, max has been reached of $MaxHighlightCount")
        }
    }

    private fun isCornerHighlighted(x: Float, y: Float): Boolean {
        return gridInfo.highlights.any { it.position.x == x && it.position.y == y }
    }

    fun getInfo(): GridInfo {
        return gridInfo
    }
}