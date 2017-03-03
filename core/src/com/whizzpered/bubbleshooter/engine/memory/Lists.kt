package com.whizzpered.bubbleshooter.engine.memory

/**
 * Created by phdeh on 03/03/2017.
 */

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