package com.care.seredkin

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class WeatherController(private val weatherService: WeatherService) {

    @Get("/init")
    fun init() = weatherService.init()

    @Get("/weather/{cityAndCountry}")
    fun currentWeather(cityAndCountry: String) = weatherService.currentWeather(cityAndCountry)

    @Get("/weatherGroup/{ids}/{units}/{interval}", produces = ["text/event-stream"])
    fun currentWeatherInCities(ids: String, units: String, interval: Int) = weatherService
            .currentWeatherByIds(ids.csvToLongArray(), units, interval)

    @Get(value = "/stream/fake/{interval}", produces = ["text/event-stream"])
    fun fakeAll(interval: Int) = weatherService.fake(interval = interval)

     private fun String.csvToLongArray() = this.split(",").map { it.toLong() }.toLongArray()
}


