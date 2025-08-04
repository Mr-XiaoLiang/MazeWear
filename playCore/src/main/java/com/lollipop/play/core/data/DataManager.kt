package com.lollipop.play.core.data

import android.content.Context
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MJson
import com.lollipop.maze.data.MPath
import com.lollipop.maze.helper.doAsync
import com.lollipop.maze.helper.onUI
import com.lollipop.play.core.helper.registerLog
import org.json.JSONObject
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

object DataManager {

    private var mazeId = 0

    private const val KEY_MAZE_CACHE_DIR = "maze"

    private const val KEY_MAZE = "maze"
    private const val KEY_TIME = "time"
    private const val KEY_IS_COMPLETE = "is_complete"

    private var mazeCacheDir: File? = null

    private val mazeHistoryList = CopyOnWriteArrayList<MazeHistory>()

    private val listenerList = CopyOnWriteArrayList<DataChangeListener>()

    private val log = registerLog()

    private fun generateId(): Int {
        val id = mazeId++
        if (id == Int.MAX_VALUE) {
            mazeId = Int.MIN_VALUE
        }
        return id
    }

    private fun getCacheDir(context: Context): File {
        val cacheDir = mazeCacheDir
        if (cacheDir == null) {
            val newDir = File(context.filesDir, KEY_MAZE_CACHE_DIR)
            mazeCacheDir = newDir
            if (!newDir.exists()) {
                newDir.mkdirs()
            }
            return newDir
        }
        return cacheDir
    }

    private fun generateCacheFile(context: Context): File {
        return File(getCacheDir(context), "${System.currentTimeMillis().toString(16)}.maze")
    }

    fun load(context: Context, onEnd: () -> Unit) {
        doAsync(onError = {
            onUI {
                onLoadEnd()
                onEnd()
            }
        }) {
            val mazeList = mutableListOf<MazeHistory>()
            val cacheDir = getCacheDir(context)
            val listFiles = cacheDir.listFiles() ?: return@doAsync
            for (file in listFiles) {
                if (file.isFile) {
                    val mazeHistory = loadFile(file)
                    if (mazeHistory != null) {
                        mazeList.add(mazeHistory)
                    }
                }
            }
            mazeList.sortByDescending { it.lastTime }
            onUI {
                mazeHistoryList.clear()
                mazeHistoryList.addAll(mazeList)
                onEnd()
                onLoadEnd()
            }
        }
    }

    private fun onLoadEnd() {
        notifyLoaded()
    }

    fun findById(id: Int): MazeHistory? {
        return mazeHistoryList.firstOrNull { it.id == id }
    }

    fun findByFile(path: String): MazeHistory? {
        return mazeHistoryList.firstOrNull { it.cachePath == path }
    }

    fun update(
        context: Context,
        filePath: String,
        mazeMap: MazeMap,
        path: MPath,
        isComplete: Boolean,
        onEnd: () -> Unit
    ): File {
        val oldCache = findByFile(filePath)
        val mazeHistory = if (oldCache != null) {
            val info = MazeHistory(
                id = oldCache.id,
                cacheFile = oldCache.cacheFile,
                lastTime = System.currentTimeMillis(),
                isComplete = isComplete,
                maze = mazeMap,
                path = path
            )
            mazeHistoryList.remove(oldCache)
            info
        } else {
            MazeHistory(
                id = generateId(),
                cacheFile = generateCacheFile(context),
                lastTime = System.currentTimeMillis(),
                isComplete = isComplete,
                maze = mazeMap,
                path = path
            )
        }
        mazeHistoryList.add(0, mazeHistory)
        notifyChanged(mazeHistory.id)
        writeToFile(mazeHistory, onEnd)
        return mazeHistory.cacheFile
    }

    private fun writeToFile(
        info: MazeHistory,
        onEnd: () -> Unit
    ) {
        doAsync(
            onError = {
                onUI {
                    notifyChanged(info.id)
                    onEnd()
                }
            }
        ) {
            val jsonObj = JSONObject()
            jsonObj.put(KEY_TIME, info.lastTime)
            jsonObj.put(KEY_IS_COMPLETE, info.isComplete)
            jsonObj.put(KEY_MAZE, MJson.build(info.maze, info.path))
            val cacheFile = info.cacheFile
            cacheFile.parentFile?.mkdirs()
            cacheFile.writeText(jsonObj.toString())
            onUI {
                notifyChanged(info.id)
                onEnd()
            }
        }
    }

    private fun loadFile(file: File): MazeHistory? {
        return log.tryDo("loadFile") {
            val jsonObj = JSONObject(file.readText())
            val mazeObj = jsonObj.optJSONObject(KEY_MAZE) ?: return@tryDo null
            val mazeOut = MJson.parse(mazeObj) ?: return@tryDo null
            val lastTime = jsonObj.optLong(KEY_TIME, System.currentTimeMillis())
            val isComplete = jsonObj.optBoolean(KEY_IS_COMPLETE, false)
            MazeHistory(
                id = generateId(),
                cacheFile = file,
                lastTime = lastTime,
                maze = mazeOut.mazeMap,
                path = mazeOut.path,
                isComplete = isComplete
            )
        }
    }

    fun register(listener: DataChangeListener) {
        listenerList.add(listener)
    }

    fun unregister(listener: DataChangeListener) {
        listenerList.remove(listener)
    }

    private fun notifyChanged(mazeId: Int) {
        // 额外的onUI回调，主要用于跳出调用栈，避免一些情况下出现的死循环以及栈溢出
        onUI {
            for (listener in listenerList) {
                listener.onDataChanged(mazeId)
            }
        }
    }

    private fun notifyLoaded() {
        // 额外的onUI回调，主要用于跳出调用栈，避免一些情况下出现的死循环以及栈溢出
        onUI {
            for (listener in listenerList) {
                listener.onDataLoaded()
            }
        }
    }

    interface DataChangeListener {
        fun onDataChanged(mazeId: Int)

        fun onDataLoaded()
    }

}