package com.github.xyzboom.dt.data

class Data(
    val x: List<Any>,
    val y: String
) {
    override fun toString(): String {
        return "Data(x=$x, y='$y')"
    }
}