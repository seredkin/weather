package com.care.seredkin

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("com.care.seredkin")
                .mainClass(Application.javaClass)
                .start()
    }
}