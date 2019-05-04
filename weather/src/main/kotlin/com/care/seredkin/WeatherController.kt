package com.care.seredkin

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class WeatherController(private val weatherService: WeatherService) {

    @Get("/init")
    fun init() = weatherService.init()

    @Get("/weather/{country}/{city}")
    fun currentWeather(country: String, city: String) = weatherService.currentWeather(CityNameRequest(city, country.toUpperCase()))

    @Get("/weatherByCityId/{ids}/{units}/{interval}")
    fun currentWeatherInCities(ids: String, units: String, interval: Int) = weatherService
            .currentWeatherByIds(ids.csvToLongArray(), units, interval)

    @Get(value = "/stream/fake/{interval}", produces = ["text/event-stream"])
    fun fakeAll(interval: Int) = weatherService.fake(interval = interval)

    @Get(value = "/stream/weather/{cities}/{interval}/{units}", produces = ["text/event-stream"])
    fun findAll(cities: String, interval: Int, units: String) = weatherService
            .currentWeatherByIds(interval = interval, cityIds = cities.csvToLongArray(), units = units)

    private fun String.csvToLongArray() = this.split(",").map { it.toLong() }.toLongArray()
}


