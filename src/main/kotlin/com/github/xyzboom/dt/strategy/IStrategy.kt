package com.github.xyzboom.dt.strategy

fun interface IStrategy {
    fun match(property: Any): Boolean
}