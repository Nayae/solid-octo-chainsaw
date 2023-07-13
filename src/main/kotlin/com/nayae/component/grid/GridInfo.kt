package com.nayae.component.grid

import com.nayae.math.Vector2

data class GridInfo(
    var width: Float = 0.0f,
    var height: Float = 0.0f,

    var columns: Float = 0.0f,
    var rows: Float = 0.0f,
    var tileSize: Float = 0.0f,
    var gridOffset: Vector2 = Vector2.zero,

    var highlights: ArrayList<GridHighlight> = arrayListOf(),
    var lines: ArrayList<GridLine> = arrayListOf()
)