package com.lollipop.wear.blocksbuilding.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import org.json.JSONArray
import org.json.JSONObject
import java.util.LinkedList

object ViewTreeTools {

    fun printViewTree(activity: Activity): JSONArray {
        val view = activity.findViewById<View>(android.R.id.content)
        val rootArray = JSONArray()
        val pendingList = LinkedList<ViewTreeInfo>()
        pendingList.add(ViewTreeInfo(rootArray, view))
        while (pendingList.isNotEmpty()) {
            val info = pendingList.removeFirst()
            info.print()
            pendingList.addAll(info.getChildren())
        }
        return printViewTree(activity.findViewById(android.R.id.content))
    }

    fun printViewTree(viewRoot: View): JSONArray {
        val rootArray = JSONArray()
        val pendingList = LinkedList<ViewTreeInfo>()
        pendingList.add(ViewTreeInfo(rootArray, viewRoot))
        while (pendingList.isNotEmpty()) {
            val info = pendingList.removeFirst()
            info.print()
            pendingList.addAll(info.getChildren())
        }
        return rootArray
    }

    private class ViewTreeInfo(private val outArray: JSONArray, private val view: View) {

        private val childrenArray = JSONArray()
        private val infoObject = JSONObject().apply {
            put("children", childrenArray)
        }

        fun print() {
            outArray.put(getViewInfo())
        }

        fun getChildren(): List<ViewTreeInfo> {
            val result = ArrayList<ViewTreeInfo>()
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    result.add(ViewTreeInfo(childrenArray, child))
                }
            }
            return result
        }

        private fun getViewInfo(): JSONObject {
            return infoObject.apply {
                val resId = view.id
                put(
                    "id", if (resId == View.NO_ID) {
                        "NO_ID"
                    } else {
                        view.resources.getResourceName(resId)
                    }
                )
                put("name", view.javaClass.name)
                put("description", view.contentDescription)
                put("size", JSONObject().apply {
                    put("width", view.width)
                    put("height", view.height)
                })
                put("location", JSONObject().apply {
                    put("x", view.left)
                    put("y", view.top)
                })
                put("padding", JSONObject().apply {
                    put("left", view.paddingLeft)
                    put("top", view.paddingTop)
                    put("right", view.paddingRight)
                    put("bottom", view.paddingBottom)
                })
            }
        }

    }

}