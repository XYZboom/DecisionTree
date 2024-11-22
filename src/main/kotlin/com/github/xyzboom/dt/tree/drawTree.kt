package com.github.xyzboom.dt.tree

import com.github.xyzboom.dt.node.DTLeafNode
import com.github.xyzboom.dt.node.DTNode
import com.github.xyzboom.dt.node.DTNonLeafNode
import guru.nidi.graphviz.attribute.Font
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Shape
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.model.MutableNode
import guru.nidi.graphviz.toGraphviz
import java.io.File
import javax.imageio.ImageIO

fun DecisionTree.toGraphViz(): MutableGraph {
    return graph(directed = true) {
        node[Font.name("Microsoft YaHei")]
        edge[Font.name("Microsoft YaHei")]
        val deque = ArrayDeque<DTNode>()
        deque.add(root)
        val nodeMap = HashMap<DTNode, MutableNode>()
        val nodeIdMap = HashMap<DTNode, Int>()
        var idNow = 0
        while (deque.isNotEmpty()) {
            val now = deque.removeFirst()
            val shape = when (now) {
                is DTLeafNode -> Shape.CIRCLE
                is DTNonLeafNode -> {
                    for (child in now.children) {
                        deque.add(child)
                    }
                    Shape.BOX
                }
            }
            val gNode = idNow.toString()[shape, Label.of(now.decision.toString())]
            nodeMap[now] = gNode
            nodeIdMap[now] = idNow
            idNow++
        }
        deque.add(root)
        while (deque.isNotEmpty()) {
            when (val now = deque.removeFirst()) {
                is DTLeafNode -> {}
                is DTNonLeafNode -> {
                    val nowGNode = nodeMap[now]!!
                    for ((child, strategy) in now.children.zip(now.strategies)) {
                        deque.add(child)
                        val childGNode = nodeMap[child]!!
                        (nowGNode - childGNode)[Label.of(strategy.toString())]
                    }
                }
            }
        }
    }
}

fun MutableGraph.save(path: String) {
    val img = toGraphviz().render(Format.PNG).toImage()
    ImageIO.write(img, "png", File(path))
}