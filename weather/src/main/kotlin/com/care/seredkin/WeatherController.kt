package com.care.seredkin

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.reactivex.Flowable

@Controller
class WeatherController(private val weatherService: WeatherService) {


    @Get("/weather/{country}/{city}")
    fun currentWeather(country: String, city: String) = weatherService.currentWeather(City(city, country))


    @Get("/stream/weather")
    fun findAll(): Flowable<CityWeatherResponse> = weatherService.findAll()

}


