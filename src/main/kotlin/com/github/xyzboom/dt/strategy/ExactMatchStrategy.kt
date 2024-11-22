package com.github.xyzboom.dt.strategy

class ExactMatchStrategy(private val expected: Any): IStrategy {
    override fun match(property: Any): Boolean {
        return expected == property
    }

    override fun toString(): String {
        return "ExactMatch: $expected"
    }
}