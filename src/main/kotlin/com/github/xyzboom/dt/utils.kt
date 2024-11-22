package com.github.xyzboom.dt

fun <T> List<T>.dropAt(index: Int): List<T> {
    return drop(index).take(size - index - 1) + take(index)
}