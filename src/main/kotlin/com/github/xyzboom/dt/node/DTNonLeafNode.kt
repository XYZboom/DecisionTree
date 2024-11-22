package com.github.xyzboom.dt.node

import com.github.xyzboom.dt.strategy.IStrategy

class DTNonLeafNode(
    decision: String,
    val strategies: MutableList<IStrategy> = ArrayList(),
    val children: MutableList<DTNode> = ArrayList(),
    parent: DTNode?,
): DTNode(decision, parent)