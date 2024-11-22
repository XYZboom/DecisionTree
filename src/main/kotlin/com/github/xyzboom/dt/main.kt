package com.github.xyzboom.dt

import com.github.xyzboom.dt.data.Data
import com.github.xyzboom.dt.data.DataSet
import com.github.xyzboom.dt.tree.buildTree
import com.github.xyzboom.dt.tree.save
import com.github.xyzboom.dt.tree.toGraphViz

fun main() {
    val classLoader = Thread.currentThread().contextClassLoader
    val rawLines = (classLoader.getResourceAsStream("waterlemon_1.txt")
        ?: throw NoSuchElementException("data not found")
            ).bufferedReader().use { it.readLines() }
    val propertyNames = rawLines[0].split(",").drop(1).dropLast(1)
    val dataList = mutableListOf<Data>()
    for ((index, rawLine) in rawLines.drop(1).withIndex()) {
        val rawProperties = rawLine.split(",").drop(1)
        val y = rawProperties.last()
        val properties = rawProperties.dropLast(1)
        dataList.add(Data(properties, y, index + 1))
    }
    println(dataList)
    val tree = buildTree(DataSet(dataList, propertyNames))
    tree.toGraphViz().save("tree1.png")
}