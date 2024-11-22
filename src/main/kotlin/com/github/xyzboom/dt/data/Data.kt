package com.github.xyzboom.dt.data

class Data(
    val x: List<Any>,
    val y: String,
    val id: Int
) {
    override fun toString(): String {
        return "Data(x=$x, y='$y', id=$id)"
    }
}