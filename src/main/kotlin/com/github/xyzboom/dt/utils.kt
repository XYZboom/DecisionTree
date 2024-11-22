package com.github.xyzboom.dt

fun <T> List<T>.dropAt(index: Int): List<T> {
    return take(index) + drop(index).takeLast(size - index - 1)
}