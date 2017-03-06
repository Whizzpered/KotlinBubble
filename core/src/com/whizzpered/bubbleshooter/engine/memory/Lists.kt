package com.whizzpered.bubbleshooter.engine.memory

fun <T, G> makeMutableListFrom(collection: Iterable<G>, convertor: (G) -> T): MutableList<T> {
    val m = mutableListOf<T>()
    collection.forEach { m += convertor(it) }
    return m
}

fun <T, G> makeListFrom(collection: Iterable<G>, convertor: (G) -> T): List<T> =
        makeMutableListFrom(collection, convertor).toList()

fun <T, G> makeMutableListFrom(collection: Array<G>, convertor: (G) -> T): MutableList<T> {
    val m = mutableListOf<T>()
    collection.forEach { m += convertor(it) }
    return m
}

fun <T, G> makeListFrom(collection: Array<G>, convertor: (G) -> T): List<T> =
        makeMutableListFrom(collection, convertor).toList()

fun <T, G : Comparable<G>> Iterable<T>.maxBy(by: (T) -> G): T? {
    val i = this.iterator()
    if (!i.hasNext()) {
        return null
    } else {
        var g = i.next()
        var u = by(g)
        while (i.hasNext()) {
            val f = i.next()
            val v = by(f)
            if ((u == null) || (v != null && v > u)) {
                g = f
                u = v
            }
        }
        return g
    }
}

fun <T, G : Comparable<G>> Iterable<T>.minBy(by: (T) -> G): T? {
    val i = this.iterator()
    if (!i.hasNext()) {
        return null
    } else {
        var g = i.next()
        var u = by(g)
        while (i.hasNext()) {
            val f = i.next()
            val v = by(f)
            if ((u == null) || (v != null && v < u)) {
                g = f
                u = v
            }
        }
        return g
    }
}