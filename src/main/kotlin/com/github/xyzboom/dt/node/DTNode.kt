package com.github.xyzboom.dt.node

sealed class DTNode(
    val decision: Any,
    val parent: DTNode?
)

