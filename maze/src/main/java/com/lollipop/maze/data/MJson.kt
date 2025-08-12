package com.lollipop.maze.data

import com.lollipop.maze.Maze
import com.lollipop.maze.MazeMap
import org.json.JSONArray
import org.json.JSONObject

object MJson {

    const val UNKNOWN = "U"

    const val WALL = "W"
    const val ROAD = "R"
    const val START = "S"
    const val END = "E"

    const val MAP = "Map"
    const val PATH = "Path"

    const val X = "X"
    const val Y = "Y"

    const val WIDTH = "Width"
    const val HEIGHT = "Height"

    fun parse(json: JSONObject): ParseOut? {
        val width = json.optInt(WIDTH)
        val height = json.optInt(HEIGHT)
        val map = MMap(width, height)
        val mapJson = json.optJSONArray(MAP) ?: return null
        var startX = -1
        var startY = -1
        var endX = -1
        var endY = -1
        for (y in 0 until height) {
            val line = mapJson.optJSONArray(y) ?: continue
            for (x in 0 until width) {
                val key = line.optString(x)
                when (key) {
                    WALL -> {
                        map.wall(x, y)
                    }
                    ROAD -> {
                        map.road(x, y)
                    }
                    START -> {
                        map.road(x, y)
                        startX = x
                        startY = y
                    }

                    END -> {
                        map.road(x, y)
                        endX = x
                        endY = y
                    }
                }
            }
        }
        val path = MPath()
        val pathJson = json.optJSONArray(PATH)
        if (pathJson != null) {
            for (i in 0 until pathJson.length()) {
                val pointJson = pathJson.optJSONObject(i)
                if (pointJson != null) {
                    val x = pointJson.optInt(X)
                    val y = pointJson.optInt(Y)
                    path.put(MPoint(x, y))
                }
            }
        }
        return ParseOut(
            mazeMap = MazeMap(
                start = MPoint(x = startX, y = startY),
                end = MPoint(x = endX, y = endY),
                map = map
            ),
            path = path
        )
    }

    fun build(mazeMap: MazeMap, path: MPath): JSONObject {
        val map = mazeMap.map
        val mapBuilder = JsonBuilder()
        for (y in 0 until mazeMap.height) {
            for (x in 0 until mazeMap.width) {
                if (x == mazeMap.start.x && y == mazeMap.start.y) {
                    mapBuilder.addStart()
                } else if (x == mazeMap.end.x && y == mazeMap.end.y) {
                    mapBuilder.addEnd()
                } else {
                    when (map[x, y]) {
                        Maze.ROAD -> {
                            mapBuilder.addRoad()
                        }

                        Maze.WALL -> {
                            mapBuilder.addWall()
                        }

                        else -> {
                            mapBuilder.addUnknown()
                        }
                    }
                }
            }
            mapBuilder.newLine()
        }
        val pathBuilder = JSONArray()
        for (point in path.pointList) {
            val pointJson = JSONObject()
            pointJson.put(X, point.x)
            pointJson.put(Y, point.y)
            pathBuilder.put(pointJson)
        }

        val config = JSONObject()
        config.put(MAP, mapBuilder.json)
        config.put(PATH, pathBuilder)
        config.put(WIDTH, mazeMap.width)
        config.put(HEIGHT, mazeMap.height)
        return config
    }

    private class JsonBuilder() {

        val json = JSONArray()
        private var currentLine: JSONArray? = null

        private fun getLine(): JSONArray {
            val line = currentLine
            if (line == null) {
                val newArray = JSONArray()
                currentLine = newArray
                json.put(newArray)
                return newArray
            } else {
                return line
            }
        }

        fun addWall() {
            getLine().put(WALL)
        }

        fun addRoad() {
            getLine().put(ROAD)
        }

        fun addUnknown() {
            getLine().put(UNKNOWN)
        }

        fun addStart() {
            getLine().put(START)
        }

        fun addEnd() {
            getLine().put(END)
        }

        fun newLine() {
            currentLine = null
        }

    }

    class ParseOut(
        val mazeMap: MazeMap,
        val path: MPath
    )

}