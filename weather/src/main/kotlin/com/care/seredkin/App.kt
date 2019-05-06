package com.care.seredkin

import io.micronaut.runtime.Micronaut

object App {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build().packages("com.care.seredkin").mainClass(App.javaClass).start() }
}