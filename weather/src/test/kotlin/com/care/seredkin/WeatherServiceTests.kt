package com.care.seredkin

import com.care.seredkin.WeatherService.Companion.METRIC
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import java.time.Instant.now
import kotlin.math.absoluteValue
import com.care.seredkin.Configuration.defaultCities as configuredCities

class WeatherServiceTests {
    private val weatherService = WeatherService(ObjectMapper().registerKotlinModule())

    @Test
    fun fetchFromOwmIntegrationTest() {
        val service = weatherService
        configuredCities.values.forEach {
            with(service.currentWeather(it)) {
                assert(this.main.temp.isFinite())
                assert(this.clouds.all >= 0)
                assert(this.dt.isBefore(now()))//make sure we're on time with the server
                assert(this.main.temp.absoluteValue < 250)//happens in case typos in 'units' parameter
                assert(configuredCities.any { str -> str.value == it })
            }
        }
    }

    @Test
    fun controllerIntegrationTest() {
        val controller = WeatherController(weatherService)
        val berlin = "Berlin"
        val waltham = "Waltham"
        val irkutsk = "Irkutsk"
        with(controller.currentWeather("$berlin,DE")) {
            assert(this.name == berlin)
        }
        with(controller.currentWeather("$waltham,US")) {
            assert(this.name == waltham)
        }
        with(controller.currentWeather("$irkutsk,RU")) {
            assert(this.name == irkutsk)
        }
    }

    @Test
    fun groupOfIdsRequestTest() {
        val controller = WeatherController(weatherService)
        val cityIds = Configuration.defaultCities.keys.joinToString(",")
        val cityMap = controller.currentWeatherInCities(cityIds, METRIC, 1).firstOrError().blockingGet()
        assert(cityMap.size == Configuration.defaultCities.size)
    }

}