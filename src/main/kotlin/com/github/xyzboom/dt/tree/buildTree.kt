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
    return DecisionTree(buildNode(dataset.dataList, dataset.dataList, dataset.propertyNames, "ID3"))
}

private val algorithmMap: Map<String, (dataList: List<Data>) -> Int> = buildMap {
    put("ID3", ::id3)
}

private fun buildNode(
    dataList: List<Data>,
    splitDataList: List<Data>,
    propertyNames: List<String>,
    algorithm: String,
    parent: DTNonLeafNode? = null
): DTNode {
    // 样本属于同一类别C
    if (splitDataList.map { it.y }.toSet().size == 1) {
        return DTLeafNode(splitDataList.first().y, parent)
    }
    // 属性集为空 或者 数据集中的样本在属性集上取值相同
    if (propertyNames.isEmpty() || splitDataList.map { it.x }.toSet().size == 1) {
        val group = splitDataList.groupBy { it.y }.mapValues { it.value.size }
        val maxY = group.maxBy { it.value }.key
        return DTLeafNode(maxY, parent)
    }
    // 选择最优划分属性
    val chooseAlgorithm = algorithmMap[algorithm]
        ?: throw NoSuchElementException("No algorithm found for algorithm $algorithm")
    val chooseIndex = chooseAlgorithm(splitDataList)
    val groups = splitDataList.groupBy { it.x[chooseIndex] }
    val result = DTNonLeafNode(propertyNames[chooseIndex], parent = parent)
    val availableProperties = dataList.map { it.x[chooseIndex] }.toSet()
    for (property in availableProperties) {
        val group = groups[property]
        //  ^^^^^ Dv <- D中在a*中取值为av的样本子集
        val strategy = ExactMatchStrategy(property)
        result.strategies.add(strategy)
        if (group == null) {
            val group1 = splitDataList.groupBy { it.y }.mapValues { it.value.size }
            val maxY = group1.maxBy { it.value }.key
            val child = DTLeafNode(maxY, result)
            result.children.add(child)
            continue
        }
        val child = buildNode(
            dataList.map { Data(it.x.dropAt(chooseIndex), it.y, it.id) },
            group.map { Data(it.x.dropAt(chooseIndex), it.y, it.id) },
            propertyNames.dropAt(chooseIndex),
            algorithm, result
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
