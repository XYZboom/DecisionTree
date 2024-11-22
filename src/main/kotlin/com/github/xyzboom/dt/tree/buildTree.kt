package com.github.xyzboom.dt.tree

import com.github.xyzboom.dt.data.Data
import com.github.xyzboom.dt.data.DataSet
import com.github.xyzboom.dt.dropAt
import com.github.xyzboom.dt.node.DTLeafNode
import com.github.xyzboom.dt.node.DTNode
import com.github.xyzboom.dt.node.DTNonLeafNode
import com.github.xyzboom.dt.strategy.ExactMatchStrategy
import kotlin.math.log2

fun buildTree(dataset: DataSet): DecisionTree {
    return DecisionTree(buildNode(dataset.dataList, dataset.propertyNames, "ID3"))
}

private val algorithmMap: Map<String, (dataList: List<Data>) -> Int> = buildMap {
    put("ID3", ::id3)
}

private fun buildNode(
    dataList: List<Data>,
    propertyNames: List<String>,
    algorithm: String,
    parent: DTNonLeafNode? = null
): DTNode {
    // 样本属于同一类别C
    if (dataList.map { it.y }.toSet().size == 1) {
        return DTLeafNode(dataList.first().y, parent)
    }
    // 属性集为空 或者 数据集中的样本在属性集上取值相同
    if (propertyNames.isEmpty()/* || dataList.*/) {
        val group = dataList.groupBy { it.y }.mapValues { it.value.size }
        val maxY = group.maxBy { it.value }.key
        return DTLeafNode(maxY, parent)
    }
    // 选择最优划分属性
    val chooseAlgorithm = algorithmMap[algorithm]
        ?: throw NoSuchElementException("No algorithm found for algorithm $algorithm")
    val chooseIndex = chooseAlgorithm(dataList)
    val groups = dataList.groupBy { it.x[chooseIndex] }
    val result = DTNonLeafNode(propertyNames[chooseIndex], parent = parent)
    for ((_, group) in groups.entries) {
        //   ^^^^^ Dv <- D中在a*中取值为av的样本子集
        val value = group.first().x[chooseIndex]
        val strategy = ExactMatchStrategy(value)
        result.strategies.add(strategy)
        val child = buildNode(
            group.map { Data(it.x.dropAt(chooseIndex), it.y) },
            propertyNames.dropAt(chooseIndex),
            algorithm, parent
        )
        result.children.add(child)
    }
    return result
}

fun ent(dataList: List<Data>): Double {
    val dataSize = dataList.size
    val group = dataList.groupBy { it.y }.mapValues { it.value.size }
    return -group.entries.sumOf {
        val p = it.value * 1.0 / dataSize
        p * log2(p)
    }
}

fun gainList(dataList: List<Data>): List<Double> {
    val ent = ent(dataList)
    val dataSize = dataList.size
    return dataList.first().x.indices.map { i ->
        val group = dataList.groupBy { it.x[i] }
        ent - group.entries.sumOf {
            val split = it.value
            (split.size * 1.0 / dataSize) * ent(split)
        }
    }
}

fun id3(dataList: List<Data>): Int {
    val gains = gainList(dataList)
    val max = gains.max()
    return gains.indexOf(max)
}
